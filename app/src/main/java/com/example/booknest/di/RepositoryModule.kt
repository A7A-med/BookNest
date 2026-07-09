package com.example.booknest.di

import com.example.booknest.data.repository.FavoritesRepository
import com.example.booknest.data.repository.FavoritesRepositoryImpl
import com.example.booknest.data.repository.BookRepository
import com.example.booknest.data.repository.BookRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBookRepository(
        impl: BookRepositoryImpl
    ): BookRepository

    // قم بنقل هذه الدالة إلى هنا داخل أقواس الكلاس
    @Binds
    @Singleton
    abstract fun bindFavoritesRepository(
        impl: FavoritesRepositoryImpl
    ): FavoritesRepository
}