package com.example.booknest.ui.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.booknest.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

@Composable
fun ProfileScreen(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    onLogout: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onEditProfileClick: () -> Unit
) {
    val currentUser = auth.currentUser

    if (currentUser == null) {
        LoggedOutView(onLoginClick = onLoginClick, onSignUpClick = onSignUpClick)
    } else {
        ProfileContent(auth = auth, db = db, onLogout = onLogout, onNavigateToFavorites = onNavigateToFavorites, onEditProfileClick = onEditProfileClick)
    }
}

@Composable
fun ProfileContent(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    onLogout: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onEditProfileClick: () -> Unit
) {

    val scrollState = rememberScrollState()
    val userId = auth.currentUser?.uid ?: ""
    var fullName by remember { mutableStateOf("Loading...") }
    var email by remember { mutableStateOf(auth.currentUser?.email ?: "") }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var savedCount by remember { mutableStateOf(0) }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            db.collection("Users").document(userId).get().addOnSuccessListener { doc ->
                fullName = doc.getString("Full Name") ?: "User"
                imageUrl = doc.getString("profileImageUrl")
            }
            db.collection("SavedBooks").whereEqualTo("userId", userId).get().addOnSuccessListener {
                savedCount = it.size()
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { uploadImageToFirebase(it, userId, db) { newUrl -> imageUrl = newUrl } }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.padding(top = 48.dp), contentAlignment = Alignment.BottomEnd) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .memoryCacheKey(imageUrl)
                    .diskCacheKey(imageUrl)
                    .build(),
                error = painterResource(id = R.drawable.default_profile),
                placeholder = painterResource(id = R.drawable.default_profile),
                contentDescription = "Profile",
                modifier = Modifier.size(120.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Surface(
                modifier = Modifier
                    .size(35.dp)
                    .clip(CircleShape) // مهم للـ ripple effect
                    .clickable { launcher.launch("image/*") }, // هنا بقى الـ click
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                shadowElevation = 4.dp
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = Color.White,
                    modifier = Modifier.padding(8.dp)
                )            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(fullName, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(email, color = Color.Gray)

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth().clickable { onNavigateToFavorites() },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("My Favorites", fontWeight = FontWeight.Bold)
                Text("$savedCount Favorites")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("ACCOUNT", modifier = Modifier.align(Alignment.Start), color = Color.Gray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(8.dp))

        ProfileInfoCard("FULL NAME", fullName, Icons.Default.Person)
        ProfileInfoCard("EMAIL", email, Icons.Default.Email)
        ProfileInfoCard("PASSWORD", "••••••••••••", Icons.Default.Lock)

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { onEditProfileClick() }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("Edit Profile") }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { auth.signOut(); onLogout() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFDECEA), contentColor = Color.Red)
        ) { Text("Log Out") }
    }
}

@Composable
fun ProfileInfoCard(label: String, value: String, icon: ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = Color(0xFFF0F0FF)) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(8.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(label, fontSize = 10.sp, color = Color.Gray)
                Text(value, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun LoggedOutView(onLoginClick: () -> Unit, onSignUpClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to BookNest!", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Please log in to continue.")
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onLoginClick, modifier = Modifier.fillMaxWidth()) { Text("Login") }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(onClick = onSignUpClick, modifier = Modifier.fillMaxWidth()) { Text("Sign Up") }
    }
}

fun uploadImageToFirebase(uri: Uri, userId: String, db: FirebaseFirestore, onComplete: (String) -> Unit) {
    val ref = FirebaseStorage.getInstance().reference.child("profile_images/$userId.jpg")
    ref.putFile(uri).addOnSuccessListener {
        ref.downloadUrl.addOnSuccessListener { url ->
            db.collection("Users").document(userId).update("profileImageUrl", url.toString())
            onComplete(url.toString())
        }
    }
}