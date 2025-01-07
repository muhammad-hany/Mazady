package com.example.mazady.data.api

import com.example.mazady.models.CategoryData
import com.example.mazady.models.CategoryProperty
import com.example.mazady.models.MazadyApiResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MazadyApi {
    @GET("get_all_cats")
    suspend fun getAllCategories(): MazadyApiResponse<CategoryData>

    @GET("properties")
    suspend fun getCategoryProperties(@Query("cat") categoryId: Int): MazadyApiResponse<List<CategoryProperty>>

    @GET("get-options-child/{optionId}")
    suspend fun getOptionsChild(@Path("optionId") optionId: Int): MazadyApiResponse<List<CategoryProperty>>
}