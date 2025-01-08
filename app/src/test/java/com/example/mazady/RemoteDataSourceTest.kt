package com.example.mazady

import com.example.mazady.data.api.MazadyApi
import com.example.mazady.data.datasource.RemoteDataSourceImpl
import com.example.mazady.models.CategoryData
import com.example.mazady.models.CategoryProperty
import com.example.mazady.models.MazadyApiResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import com.google.common.truth.Truth.assertThat

class RemoteDataSourceTest {

    private val fakeMazadyApi: MazadyApi = mockk()
    private val remoteDataSourceImpl = RemoteDataSourceImpl(fakeMazadyApi)

    @Test
    fun testGetAllCategories() = runTest {
        val response: MazadyApiResponse<CategoryData> = mockk()
        coEvery { fakeMazadyApi.getAllCategories() } returns response
        assertEquals(response, remoteDataSourceImpl.getAllCategories())
        coVerify { fakeMazadyApi.getAllCategories() }
    }

    @Test
    fun testGetCategoryProperties() = runTest {
        val response: MazadyApiResponse<List<CategoryProperty>> = mockk()
        val categoryId = 234234
        coEvery { fakeMazadyApi.getCategoryProperties(categoryId) } returns response
        assertEquals(response, remoteDataSourceImpl.getCategoryProperties(categoryId))
        coVerify { fakeMazadyApi.getCategoryProperties(categoryId) }
    }

    @Test
    fun testGetOptionsChild() = runTest {
        val response: MazadyApiResponse<List<CategoryProperty>> = mockk()
        val optionId = 234234
        coEvery { fakeMazadyApi.getOptionsChild(optionId) } returns response
        assertEquals(response, remoteDataSourceImpl.getOptionsChild(optionId))
        coVerify { fakeMazadyApi.getOptionsChild(optionId) }
    }
}