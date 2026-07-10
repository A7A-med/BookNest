package com.example.booknest.data.model

data class Book(
    val id: String,
    val title: String,
    val authors: String = "Unknown",
    val description: String = "No description available",
    val category: String = "General",
    val rating: Double = 0.0,
    val language: String = "N/A",
    val publishedDate: String = "N/A",
    val thumbnailUrl: String? = null,
    val pageCount: Int = 0
)
