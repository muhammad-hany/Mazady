package com.example.mazady.models

data class ScreenState (
    val isLoading: Boolean = false,
    val error: String? = null,
    val submitButtonVisible: Boolean = false
)