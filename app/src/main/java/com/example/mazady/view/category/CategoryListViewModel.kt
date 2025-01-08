package com.example.mazady.view.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mazady.data.repository.Repository
import com.example.mazady.models.Category
import com.example.mazady.models.CategoryOtherPropertyClick
import com.example.mazady.models.CategoryProperty
import com.example.mazady.models.CategoryPropertyClick
import com.example.mazady.models.CategoryPropertyInput
import com.example.mazady.models.CategoryPropertyListItem
import com.example.mazady.models.Error
import com.example.mazady.models.ItemClickAction
import com.example.mazady.models.ListItem
import com.example.mazady.models.MainCategoryClick
import com.example.mazady.models.MainCategoryListItem
import com.example.mazady.models.PropertyOption
import com.example.mazady.models.ScreenState
import com.example.mazady.models.SubCategoryClick
import com.example.mazady.models.SubCategoryListItem
import com.example.mazady.models.Success
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryListViewModel(private val repository: Repository) : ViewModel() {

    private val _userSelectionFlow = MutableStateFlow<List<ListItem>>(emptyList())
    val userSelectionFlow = _userSelectionFlow.asStateFlow()
    private val userSelectionState get() = _userSelectionFlow.value

    private val _screenStateFlow = MutableStateFlow(ScreenState())
    val screenStateFlow = _screenStateFlow.asStateFlow()
    private val screenState get() = _screenStateFlow.value

    init {
        viewModelScope.launch {
            _screenStateFlow.emit(screenState.copy(isLoading = true))
            when (val result = repository.getAllCategories()) {
                is Success -> {
                    _screenStateFlow.emit(screenState.copy(isLoading = false))
                    _userSelectionFlow.emit(listOf(MainCategoryListItem(result.data, -1)))
                }

                is Error -> {
                    _screenStateFlow.emit(
                        screenState.copy(
                            isLoading = false,
                            error = result.error.message
                        )
                    )
                }
            }
        }
    }

    fun validateData(): Boolean {
        var hasOneError = false
        val modifiedList = userSelectionState.map {
            if (!hasOneError) hasOneError = it.hasError
            when(it) {
                is MainCategoryListItem -> it.copy(categoryError = it.hasError)
                is CategoryPropertyListItem -> it.copy(propertyError = it.hasError)
                is SubCategoryListItem -> it.copy(categoryError = it.hasError)
            }
        }
        if (!hasOneError) repository.submitItems(modifiedList)
        viewModelScope.launch {
            _userSelectionFlow.emit(modifiedList)
        }
        return !hasOneError
    }

    fun onItemClicked(clickAction: ItemClickAction) {
        when (clickAction) {
            is MainCategoryClick -> onItemSelected(clickAction)
            is SubCategoryClick -> onItemSelected(clickAction)
            is CategoryPropertyClick -> onItemSelected(clickAction)
            is CategoryPropertyInput -> onPropertyInput(clickAction)
            is CategoryOtherPropertyClick -> onItemSelected(clickAction)
        }
    }

    private fun onItemSelected(clickAction: MainCategoryClick) {
        viewModelScope.launch {
            // update main category selection index
            var categoryListItem = userSelectionState.firstOrNull() as? MainCategoryListItem
                ?: return@launch
            // if the same category clicked, do nothing
            if (clickAction.index == categoryListItem.selectionIndex) return@launch

            categoryListItem = categoryListItem.copy(selectionIndex = clickAction.index, categoryError = false)
            val subCategories =
                clickAction.category.children ?: return@launch
            _userSelectionFlow.emit(
                listOf(categoryListItem, SubCategoryListItem(subCategories, -1))
            )
            _screenStateFlow.emit(screenState.copy(submitButtonVisible = false))
        }
    }

    private fun onItemSelected(click: SubCategoryClick) {
        viewModelScope.launch {
            // update subCategory selection index
            var subCategoryListItem =
                userSelectionState.firstOrNull { it is SubCategoryListItem } as? SubCategoryListItem
                    ?: return@launch

            // if the same category clicked, do nothing
            if (click.index == subCategoryListItem.selectionIndex) return@launch

            subCategoryListItem = subCategoryListItem.copy(selectionIndex = click.index, categoryError = false)
            val propertiesItems =
                getPropertiesForSubCategory(click.category).map { CategoryPropertyListItem(it, -1) }
            val mainCategory = userSelectionState.firstOrNull() as? MainCategoryListItem
                ?: return@launch
            _userSelectionFlow.emit(
                (listOf(mainCategory, subCategoryListItem) + propertiesItems)
            )
            _screenStateFlow.emit(screenState.copy(submitButtonVisible = true))
        }
    }

    private fun onItemSelected(click: CategoryPropertyClick) {
        viewModelScope.launch {

            val selectedOption = click.categoryProperty.options?.get(click.index)
                ?: return@launch
            val currentPropertyIndex = userSelectionState.indexOfFirst {
                it is CategoryPropertyListItem && it.data == click.categoryProperty
            }
            if (currentPropertyIndex == -1) return@launch
            var currentPropertyState =
                userSelectionState[currentPropertyIndex] as? CategoryPropertyListItem
                    ?: return@launch

            // if the same option clicked, do nothing
            if (currentPropertyState.selectionIndex == click.index) return@launch

            // check if unselected option has children
            var itemsToRemove: List<ListItem> = emptyList()
            if (currentPropertyState.selectionIndex != -1) {
                val unselectedOption =
                    currentPropertyState.data.options?.getOrNull(currentPropertyState.selectionIndex)
                if (unselectedOption?.id != null) {
                    // remove child options
                    itemsToRemove = getChildrenRecursively(unselectedOption.id)
                }
            }

            // updating selection state
            currentPropertyState = currentPropertyState.copy(selectionIndex = click.index, propertyError = false)

            // does selected options have children
            val childProperties =
                getChildOptions(selectedOption).map { CategoryPropertyListItem(it, -1) }
            val modifiedSelections = userSelectionState.toMutableList()
            modifiedSelections[currentPropertyIndex] = currentPropertyState
            if (itemsToRemove.isNotEmpty()) modifiedSelections.removeAll(itemsToRemove)
            modifiedSelections.addAll(currentPropertyIndex + 1, childProperties)
            _userSelectionFlow.emit(modifiedSelections)

        }
    }

    private fun getChildrenRecursively(optionId: Int): List<ListItem> {
        val result: MutableList<ListItem> = mutableListOf()
        userSelectionState.filterIsInstance<CategoryPropertyListItem>().forEach {
            if (it.data.parentId == optionId) {
                val children = it.data.options?.mapNotNull { option ->
                    val id = option.id ?: return@mapNotNull null
                    getChildrenRecursively(id)
                }?.flatten() ?: emptyList()
                result.addAll(listOf(it) + children)
            }
        }
        return result
    }

    private fun onPropertyInput(click: CategoryPropertyInput) {
        val currentPropertyIndex = userSelectionState.indexOfFirst {
            it is CategoryPropertyListItem && it.data == click.categoryProperty
        }
        var currentPropertyState =
            userSelectionState[currentPropertyIndex] as? CategoryPropertyListItem
                ?: return

        currentPropertyState = currentPropertyState.copy(inputText = click.inputText, propertyError = false)
        val modifiedSelections = userSelectionState.toMutableList()
        modifiedSelections[currentPropertyIndex] = currentPropertyState
        viewModelScope.launch {
            _userSelectionFlow.emit(modifiedSelections)
        }
    }

    private fun onItemSelected(clickAction: CategoryOtherPropertyClick) {
        val currentPropertyIndex = userSelectionState.indexOfFirst {
            it is CategoryPropertyListItem && it.data == clickAction.categoryProperty
        }
        val otherOption = clickAction.categoryProperty.options?.first { it.slug == "other" }
            ?: return

        // checking if other property already added
        if (userSelectionState.any { it is CategoryPropertyListItem && it.data.parentId == otherOption.id }) return

        val addedProperty = createOtherProperty(otherOption, clickAction.categoryProperty.name)
        val addedPropertyListItem = CategoryPropertyListItem(addedProperty, clickAction.index)
        val modifiedSelections = userSelectionState.toMutableList()
        modifiedSelections[currentPropertyIndex] =
            (modifiedSelections[currentPropertyIndex] as CategoryPropertyListItem).copy(
                selectionIndex = clickAction.index, propertyError = false
            )
        modifiedSelections.add(currentPropertyIndex + 1, addedPropertyListItem)
        viewModelScope.launch {
            _userSelectionFlow.emit(modifiedSelections)
        }
    }

    private fun createOtherProperty(
        options: PropertyOption,
        parentName: String?
    ): CategoryProperty {
        return CategoryProperty(
            slug = "other",
            name = "User input $parentName",
            parentId = options.id ?: 0,
            options = null,
            id = null,
            image = null,
            description = null,
        )
    }

    private suspend fun getPropertiesForSubCategory(subCategory: Category): List<CategoryProperty> {
        val id = subCategory.id ?: return emptyList()
        _screenStateFlow.emit(screenState.copy(isLoading = true))
        return when (val result = repository.getCategoryProperties(id)) {
            is Success -> {
                _screenStateFlow.emit(screenState.copy(isLoading = false))
                result.data
            }

            is Error -> {
                _screenStateFlow.emit(
                    screenState.copy(
                        isLoading = false,
                        error = result.error.message
                    )
                )
                emptyList()
            }
        }
    }

    private suspend fun getChildOptions(option: PropertyOption): List<CategoryProperty> {
        val id = option.id ?: return emptyList()
        _screenStateFlow.emit(screenState.copy(isLoading = true))
        return when (val result = repository.getOptionsChild(id)) {
            is Success -> {
                _screenStateFlow.emit(screenState.copy(isLoading = false))
                result.data
            }
            is Error -> {
                _screenStateFlow.emit(
                    screenState.copy(
                        isLoading = false,
                        error = result.error.message
                    )
                )
                emptyList()
            }
        }
    }

}