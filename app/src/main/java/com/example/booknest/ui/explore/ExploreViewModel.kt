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

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
        //fetchBooks()
    }

    fun onCategorySelected(category: String) {
        if (_selectedCategory.value == category) return

        _selectedCategory.value = category

        _books.value = Resource.Loading
        fetchBooks()
    }

    private fun fetchBooks() {
        viewModelScope.launch {
            val isSearching = _searchQuery.value.isNotEmpty()

            val q = if (isSearching) _searchQuery.value else _selectedCategory.value

            repository.getBooks(q, isSearch = isSearching).collect { resource ->
                _books.value = resource
            }
        }
    }

    fun performSearch() {
        _books.value = Resource.Loading
        fetchBooks()
    }
}