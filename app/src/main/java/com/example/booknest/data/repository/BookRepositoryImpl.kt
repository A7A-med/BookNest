package com.example.booknest.data.repository

import com.example.booknest.data.model.Book
import com.example.booknest.data.model.Resource
import com.example.booknest.data.remote.RetrofitInstance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import com.example.booknest.BuildConfig
import com.example.booknest.data.remote.toDomainList
import kotlinx.coroutines.CancellationException
import okio.IOException

class BookRepositoryImpl @Inject constructor(
    private val apiService: RetrofitInstance
) : BookRepository {
    override fun getBooks(query: String): Flow<Resource<List<Book>>> = flow {
        emit(Resource.Loading)
        try {
            val response=apiService.searchBooks(
                query=query,
                apiKey = BuildConfig.BOOKS_API_KEY
            )
            emit(Resource.Success(response.toDomainList()))
        }catch (e: CancellationException){
            throw e
        }catch (e: retrofit2.HttpException){
            emit(Resource.Error("Server Error(${e.code()}"))
        }catch (e: Exception){
            emit(Resource.Error(e.localizedMessage?:"UnExpected Error"))
        }
    }
}