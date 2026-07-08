package com.example.booknest.di

import android.content.Context
import androidx.room.Room
import com.example.booknest.data.local.BookNestDatabase
import com.example.booknest.data.local.FavoriteBookDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): BookNestDatabase {

        return Room.databaseBuilder(
            context,
            BookNestDatabase::class.java,
            "booknest_database"
        ).build()
    }

    @Provides
    fun provideFavoriteBookDao(
        database: BookNestDatabase
    ): FavoriteBookDao {
        return database.favoriteBookDao()
    }
}