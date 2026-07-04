package com.example.booknest.ui.home

import com.example.booknest.data.model.Book

data class BookSection(
    val title: String,
    val books: List<Book>
)
data class HomeUiState(
    val isLoading: Boolean=false,
    val sections: List<BookSection> = emptyList(),
    val errorMessage: String? = null
)
