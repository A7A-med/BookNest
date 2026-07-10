package com.example.booknest.ui.category

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booknest.data.model.Book
import com.example.booknest.data.model.Resource
import com.example.booknest.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryBooksUiState(
    val isLoading: Boolean = false,
    val books: List<Book> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class CategoryBooksViewModel @Inject constructor(
    private val repository: BookRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryBooksUiState())
    val uiState: StateFlow<CategoryBooksUiState> = _uiState.asStateFlow()

    val categoryTitle: String = savedStateHandle.get<String>("categoryTitle") ?: ""
    private val categoryQuery: String = java.net.URLDecoder.decode(
        savedStateHandle.get<String>("categoryQuery")?:"","UTF-8"
    )

    init {
        loadBooks()
    }

    fun loadBooks() {
        viewModelScope.launch {
            repository.getBooks(categoryQuery).collect { result ->
                _uiState.value = when (result) {
                    is Resource.Loading -> _uiState.value.copy(isLoading = true, errorMessage = null)
                    is Resource.Success -> _uiState.value.copy(isLoading = false, books = result.data, errorMessage = null)
                    is Resource.Error -> _uiState.value.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }
}