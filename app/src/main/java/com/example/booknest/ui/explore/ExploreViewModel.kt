package com.example.booknest.ui.explore

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booknest.data.model.Book
import com.example.booknest.data.model.Resource
import com.example.booknest.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Fiction")
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _books = MutableStateFlow<Resource<List<Book>>>(Resource.Loading)
    val books = _books.asStateFlow()

    init {
        fetchBooks()
    }

    fun getBookById(id: String): Book? {
        val resource = _books.value
        return if (resource is Resource.Success) {
            resource.data.find { it.id == id }
        } else null
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onCategorySelected(category: String) {
        if (_selectedCategory.value == category) return

        _selectedCategory.value = category

        _books.value = Resource.Loading
        fetchBooks()
    }

    private fun fetchBooks() {
        viewModelScope.launch {
            _books.value = Resource.Loading

            val isSearching = _searchQuery.value.isNotEmpty()
            val q = if (isSearching){
                _searchQuery.value
            } else {
                "subject:${_selectedCategory.value.lowercase()}"
            }

            try {
                val result = repository.getBooks(q, isSearch = isSearching).last()
                _books.value = result
            } catch (e: Exception) {
                _books.value = Resource.Error("Error: ${e.message}")
            }
        }
    }

    fun performSearch() {
        _books.value = Resource.Loading
        fetchBooks()
    }
}