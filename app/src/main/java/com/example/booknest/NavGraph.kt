package com.example.booknest

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.booknest.ui.LoginAndSignup.LoginScreen
import com.example.booknest.ui.LoginAndSignup.SignupScreen
import com.example.booknest.ui.home.HomeScreen
import com.example.booknest.ui.profile.ProfileScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun NavGraph(
    navController: NavHostController,
    auth: FirebaseAuth,
    db: FirebaseFirestore
) {
    NavHost(navController = navController, startDestination = "Profile") {

        composable("signup") {
            SignupScreen(
                auth = auth,
                db = db,
                onNavigateToLogin = { navController.navigate("login") }
            )
        }

        composable("login") {
            LoginScreen(
                auth = auth,
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("signup") { inclusive = true }
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToSignup = { navController.navigate("signup") }
            )
        }

        composable("home") {
            HomeScreen()
        }

        composable("Profile") {
            ProfileScreen(
                auth = auth,
                db = db,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                        popUpTo("Profile") { inclusive = true }
                    }
                },
                onNavigateToFavorites = { navController.navigate("favorites") }
            )
        }
    }
}