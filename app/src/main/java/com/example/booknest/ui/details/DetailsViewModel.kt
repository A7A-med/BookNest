package com.example.booknest.ui.details

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// 1. تعريف كلاس الكتاب (الموديل)
data class Book(
    val id: String,
    val title: String,
    val thumbnailUrl: String,
    val description: String,
    val authors: String
)

// 2. تعريف حالة الشاشة
data class DetailsUiState(
    val book: Book? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class DetailsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(DetailsUiState())
    val uiState = _uiState.asStateFlow()

    fun loadBookDetails(bookId: String) {
        _uiState.value = DetailsUiState(isLoading = true)

        val fakeBook = Book(
            id = bookId,
            title = "كتاب: $bookId",
            thumbnailUrl = "https://via.placeholder.com/150",
            description = "هذا هو الوصف التفصيلي للكتاب الذي تم اختياره. الصفحة الآن تعمل بكامل طاقتها بفضل الـ ViewModel.",
            authors = "المؤلف المجهول"
        )

        _uiState.value = DetailsUiState(book = fakeBook, isLoading = false)
    }
}