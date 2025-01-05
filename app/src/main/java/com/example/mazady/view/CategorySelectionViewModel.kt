package com.example.mazady.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mazady.data.repository.Repository
import com.example.mazady.models.CategoryState
import com.example.mazady.models.Error
import com.example.mazady.models.Success
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategorySelectionViewModel(private val repository: Repository) : ViewModel() {

    private val _categoryState = MutableStateFlow(CategoryState())
    val categoryState = _categoryState.asStateFlow()


    init {
        getAllCategories()
    }

    private fun getAllCategories() {
        viewModelScope.launch {
            _categoryState.emit(_categoryState.value.copy(isLoading = true))
            when (val result = repository.getAllCategories()) {
                is Success -> {
                    _categoryState.emit(
                        _categoryState.value.copy(
                            isLoading = false,
                            categories = result.data
                        )
                    )
                }

                is Error -> {
                    _categoryState.emit(
                        _categoryState.value.copy(
                            isLoading = false,
                            error = result.error.message
                        )
                    )
                }
            }
        }
    }

    fun getCategoryProperties(categoryId: Int) {
        viewModelScope.launch {
            _categoryState.emit(_categoryState.value.copy(isLoading = true))
            when (val result = repository.getCategoryProperties(categoryId)) {
                is Success -> {
                    _categoryState.emit(
                        _categoryState.value.copy(
                            isLoading = false,
                            subCategoryProperties = result.data
                        )
                    )
                }

                is Error -> {
                    _categoryState.emit(
                        _categoryState.value.copy(
                            isLoading = false,
                            error = result.error.message
                        )
                    )
                }
            }
        }
    }

    fun onMainCategorySelected(index: Int) {
        viewModelScope.launch {
            _categoryState.value = categoryState.value.copy(selectedMainCategoryIndex = index)
        }
    }

    fun onSubCategorySelected(index: Int) {
        _categoryState.value = categoryState.value.copy(selectedSubCategoryIndex = index)

    }

    fun onPropertySelected(index: Int) {
        _categoryState.value = categoryState.value.copy(selectedPropertyIndex = index)

    }
}