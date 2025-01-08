package com.example.mazady.models


sealed class ListItem(
    val selection: Int,
    val userInput: String? = null,
    val showError: Boolean = false
) {
    val hasError get() = selection == -1 && userInput.isNullOrBlank()
}

data class MainCategoryListItem(
    val data: List<Category>,
    val selectionIndex: Int,
    val categoryError: Boolean = false
) : ListItem(selectionIndex, null, categoryError)

data class SubCategoryListItem(
    val data: List<Category>,
    val selectionIndex: Int,
    val categoryError: Boolean = false
) : ListItem(selectionIndex, null, categoryError)

data class CategoryPropertyListItem(
    val data: CategoryProperty,
    val selectionIndex: Int,
    val inputText: String? = null,
    val propertyError: Boolean = false
) : ListItem(selectionIndex, inputText, propertyError)

sealed class ItemClickAction
data class MainCategoryClick(val category: Category, val index: Int) : ItemClickAction()
data class SubCategoryClick(val category: Category, val index: Int) : ItemClickAction()
data class CategoryPropertyClick(
    val categoryProperty: CategoryProperty,
    val index: Int,
) : ItemClickAction()

data class CategoryPropertyInput(val categoryProperty: CategoryProperty, val inputText: String) :
    ItemClickAction()

data class CategoryOtherPropertyClick(val categoryProperty: CategoryProperty, val index: Int) :
    ItemClickAction()