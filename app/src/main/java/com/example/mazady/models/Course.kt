package com.example.mazady.models

data class Course (
    val title: String,
    val description: String,
    val imageUrl: String,
    val tags: List<CourseTag>,
    val author: String,
    val duration: String,
    val authorOccupation: String,
    val lessonsCount: Int
)