package com.example.mazady.models

sealed class MazadyResult<T>
data class Success<T>(val data: T) : MazadyResult<T>()
data class Error<T>(val error: Throwable) : MazadyResult<T>()