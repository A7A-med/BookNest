package com.example.booknest.data.repository

import com.example.booknest.data.model.Book
import com.example.booknest.data.model.Resource
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    fun getBooks(query: String): Flow<Resource<List<Book>>>
    fun getBookById(bookId: String): Flow<Resource<Book>>
}