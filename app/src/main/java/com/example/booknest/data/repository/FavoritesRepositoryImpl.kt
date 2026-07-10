package com.example.booknest.data.repository

import com.example.booknest.data.local.FavoriteBookDao
import com.example.booknest.data.local.FavoriteBookEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavoritesRepositoryImpl @Inject constructor(
    private val favoriteBookDao: FavoriteBookDao
) : FavoritesRepository {

    override fun getFavoriteBooks(): Flow<List<FavoriteBookEntity>> = favoriteBookDao.getFavoriteBooks()

    override suspend fun addBook(book: FavoriteBookEntity) = favoriteBookDao.insertBook(book)

    override suspend fun removeBook(bookId: String) = favoriteBookDao.deleteBook(bookId)

    override fun isBookFavorite(bookId: String): Flow<Boolean> = favoriteBookDao.isFavorite(bookId)
}