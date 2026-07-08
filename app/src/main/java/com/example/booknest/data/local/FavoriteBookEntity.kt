package com.example.booknest.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_books")
data class FavoriteBookEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val authors: String,
    val description: String,
    val category: String,
    val rating: Double,
    val publishedDate: String,
    val thumbnailUrl: String?
)