package com.example.booknest.data.model

data class Book(
    val id : String,
    val title: String,
    val authors: String,
    val description:String,
    val category: String,
    val rating: Double,
    val publishedDate: String,
    val thumbnailUrl: String?
)
