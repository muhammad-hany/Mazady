package com.example.mazady.models

data class MazadyApiResponse<T> (
    val code: Int?,
    val msg: String?,
    val data: T?
)