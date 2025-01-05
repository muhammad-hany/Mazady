package com.example.mazady.data.datasource

import com.example.mazady.data.api.MazadyApi
import com.example.mazady.models.CategoryData
import com.example.mazady.models.CategoryProperty
import com.example.mazady.models.MazadyApiResponse

class RemoteDataSourceImpl(private val mazadyApi: MazadyApi) : RemoteDataSource {

    override suspend fun getAllCategories(): MazadyApiResponse<CategoryData> {
        return mazadyApi.getAllCategories()
    }

    override suspend fun getCategoryProperties(categoryId: Int): MazadyApiResponse<List<CategoryProperty>> {
        return mazadyApi.getCategoryProperties(categoryId)
    }
}