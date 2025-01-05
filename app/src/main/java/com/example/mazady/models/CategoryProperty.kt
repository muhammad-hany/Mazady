package com.example.mazady.models

data class CategoryProperty (
    val id: Int?,
    val name: String?,
    val image: String?,
    val description: String?,
    val slug: String?,
    val options: List<PropertyOption>?
)