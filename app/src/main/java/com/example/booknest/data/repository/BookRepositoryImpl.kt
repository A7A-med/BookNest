package com.example.booknest.data.repository

import com.example.booknest.BuildConfig
import com.example.booknest.data.model.Book
import com.example.booknest.data.model.Resource
import com.example.booknest.data.remote.BookApiService
import com.example.booknest.data.remote.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val apiService: BookApiService
) : BookRepository {

    override fun getBooks(query: String): Flow<Resource<List<Book>>> = flow {
        /* الكود بتاعك القديم */
    }

    override fun getBookById(bookId: String): Flow<Resource<Book>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.getBookById(bookId, BuildConfig.BOOKS_API_KEY)
            emit(Resource.Success(response.toDomain()))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown Error"))
        }
    }
}