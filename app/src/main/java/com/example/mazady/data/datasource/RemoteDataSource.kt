package com.example.mazady.data.datasource

import com.example.mazady.models.CategoryData
import com.example.mazady.models.CategoryProperty
import com.example.mazady.models.MazadyApiResponse

interface RemoteDataSource {
    suspend fun getAllCategories(): MazadyApiResponse<CategoryData>
    suspend fun getCategoryProperties(categoryId: Int): MazadyApiResponse<List<CategoryProperty>>
    suspend fun getOptionsChild(optionId: Int): MazadyApiResponse<List<CategoryProperty>>
}