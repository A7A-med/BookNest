package com.example.booknest.data.remote

import com.example.booknest.data.model.BookDto
import com.example.booknest.data.model.BookSearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BookApiService {

    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 20,
        @Query("startIndex") startIndex: Int = 0,
        @Query("key") apiKey: String
    ): BookSearchResponseDto

    @GET("volumes/{id}")
    suspend fun getBookById(
        @Path("id") id: String,
        @Query("key") apiKey: String
    ): BookDto

    companion object {
        const val BASE_URL = "https://www.googleapis.com/books/v1/"
    }
}