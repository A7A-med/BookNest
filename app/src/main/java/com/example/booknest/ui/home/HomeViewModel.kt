package com.example.booknest.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booknest.data.model.Book
import com.example.booknest.data.model.Resource
import com.example.booknest.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private data class CategoryQuery(val title: String, val query: String)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val categories = listOf(
        CategoryQuery("Just for You", "subject:fiction"),
        CategoryQuery("Science", "subject:science"),
        CategoryQuery("Business", "subject:business"),
        CategoryQuery("Technology","subject:technology"),
        CategoryQuery("Travel","subject:travel"),
        CategoryQuery("Religion","subject:religion"),
        CategoryQuery("Cooking","subject:cooking")
    )

    init {
        loadHome()
    }

    fun getBookById(id: String): Book? {
        val sections = _uiState.value.sections
        return sections.flatMap { it.books }.find { it.id == id }
    }

    fun loadHome() {
        if (_uiState.value.sections.isNotEmpty()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val deferredSections = categories.map { category ->
                    async {
                        val result = repository.getBooks(category.query).first { it !is Resource.Loading }
                        category to result
                    }
                }

                val results = deferredSections.awaitAll()

                val firstError = results.firstNotNullOfOrNull { (_, result) ->
                    (result as? Resource.Error)?.message
                }

                if (firstError != null && results.all { it.second is Resource.Error }) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = firstError
                    )
                } else {
                    val sections = results.mapNotNull { (category, result) ->
                        when(result){
                            is Resource.Success -> BookSection(category.title, category.query, result.data)
                            is Resource.Error -> {
                                android.util.Log.e("HomeViewModel", "Section '${category.title}' failed: ${result.message}")
                                null
                            }
                            else -> null
                        }
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        sections = sections,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Unexpected Error"
                )
            }
        }
    }
}