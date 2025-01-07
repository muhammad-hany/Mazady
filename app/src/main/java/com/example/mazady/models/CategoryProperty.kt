package com.example.mazady.models

import com.squareup.moshi.Json

data class CategoryProperty (
    val id: Int?,
    val name: String?,
    val image: String?,
    val description: String?,
    val slug: String?,
    @Json(name = "parent") val parentId: Int?,
    val options: List<PropertyOption>?
)