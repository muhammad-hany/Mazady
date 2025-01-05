package com.example.mazady.models

import com.squareup.moshi.Json

data class Category(
    val id: Int?,
    val name: String?,
    @Json(name = "image") val image: String?,
    val description: String?,
    val slug: String?,
    val children: List<Category>?,
    @Json(name = "circle_icon") val circleIconUrl: String?,
    @Json(name = "disable_shipping") val disableShipping: Int?,
)