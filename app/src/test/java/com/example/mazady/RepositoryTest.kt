package com.example.mazady

import com.example.mazady.data.datasource.RemoteDataSource
import com.example.mazady.data.repository.RepositoryImpl
import com.example.mazady.models.Category
import com.example.mazady.models.CategoryData
import com.example.mazady.models.CategoryProperty
import com.example.mazady.models.Error
import com.example.mazady.models.ListItem
import com.example.mazady.models.MazadyApiResponse
import com.example.mazady.models.PropertyOption
import com.example.mazady.models.Success
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class RepositoryTest {
    private val fakeRemoteDataSource: RemoteDataSource = mockk()
    private val repository = RepositoryImpl(fakeRemoteDataSource)


    @Test
    fun `test get all categories in success state`() = runTest {
        val categories = listOf(mockk<Category>())
        val categoryData = CategoryData(categories)
        val response = MazadyApiResponse(200, "success", categoryData)
        coEvery { fakeRemoteDataSource.getAllCategories() } returns response
        val result = repository.getAllCategories()
        coVerify { fakeRemoteDataSource.getAllCategories() }
        assertThat(result).isInstanceOf(Success::class.java)
        assertThat((result as Success).data).isEqualTo(categories)
    }

    @Test
    fun `test get all categories in error state`() = runTest {
        val message = "error"
        val response = MazadyApiResponse<CategoryData>(404, message, null)
        coEvery { fakeRemoteDataSource.getAllCategories() } returns response
        val result = repository.getAllCategories()
        coVerify { fakeRemoteDataSource.getAllCategories() }
        assertThat(result).isInstanceOf(Error::class.java)
        assertThat((result as Error).error.message).isEqualTo(message)
    }

    @Test
    fun `test get all categories while exception is being thrown`() = runTest {
        val exception = Exception("exception")
        coEvery { fakeRemoteDataSource.getAllCategories() } throws exception
        val result = repository.getAllCategories()
        coVerify { fakeRemoteDataSource.getAllCategories() }
        assertThat(result).isInstanceOf(Error::class.java)
        assertThat((result as Error).error).isEqualTo(exception)
    }

    @Test
    fun `test get category properties in success state`() = runTest {
        val propertyOptions = PropertyOption(1, "name", "type", 12323, true)
        val categoryProperty =
            CategoryProperty(1, "name", "type", "value", "slug", 1123, listOf(propertyOptions))
        val categoryId = 1
        val response = MazadyApiResponse(200, "success", listOf(categoryProperty))
        coEvery { fakeRemoteDataSource.getCategoryProperties(categoryId) } returns response
        val result = repository.getCategoryProperties(categoryId)
        coVerify { fakeRemoteDataSource.getCategoryProperties(categoryId) }
        assertThat(result).isInstanceOf(Success::class.java)

        // asserting property
        assertThat((result as Success).data.first().id).isEqualTo(categoryProperty.id)
        assertThat(result.data.first().name).isEqualTo(categoryProperty.name)
        assertThat(result.data.first().slug).isEqualTo(categoryProperty.slug)
        assertThat(result.data.first().parentId).isEqualTo(categoryProperty.parentId)

        // asserting property options
        assertThat(result.data.first().options).isNotEmpty()
        assertThat(result.data.first().options!!.first()).isEqualTo(propertyOptions)

        // asserting that "other" option is being added
        assertThat(result.data.first().options!!.last().name).isEqualTo("other")
    }

    @Test
    fun `test get category properties in error state`() = runTest {
        val message = "error"
        val categoryId = 1
        val response = MazadyApiResponse<List<CategoryProperty>>(404, message, null)
        coEvery { fakeRemoteDataSource.getCategoryProperties(categoryId) } returns response
        val result = repository.getCategoryProperties(categoryId)
        coVerify { fakeRemoteDataSource.getCategoryProperties(categoryId) }
        assertThat(result).isInstanceOf(Error::class.java)
        assertThat((result as Error).error.message).isEqualTo(message)
    }

    @Test
    fun `test get category properties while exception is being thrown`() = runTest {
        val exception = Exception("exception")
        val categoryId = 1
        coEvery { fakeRemoteDataSource.getCategoryProperties(categoryId) } throws exception
        val result = repository.getCategoryProperties(categoryId)
        coVerify { fakeRemoteDataSource.getCategoryProperties(categoryId) }
        assertThat(result).isInstanceOf(Error::class.java)
        assertThat((result as Error).error).isEqualTo(exception)
    }

    @Test
    fun `test get options child in success state`() = runTest {
        val propertyOptions = PropertyOption(1, "name", "type", 12323, true)
        val categoryProperty =
            CategoryProperty(1, "name", "type", "value", "slug", 1123, listOf(propertyOptions))
        val optionId = 1
        val response = MazadyApiResponse(200, "success", listOf(categoryProperty))
        coEvery { fakeRemoteDataSource.getOptionsChild(optionId) } returns response
        val result = repository.getOptionsChild(optionId)
        coVerify { fakeRemoteDataSource.getOptionsChild(optionId) }
        assertThat(result).isInstanceOf(Success::class.java)

        // asserting property
        assertThat((result as Success).data.first().id).isEqualTo(categoryProperty.id)
        assertThat(result.data.first().name).isEqualTo(categoryProperty.name)
        assertThat(result.data.first().slug).isEqualTo(categoryProperty.slug)
        assertThat(result.data.first().parentId).isEqualTo(categoryProperty.parentId)

        // asserting property options
        assertThat(result.data.first().options).isNotEmpty()
        assertThat(result.data.first().options!!.first()).isEqualTo(propertyOptions)

        // asserting that "other" option is being added
        assertThat(result.data.first().options!!.last().name).isEqualTo("other")
    }

    @Test
    fun `test get options child in error state`() = runTest {
        val message = "error"
        val optionId = 1
        val response = MazadyApiResponse<List<CategoryProperty>>(404, message, null)
        coEvery { fakeRemoteDataSource.getOptionsChild(optionId) } returns response
        val result = repository.getOptionsChild(optionId)
        coVerify { fakeRemoteDataSource.getOptionsChild(optionId) }
        assertThat(result).isInstanceOf(Error::class.java)
        assertThat((result as Error).error.message).isEqualTo(message)
    }

    @Test
    fun `test get options child while exception is being thrown`() = runTest {
        val exception = Exception("exception")
        val optionId = 1
        coEvery { fakeRemoteDataSource.getOptionsChild(optionId) } throws exception
        val result = repository.getOptionsChild(optionId)
        coVerify { fakeRemoteDataSource.getOptionsChild(optionId) }
        assertThat(result).isInstanceOf(Error::class.java)
        assertThat((result as Error).error).isEqualTo(exception)
    }

    @Test
    fun `test submit items`() {
        val items = listOf(mockk<ListItem>())
        repository.submitItems(items)
        assertThat(repository.getSubmittedItems()).isEqualTo(items)
    }

    @Test
    fun `test clear items`() {
        val items = listOf(mockk<ListItem>())
        repository.submitItems(items)
        repository.clearItems()
        assertThat(repository.getSubmittedItems()).isEmpty()
    }
}