package com.example.booknest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.cloudinary.android.MediaManager
import com.example.booknest.ui.LoginAndSignup.SignupScreen
import com.example.booknest.ui.home.HomeScreen
import com.example.booknest.ui.theme.BookNestTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val config = mapOf(
            "cloud_name" to "uubyynq9",
            "api_key" to "847765613375499",
            "api_secret" to "o-R2_Jyxlqc2SUiKjnrk5y-iZO0"
        )

        try {
            MediaManager.init(this, config)
        } catch (e: Exception) { }

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        enableEdgeToEdge()
        setContent {
            BookNestTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController, auth = auth, db = db)
            }
        }
    }
}