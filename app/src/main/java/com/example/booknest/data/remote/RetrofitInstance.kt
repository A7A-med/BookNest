package com.example.booknest.data.remote

import retrofit2.http.Query
import retrofit2.http.GET

interface RetrofitInstance {
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults:Int=20,
        @Query("startIndex") startIndex:Int=0,
        @Query("key") apiKey: String
    ): BookSearchResponseDto
    companion object{
        const val BASE_URL= "https://www.googleapis.com/books/v1/"
    }
}