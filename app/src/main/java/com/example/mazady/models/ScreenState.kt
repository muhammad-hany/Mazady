package com.example.mazady.models

data class ScreenState (
    val categories: List<Category>? = null,
    val properties: List<CategoryProperty>? = null,
    val userSelectionList: List<Any> = emptyList(),
    val addedOptions: List<CategoryProperty>? = null,
)