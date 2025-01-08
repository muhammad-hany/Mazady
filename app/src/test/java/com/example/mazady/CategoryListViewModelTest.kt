package com.example.mazady

import com.example.mazady.data.repository.Repository
import com.example.mazady.models.Category
import com.example.mazady.models.Error
import com.example.mazady.models.ListItem
import com.example.mazady.models.MainCategoryListItem
import com.example.mazady.models.ScreenState
import com.example.mazady.models.Success
import com.example.mazady.view.category.CategoryListViewModel
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CategoryListViewModelTest {

    private val fakeRepository: Repository = mockk()
    private lateinit var viewModel: CategoryListViewModel
    private val testDispatchers = StandardTestDispatcher()


    @Before
    fun setup() {
        Dispatchers.setMain(testDispatchers)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test getAllCategories in success state`() = runTest {
        val categories = listOf(mockk<Category>())
        val result = Success(categories)
        coEvery { fakeRepository.getAllCategories() } returns result
        viewModel = CategoryListViewModel(fakeRepository)
        val testResults = mutableListOf<List<ListItem>>()
        val job = launch {
            viewModel.userSelectionFlow.collectLatest {
                testResults.add(it)
            }
        }
        delay(1)
        coVerify { fakeRepository.getAllCategories() }
        assertThat(testResults).isNotEmpty()
        assertThat((testResults.first().first() as MainCategoryListItem).data).isEqualTo(categories)
        job.cancel()

    }

    @Test
    fun `test getAllCategories in error state`() = runTest {
        val message = "error"
        val result = Error<List<Category>>(Exception(message))
        coEvery { fakeRepository.getAllCategories() } returns result
        viewModel = CategoryListViewModel(fakeRepository)
        val testResults = mutableListOf<ScreenState>()
        val job = launch {
            viewModel.screenStateFlow.collectLatest {
                testResults.add(it)
            }
        }
        delay(1)
        coVerify { fakeRepository.getAllCategories() }
        assertThat(testResults).isNotEmpty()
        assertThat(testResults.last().isLoading).isFalse()
        assertThat(testResults.last().error).isEqualTo(message)
        job.cancel()
    }

}