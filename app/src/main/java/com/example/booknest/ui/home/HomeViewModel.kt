package com.example.booknest.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booknest.data.model.Resource
import com.example.booknest.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private data class CategoryQuery(val title: String, val query: String)

private val HOME_CATEGORIES = listOf(
    CategoryQuery("Curated for You", "subject:fiction"),
    CategoryQuery("Science", "subject:science"),
    CategoryQuery("Business", "subject:business"),
    CategoryQuery("Technology", "subject:technology"),
    CategoryQuery("Travel", "subject:travel"),
    CategoryQuery("Religion", "subject:religion"),
    CategoryQuery("Cooking", "subject:cooking")
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHome()
    }

    fun loadHome() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val deferredSections = HOME_CATEGORIES.mapIndexed { index, category ->
                    async {
                        delay(index * 200L)
                        try {
                            val result = repository.getBooks(category.query).first { it !is Resource.Loading }
                            category.title to result
                        } catch (e: Exception) {
                            category.title to Resource.Error(e.localizedMessage ?: "Failed")
                        }
                    }
                }

                val results = deferredSections.awaitAll()

                val successfulSections = results.mapNotNull { (title, result) ->
                    if (result is Resource.Success) {
                        BookSection(title, result.data)
                    } else null
                }

                val isAllFailed = results.all { it.second is Resource.Error }

                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        sections = successfulSections,
                        errorMessage = if (isAllFailed) "Failed to load content. Please try again." else null
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "An unexpected error occurred"
                    )
                }
            }
        }
    }
}