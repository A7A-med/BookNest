package com.example.booknest.ui.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.booknest.data.model.Book

@Composable
fun BookDetailsScreen(navController: NavController) {
    val book = navController.previousBackStackEntry?.savedStateHandle?.get<Book>("book")

    if (book == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text("بيانات الكتاب مش موجودة!")
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        AsyncImage(
            model = book.thumbnailUrl,
            contentDescription = book.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = book.title, style = MaterialTheme.typography.headlineMedium)
        Text(text = "المؤلف: ${book.authors ?: "غير معروف"}", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "الوصف", style = MaterialTheme.typography.titleLarge)
        Text(text = book.description, style = MaterialTheme.typography.bodyMedium)
    }
}