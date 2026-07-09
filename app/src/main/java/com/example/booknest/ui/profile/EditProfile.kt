package com.example.booknest.ui.profile

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.booknest.ui.LoginAndSignup.PasswordInputField
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val user = auth.currentUser
    val userId = user?.uid ?: ""

    var newName by remember { mutableStateOf("") }
    var newEmail by remember { mutableStateOf(user?.email ?: "") }
    var newPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        db.collection("Users").document(userId).get().addOnSuccessListener {
            newName = it.getString("Full Name") ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(24.dp)) {
            OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = newEmail,
                onValueChange = { newEmail = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordInputField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = "New Password (optional)",
                passwordVisible = passwordVisible,
                onVisibilityChange = { passwordVisible = !passwordVisible }
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    isLoading = true
                    val user = FirebaseAuth.getInstance().currentUser

                    db.collection("Users").document(userId).update("Full Name", newName)
                        .addOnSuccessListener {

                            if (newEmail != user?.email) {
                                user?.updateEmail(newEmail)?.addOnFailureListener { e ->
                                    Toast.makeText(context, "Email update failed: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            }

                            if (newPassword.isNotEmpty()) {
                                user?.updatePassword(newPassword)?.addOnFailureListener { e ->
                                    Toast.makeText(context, "Password update failed: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            }

                            Toast.makeText(context, "Profile Updated!", Toast.LENGTH_SHORT).show()
                            isLoading = false
                            onBack()
                        }
                        .addOnFailureListener {
                            isLoading = false
                            Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text("Save Changes")
            }
        }
    }
}