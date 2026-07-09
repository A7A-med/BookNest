package com.example.booknest.data.remote

import com.example.booknest.data.model.Book
import com.example.booknest.data.model.BookDto
import com.example.booknest.data.model.BookSearchResponseDto

// هنا حولنا BookItemDto لـ BookDto عشان الـ Mapper يشتغل صح
fun BookDto.toDomain(): Book {
    val info = volumeInfo
    return Book(
        id = id,
        title = info?.title ?: "Without title",
        authors = info?.authors?.joinToString(", ") ?: "Author not defined",
        description = info?.description ?: "No Description",
        category = info?.categories?.firstOrNull() ?: "All",
        rating = info?.averageRating ?: 0.0,
        publishedDate = info?.publishedDate ?: "",
        thumbnailUrl = info?.imageLinks?.thumbnail?.replace("http://", "https://")
    )
}

fun BookSearchResponseDto.toDomainList(): List<Book> {
    return items?.map { it.toDomain() } ?: emptyList()
}