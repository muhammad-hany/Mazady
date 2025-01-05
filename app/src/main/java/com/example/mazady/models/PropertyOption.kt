package com.example.mazady.models

import com.squareup.moshi.Json

data class PropertyOption (
    val id: Int?,
    val name: String?,
    val slug: String?,
    @Json(name = "parent") val parentId: Int?,
    val child: Boolean?
)