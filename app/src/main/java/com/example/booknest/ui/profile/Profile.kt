package com.example.booknest.ui.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.example.booknest.R

@Composable
fun ProfileScreen(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    onLogout: () -> Unit,
    onNavigateToFavorites: () -> Unit
) {
    val context = LocalContext.current
    val userId = auth.currentUser?.uid ?: ""

    var fullName by remember { mutableStateOf("Loading...") }
    var email by remember { mutableStateOf("Loading...") }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var savedCount by remember { mutableStateOf(0) }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            db.collection("Users").document(userId).get().addOnSuccessListener { doc ->
                fullName = doc.getString("Full Name") ?: "User"
                email = auth.currentUser?.email ?: ""
                imageUrl = doc.getString("profileImageUrl")
            }
            db.collection("SavedBooks").whereEqualTo("userId", userId).get().addOnSuccessListener {
                savedCount = it.size()
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            uploadImageToFirebase(it, userId, db) { newUrl -> imageUrl = newUrl }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.padding(top = 80.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            AsyncImage(
                model = imageUrl,
                error = painterResource(id = R.drawable.default_profile),
                placeholder = painterResource(id = R.drawable.default_profile),
                contentDescription = "Profile",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .clickable { launcher.launch("image/*") },
                contentScale = ContentScale.Crop
            )

            Surface(
                modifier = Modifier.size(35.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                shadowElevation = 4.dp
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = Color.White,
                    modifier = Modifier.padding(8.dp)
                )
            }
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

        Button(onClick = {}, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("Edit Profile") }

        Spacer(modifier = Modifier.width(16.dp))

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

fun uploadImageToFirebase(uri: Uri, userId: String, db: FirebaseFirestore, onComplete: (String) -> Unit) {
    val ref = FirebaseStorage.getInstance().reference.child("profile_images/$userId.jpg")
    ref.putFile(uri).addOnSuccessListener {
        ref.downloadUrl.addOnSuccessListener { url ->
            db.collection("Users").document(userId).update("profileImageUrl", url.toString())
            onComplete(url.toString())
        }
    }
}