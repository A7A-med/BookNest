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
import kotlinx.coroutines.delay
import okio.IOException

class BookRepositoryImpl @Inject constructor(
    private val apiService: RetrofitInstance
) : BookRepository {

    private val booksCache = mutableMapOf<String, List<Book>>()

    override fun getBooks(query: String, isSearch: Boolean): Flow<Resource<List<Book>>> = flow {
        emit(Resource.Loading)

        if (!isSearch && booksCache.containsKey(query)) {
            emit(Resource.Success(booksCache[query]!!))
            return@flow
        }

        val maxRetries = 3
        var lastError: Resource.Error? = null

        for (attempt in 1..maxRetries) {
            try {
                val response = apiService.searchBooks(
                    query = query,
                    apiKey = BuildConfig.BOOKS_API_KEY
                )
                val result = response.toDomainList()

                if (!isSearch) {
                    booksCache[query] = result
                }

                emit(Resource.Success(result))
                return@flow
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                lastError = Resource.Error("Please Check the Internet connection")
            } catch (e: retrofit2.HttpException) {
                if (e.code() == 503 || e.code() == 429) {
                    lastError = Resource.Error("Server Error (${e.code()})")
                    if (attempt < maxRetries) {
                        delay(attempt * 800L)
                        continue
                    }
                } else {
                    emit(Resource.Error("Error From The Server (${e.code()})"))
                    return@flow
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "UnExpected Error"))
            }
        }
        emit(lastError ?: Resource.Error("Failed TO Load Data"))
    }
}