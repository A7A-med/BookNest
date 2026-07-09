package com.example.booknest.data.remote

import com.example.booknest.data.model.Book

fun BookItemDto.toDomain(): Book {
    val info = volumeInfo
    return Book(
        id=id,
        title = info.title?:"Without title",
        authors = info.authors?.joinToString (", ")?:"Author not defined",
        description = info.description?: "No Description",
        category = info.categories?.firstOrNull() ?:"All",
        rating = info.averageRating ?:0.0,
        publishedDate = info.publishedDate?:"",
        thumbnailUrl = info.imageLinks?.thumbnail?.replace("http://","https://")
    )
}
fun BookSearchResponseDto.toDomainList(): List<Book>{
    return items?.map { it.toDomain() } ?:emptyList()
}