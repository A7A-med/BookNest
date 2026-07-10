package com.example.booknest.data.repository

import com.example.booknest.data.local.FavoriteBookDao
import com.example.booknest.data.local.FavoriteBookEntity
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {

    fun getFavoriteBooks(): Flow<List<FavoriteBookEntity>>

    suspend fun addBook(book: FavoriteBookEntity)

    suspend fun removeBook(bookId: String)

    fun isBookFavorite(bookId: String): Flow<Boolean>
}