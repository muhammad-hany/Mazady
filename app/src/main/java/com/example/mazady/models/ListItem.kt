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
) : ListItem(selectionIndex, null, categoryError) {

    override fun toString(): String {
        return "Main Category: ${data.getStringSelection(selectionIndex)}"
    }
}

data class SubCategoryListItem(
    val data: List<Category>,
    val selectionIndex: Int,
    val categoryError: Boolean = false
) : ListItem(selectionIndex, null, categoryError) {
    override fun toString(): String {
        return "Sub Category: ${data.getStringSelection(selectionIndex)}"
    }
}

private fun List<Category>.getStringSelection(selectionIndex: Int): String {
    if (selectionIndex == -1) return "nothing selected"
    return getOrNull(selectionIndex)?.name ?: return "nothing selected"
}

data class CategoryPropertyListItem(
    val data: CategoryProperty,
    val selectionIndex: Int,
    val inputText: String? = null,
    val propertyError: Boolean = false
) : ListItem(selectionIndex, inputText, propertyError) {

    override fun toString(): String {
        if (selectionIndex == -1 && inputText == null) return "Property ${data.slug}: nothing selected"
        val selection = if (!data.options.isNullOrEmpty()) {
            data.options.getOrNull(selectionIndex)?.name ?: return "nothing selected"
        } else inputText ?: "nothing selected"
        return "Property ${data.slug}: $selection"
    }
}

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