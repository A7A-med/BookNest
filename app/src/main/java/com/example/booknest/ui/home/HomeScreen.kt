package com.example.booknest.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable // ضيف الـ import ده
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.booknest.R
import com.example.booknest.data.model.Book
import com.example.booknest.ui.theme.PlayfairDisplay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onBookClick: (Book) -> Unit // التعديل 1: استقبال الـ callback
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { HomeTopBar(scrollBehavior = scrollBehavior) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.isLoading -> LoadingState()
                uiState.errorMessage != null && uiState.sections.isEmpty() -> ErrorState(uiState.errorMessage!!, { viewModel.loadHome() })
                else -> HomeContent(sections = uiState.sections, onBookClick = onBookClick) // التعديل 2
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)@Composable
fun HomeTopBar(scrollBehavior: TopAppBarScrollBehavior) {
    TopAppBar(
        title = { Text("BookNest") },
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit = {}) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message)
    }
}

@Composable
fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun HomeContent(sections: List<BookSection>, onBookClick: (Book) -> Unit) {
    val firstBook = sections.firstOrNull()?.books?.firstOrNull()
    val remainingSections = sections.mapIndexed { index, section ->
        if (index == 0) section.copy(books = section.books.drop(1)) else section
    }
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Welcome Back", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))
        if (firstBook != null) {
            TrendingBanner(book = firstBook, onBookClick = onBookClick)
            Spacer(modifier = Modifier.height(24.dp))
        }
        remainingSections.forEach { section ->
            if (section.books.isNotEmpty()) {
                BookSectionRow(section = section, onBookClick = onBookClick)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}


@Composable
private fun TrendingBanner(book: Book, onBookClick: (Book) -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(280.dp).clip(RoundedCornerShape(24.dp)).clickable { onBookClick(book) }) { // التعديل 6
        AsyncImage(model = book.thumbnailUrl, contentDescription = book.title, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
    }
}

@Composable
private fun BookSectionRow(section: BookSection, onBookClick: (Book) -> Unit) { // التعديل 7
    Column {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(section.books, key = { it.id }) { book ->
                BookCoverCard(book = book, onBookClick = onBookClick) // التعديل 8
            }
        }
    }
}

@Composable
private fun BookCoverCard(book: Book, onBookClick: (Book) -> Unit) { // التعديل 9
    Column(modifier = Modifier.width(140.dp).clickable { onBookClick(book) }) { // التعديل 10
        AsyncImage(model = book.thumbnailUrl, contentDescription = book.title, modifier = Modifier.fillMaxWidth().aspectRatio(2f/3f).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
        // ... (باقي الـ Text زي ما هو)
    }
}