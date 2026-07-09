package com.example.booknest.ui.explore

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.booknest.data.model.Book

@Composable
fun BookItem(book: Book) {
    Card(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
                model = book.thumbnailUrl,
                contentDescription = null,
                modifier = Modifier.height(150.dp).fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Text(text = book.title, maxLines = 1, modifier = Modifier.padding(8.dp))
        }
    }
}