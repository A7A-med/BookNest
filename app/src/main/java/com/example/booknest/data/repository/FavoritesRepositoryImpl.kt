package com.example.booknest.data.repository

import com.example.booknest.data.local.FavoriteBookDao
import com.example.booknest.data.local.FavoriteBookEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavoritesRepositoryImpl @Inject constructor(
    private val dao: FavoriteBookDao
) : FavoritesRepository {

    override fun getFavoriteBooks(): Flow<List<FavoriteBookEntity>> {
        return dao.getFavoriteBooks()
    }

    override suspend fun addBook(book: FavoriteBookEntity) {
        dao.insertBook(book)
    }

    override suspend fun removeBook(bookId: String) {
        dao.deleteBook(bookId)
    }
}