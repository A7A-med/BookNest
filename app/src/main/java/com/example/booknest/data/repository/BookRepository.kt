package com.example.booknest.data.repository

import com.example.booknest.data.model.Book
import com.example.booknest.data.model.Resource
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    fun getBooks(query: String, isSearch: Boolean = false): Flow<Resource<List<Book>>>}