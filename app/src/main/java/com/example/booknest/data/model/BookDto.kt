package com.example.booknest.data.model

data class BookDto(
    val id: String,
    val volumeInfo: VolumeInfoDto?
)

data class VolumeInfoDto(
    val title: String?,
    val authors: List<String>?,
    val description: String?,
    val imageLinks: ImageLinksDto?,
    val categories: List<String>?,
    val averageRating: Double?,
    val publishedDate: String?
)

data class ImageLinksDto(
    val thumbnail: String?
)

data class BookSearchResponseDto(
    val items: List<BookDto>?
)