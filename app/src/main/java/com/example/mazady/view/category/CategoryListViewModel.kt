package com.example.mazady.view.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mazady.data.repository.Repository
import com.example.mazady.models.Category
import com.example.mazady.models.CategoryProperty
import com.example.mazady.models.CategoryPropertyClick
import com.example.mazady.models.CategoryPropertyListItem
import com.example.mazady.models.Error
import com.example.mazady.models.ItemClickAction
import com.example.mazady.models.ListItem
import com.example.mazady.models.MainCategoryClick
import com.example.mazady.models.MainCategoryListItem
import com.example.mazady.models.PropertyOption
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

    private val _categoriesFlow = MutableStateFlow(emptyList<Category>())
    val categoriesFlow = _categoriesFlow.asStateFlow()

    init {
        viewModelScope.launch {
            when (val result = repository.getAllCategories()) {
                is Success -> {
                    _userSelectionFlow.emit(listOf(MainCategoryListItem(result.data, -1)))
                }

                is Error -> {}
            }
        }
    }

    fun onItemClicked(clickAction: ItemClickAction) {
        when (clickAction) {
            is MainCategoryClick -> onItemSelected(clickAction)
            is SubCategoryClick -> onItemSelected(clickAction)
            is CategoryPropertyClick -> onItemSelected(clickAction)
        }
    }

    private fun onItemSelected(clickAction: MainCategoryClick) {
        viewModelScope.launch {
            // update main category selection index
            var categoryListItem = userSelectionState.firstOrNull() as? MainCategoryListItem
                ?: return@launch //TODO handle this case
            categoryListItem = categoryListItem.copy(selectionIndex = clickAction.index)
            val subCategories =
                clickAction.category.children ?: return@launch //TODO handle this case
            _userSelectionFlow.emit(
                listOf(categoryListItem, SubCategoryListItem(subCategories, -1))
            )
        }
    }

    private fun onItemSelected(click: SubCategoryClick) {
        viewModelScope.launch {
            // update subCategory selection index
            var subCategoryListItem =
                userSelectionState.firstOrNull { it is SubCategoryListItem } as? SubCategoryListItem
                    ?: return@launch //TODO handle this case
            subCategoryListItem = subCategoryListItem.copy(selectionIndex = click.index)
            val propertiesItems =
                getPropertiesForSubCategory(click.category).map { CategoryPropertyListItem(it, -1) }
            val mainCategory = userSelectionState.firstOrNull() as? MainCategoryListItem
                ?: return@launch //TODO handle this case
            _userSelectionFlow.emit(
                (listOf(mainCategory, subCategoryListItem) + propertiesItems)
            )
        }
    }

    private fun onItemSelected(click: CategoryPropertyClick) {
        viewModelScope.launch {
            val selectedOption = click.categoryProperty.options?.get(click.index)
                ?: return@launch //TODO handle this case
            val currentPropertyIndex = userSelectionState.indexOfFirst {
                it is CategoryPropertyListItem && it.data == click.categoryProperty
            }
            if (currentPropertyIndex == -1) return@launch //TODO handle this case
            var currentPropertyState =
                userSelectionState[currentPropertyIndex] as? CategoryPropertyListItem
                    ?: return@launch

            // check if unselected option has children
            var itemsToRemove: List<ListItem> = emptyList()
            if (currentPropertyState.selectionIndex != -1) {
                val unselectedOption = currentPropertyState.data.options?.getOrNull(currentPropertyState.selectionIndex)
                if (unselectedOption != null) {
                    // remove child options
                    itemsToRemove = userSelectionState.filter {
                        it is CategoryPropertyListItem && it.data.parentId == unselectedOption.id
                    }
                }
            }

            // updating selection state
            currentPropertyState = currentPropertyState.copy(selectionIndex = click.index)

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

    private suspend fun getPropertiesForSubCategory(subCategory: Category): List<CategoryProperty> {
        val id = subCategory.id ?: return emptyList()//TODO handle this case
        return when (val result = repository.getCategoryProperties(id)) {
            is Success -> result.data
            is Error -> emptyList()
        }
    }

    private suspend fun getChildOptions(option: PropertyOption): List<CategoryProperty> {
        val id = option.id ?: return emptyList()//TODO handle this case
        return when (val result = repository.getOptionsChild(id)) {
            is Success -> result.data
            is Error -> emptyList()
        }
    }

}