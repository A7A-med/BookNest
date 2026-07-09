package com.example.booknest.data.repository

import android.util.Log
import com.example.booknest.data.model.Book
import com.example.booknest.data.model.Resource
import com.example.booknest.data.remote.BookApiService
import com.example.booknest.data.remote.toDomain
import com.example.booknest.data.remote.toDomainList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val apiService: BookApiService
) : BookRepository {

    override fun getBooks(query: String): Flow<Resource<List<Book>>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.searchBooks(query = query)
            emit(Resource.Success(response.toDomainList()))
        } catch (e: Exception) {
            Log.e("DEBUG_ERROR", "Error: ${e.message}")
            emit(Resource.Error(e.message ?: "حدث خطأ"))
        }
    }.flowOn(Dispatchers.IO)

    override fun getBookById(bookId: String): Flow<Resource<Book>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.getBookById(id = bookId)
            emit(Resource.Success(response.toDomain()))
        } catch (e: Exception) {
            Log.e("DEBUG_ERROR", "Error: ${e.message}")
            emit(Resource.Error(e.message ?: "حدث خطأ"))
        }
    }.flowOn(Dispatchers.IO)
}