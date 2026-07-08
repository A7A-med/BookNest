package com.example.booknest.ui.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.example.booknest.data.model.Book
import com.example.booknest.ui.theme.PlayfairDisplay

@Composable
fun FavoritesScreen() {

    val favoriteBooks = listOf(
        Book(
            id = "1",
            title = "Atomic Habits",
            authors = "James Clear",
            description = "",
            category = "",
            rating = 4.8,
            publishedDate = "",
            thumbnailUrl = "https://covers.openlibrary.org/b/id/10523338-L.jpg"
        ),
        Book(
            id = "2",
            title = "Deep Work",
            authors = "Cal Newport",
            description = "",
            category = "",
            rating = 4.7,
            publishedDate = "",
            thumbnailUrl = "https://covers.openlibrary.org/b/id/11153228-L.jpg"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Favorites",
            style = MaterialTheme.typography.headlineLarge,
            fontFamily = PlayfairDisplay,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Your collection",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(favoriteBooks) { book ->
                FavoriteBookCard(book)
            }
        }
    }
}

@Composable
private fun FavoriteBookCard(book: Book) {

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors()
    ) {

        Column {

            AsyncImage(
                model = book.thumbnailUrl,
                contentDescription = book.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(12.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(book.rating.toString())
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = book.title,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = book.authors,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text("Favorite")
                }
            }
        }
    }
}