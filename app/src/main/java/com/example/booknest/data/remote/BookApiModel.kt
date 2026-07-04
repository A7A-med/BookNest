package com.example.booknest.data.remote

import com.google.gson.annotations.SerializedName

data class BookSearchResponseDto(
    @SerializedName("totalItems") val totalItems:Int,
    @SerializedName("items") val items: List<BookItemDto>
)

data class BookItemDto(
    @SerializedName("id") val id: String,
    @SerializedName("volumeInfo") val volumeInfo: VolumeInfoDto
)

data class VolumeInfoDto(
    @SerializedName("title") val title: String?,
    @SerializedName("authors") val authors: List<String>?,
    @SerializedName("description") val description: String?,
    @SerializedName("categories") val categories: List<String>?,
    @SerializedName("averageRating") val averageRating: Double?,
    @SerializedName("pageCount") val pageCount: Int?,
    @SerializedName("publishedDate") val publishedDate: String?,
    @SerializedName("imageLinks") val imageLinks: ImageLinksDto?
)
data class ImageLinksDto(
    @SerializedName("thumbnail") val thumbnail: String?,
    @SerializedName("smallThumbnail") val smallThumbnail: String?
)
