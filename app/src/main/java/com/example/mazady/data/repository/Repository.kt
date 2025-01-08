package com.example.mazady.data.repository

import com.example.mazady.models.Category
import com.example.mazady.models.CategoryProperty
import com.example.mazady.models.ListItem
import com.example.mazady.models.MazadyResult

interface Repository {
    suspend fun getAllCategories(): MazadyResult<List<Category>>
    suspend fun getCategoryProperties(categoryId: Int): MazadyResult<List<CategoryProperty>>
    suspend fun getOptionsChild(optionId: Int): MazadyResult<List<CategoryProperty>>
    fun submitItems(items: List<ListItem>)
    fun clearItems()
    fun getSubmittedItems(): List<ListItem>
}