package com.example.mazady.data.repository

import com.example.mazady.data.datasource.RemoteDataSource
import com.example.mazady.models.Category
import com.example.mazady.models.CategoryProperty
import com.example.mazady.models.Error
import com.example.mazady.models.ListItem
import com.example.mazady.models.MazadyResult
import com.example.mazady.models.PropertyOption
import com.example.mazady.models.Success
import kotlin.random.Random

class RepositoryImpl(private val remoteDataSource: RemoteDataSource) : Repository {

    private val submittedItems = mutableListOf<ListItem>()

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
            if (response.code == 200 && response.data != null) {
                Success(response.data.map { it.addedOtherOption() })
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
            if (response.code == 200 && response.data != null) {
                Success(response.data.map { it.addedOtherOption() } )
            } else {
                Error(Exception(response.msg))
            }
        } catch (e: Exception) {
            Error(e)
        }
    }

    override fun submitItems(items: List<ListItem>) {
        submittedItems.clear()
        submittedItems.addAll(items)
    }

    override fun clearItems() {
        submittedItems.clear()
    }

    override fun getSubmittedItems(): List<ListItem> = submittedItems.toList()

    private fun CategoryProperty.addedOtherOption() = copy(
        options = if (!options.isNullOrEmpty()) options.plus(createOtherPropertyOption(id ?: -1)) else null
    )

    private fun createOtherPropertyOption(parentId: Int) = PropertyOption(
        id = Random.nextInt(100, 1000),
        name = "other",
        slug = "other",
        parentId = parentId,
        child = true
    )
}