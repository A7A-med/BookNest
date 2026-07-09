package com.example.booknest.ui.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@Composable
fun BookDetailsScreen(
    bookId: String?,
    viewModel: DetailsViewModel = hiltViewModel()
) {
    LaunchedEffect(bookId) {
        if (bookId != null) {
            viewModel.loadBookDetails(bookId)
        }
    }

    // متابعة حالة الشاشة
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else if (uiState.book != null) {
            val book = uiState.book!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start
            ) {
                AsyncImage(
                    model = book.thumbnailUrl,
                    contentDescription = book.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = book.title, style = MaterialTheme.typography.headlineMedium)

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "تأليف: ${book.authors}", style = MaterialTheme.typography.titleMedium)

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "الوصف:", style = MaterialTheme.typography.titleSmall)
                Text(text = book.description, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}