package com.example.booknest.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.booknest.R
import com.example.booknest.data.model.Book
import com.example.booknest.ui.theme.PlayfairDisplay
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCategoryClick: (title: String, query: String) -> Unit,
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
    db: FirebaseFirestore = Firebase.firestore
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = rememberTopAppBarState()
    )
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { HomeTopBar(navController = navController, scrollBehavior = scrollBehavior) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> LoadingState()
                uiState.errorMessage != null && uiState.sections.isEmpty() -> ErrorState(
                    message = uiState.errorMessage!!,
                    onRetry = { viewModel.loadHome() }
                )
                else -> HomeContent(
                    sections = uiState.sections,
                    db = db,
                    onCategoryClick = onCategoryClick,
                    navController = navController
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(navController: NavController, scrollBehavior: TopAppBarScrollBehavior) {
    TopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.book_nest_logo),
                    contentDescription = null,
                    modifier = Modifier.size(75.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "BookNest",
                    fontFamily = PlayfairDisplay,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        actions = {
            val currentUser = FirebaseAuth.getInstance().currentUser
            val profileImageUrl = currentUser?.photoUrl?.toString()

            IconButton(
                onClick = {
                    navController.navigate("Profile")
                },
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (currentUser != null && !profileImageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = profileImageUrl,
                        contentDescription = "Profile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.default_profile),
                        contentDescription = "Default Profile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
    )
}

@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = message, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Try Again")
        }
    }
}

@Composable
private fun HomeContent(
    sections: List<BookSection>,
    db: FirebaseFirestore,
    onCategoryClick: (title: String, query: String) -> Unit,
    navController: NavController
) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    var fullName by remember { mutableStateOf<String?>(null) }

    val annotatedText by remember(currentUser, fullName) {
        derivedStateOf {
            buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color(0xFF154212), fontWeight = FontWeight.Bold)) {
                    append("Welcome Back")
                }
                if (currentUser != null && fullName != null) {
                    withStyle(style = SpanStyle(color = Color(0xFF154212), fontWeight = FontWeight.Bold)) {
                        append(",")
                    }
                    withStyle(style = SpanStyle(color = Color.Gray, fontSize = 25.sp)) {
                        append("\n$fullName")
                    }
                }
            }
        }
    }

    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { uid ->
            db.collection("Users").document(uid).get().addOnSuccessListener { doc ->
                fullName = doc.getString("Full Name")
            }
        }
    }

    val firstBook = sections.firstOrNull()?.books?.firstOrNull()
    val remainingSections = sections.mapIndexed { index, section ->
        if (index == 0) section.copy(books = section.books.drop(1)) else section
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = annotatedText, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))
        
        if (firstBook != null) {
            TrendingBanner(book = firstBook, navController = navController)
            Spacer(modifier = Modifier.height(24.dp))
        }

        remainingSections.forEach { section ->
            if (section.books.isNotEmpty()) {
                BookSectionRow(
                    section = section,
                    onViewAllClick = onCategoryClick,
                    onBookClick = { book ->
                        navController.navigate("book_details/${book.id}")
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
private fun TrendingBanner(book: Book, navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable { navController.navigate("book_details/${book.id}") }
    ) {
        AsyncImage(
            model = book.thumbnailUrl,
            contentDescription = book.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.75f), Color.Transparent)
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.primary
            ) {
                Text(
                    text = "Trending Now",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = book.title,
                fontFamily = PlayfairDisplay,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                lineHeight = 32.sp,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = book.description,
                color = Color.White.copy(alpha = 0.9f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.primary
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Start Reading",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BookSectionRow(
    section: BookSection,
    onViewAllClick: (title: String, query: String) -> Unit,
    onBookClick: (Book) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = section.title, style = MaterialTheme.typography.headlineMedium
            )
            TextButton(onClick = { onViewAllClick(section.title, section.query) }) {
                Text("View All")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(section.books, key = { it.id }) { book ->
                BookCoverCard(
                    book = book,
                    onClick = onBookClick
                )
            }
        }
    }
}

@Composable
internal fun BookCoverCard(
    book: Book,
    onClick: ((Book) -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .then(
                if (onClick != null) Modifier.clickable { onClick(book) }
                else Modifier
            )
    ) {
        AsyncImage(
            model = book.thumbnailUrl,
            contentDescription = book.title,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = book.title,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = book.authors,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
