package com.example.booknest.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FavoriteBookEntity::class],
    version = 1
)
abstract class BookNestDatabase : RoomDatabase() {

    abstract fun favoriteBookDao(): FavoriteBookDao
}