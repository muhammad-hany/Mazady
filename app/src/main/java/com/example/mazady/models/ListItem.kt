package com.example.mazady.models


sealed class ListItem
data class MainCategoryListItem(val data: List<Category>, val selectionIndex: Int) : ListItem()
data class SubCategoryListItem(val data: List<Category>, val selectionIndex: Int) : ListItem()
data class CategoryPropertyListItem(
    val data: CategoryProperty,
    val selectionIndex: Int,
    val inputText: String? = null
) : ListItem()

sealed class ItemClickAction
data class MainCategoryClick(val category: Category, val index: Int) : ItemClickAction()
data class SubCategoryClick(val category: Category, val index: Int) : ItemClickAction()
data class CategoryPropertyClick(
    val categoryProperty: CategoryProperty,
    val index: Int,
) : ItemClickAction()
data class CategoryPropertyInput(val categoryProperty: CategoryProperty, val inputText: String) :
    ItemClickAction()