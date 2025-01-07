package com.example.mazady.data.repository

import com.example.mazady.data.datasource.RemoteDataSource
import com.example.mazady.models.Category
import com.example.mazady.models.CategoryProperty
import com.example.mazady.models.Error
import com.example.mazady.models.MazadyResult
import com.example.mazady.models.Success

class RepositoryImpl(private val remoteDataSource: RemoteDataSource) : Repository {

    override suspend fun getAllCategories(): MazadyResult<List<Category>> {
        return try {
            val response = remoteDataSource.getAllCategories()
            if (response.code == 200 && response.data?.categories != null) {
                Success(response.data.categories)
            } else {
                Error(Exception(response.msg))
            }
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun getCategoryProperties(categoryId: Int): MazadyResult<List<CategoryProperty>> {
        return try {
            val response = remoteDataSource.getCategoryProperties(categoryId)
            if (response.code == 200 && !response.data.isNullOrEmpty()) {
                Success(response.data)
            } else {
                Error(Exception(response.msg))
            }
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun getOptionsChild(optionId: Int): MazadyResult<List<CategoryProperty>> {
        return try {
            val response = remoteDataSource.getOptionsChild(optionId)
            if (response.code == 200 && !response.data.isNullOrEmpty()) {
                Success(response.data)
            } else {
                Error(Exception(response.msg))
            }
        } catch (e: Exception) {
            Error(e)
        }
    }
}