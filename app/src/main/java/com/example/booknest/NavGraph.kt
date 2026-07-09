package com.example.booknest

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.booknest.navigation.bottomNavItem
import com.example.booknest.ui.LoginAndSignup.LoginScreen
import com.example.booknest.ui.LoginAndSignup.SignupScreen
import com.example.booknest.ui.category.CategoryScreen
import com.example.booknest.ui.details.BookDetailsScreen
import com.example.booknest.ui.favorites.FavoritesScreen
import com.example.booknest.ui.home.HomeScreen
import com.example.booknest.ui.profile.ProfileScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun NavGraph(navController: NavHostController, auth: FirebaseAuth, db: FirebaseFirestore) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute !in setOf("login", "signup")) {
                NavigationBar {
                    bottomNavItem.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = { navController.navigate(item.route) { popUpTo(0) { inclusive = true }; launchSingleTop = true } },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = "home", modifier = Modifier.padding(innerPadding)) {
            composable("signup") {
                SignupScreen(
                    auth = auth,
                    db = db,
                    onNavigateToLogin = { navController.navigate("login") }
                )
            }
            composable("login") { LoginScreen(auth, { navController.navigate("home") }, { navController.navigate("signup") }) }
            composable("home") { HomeScreen(onBookClick = { bookId -> navController.navigate("details/$bookId") }) }

            // التعديل المظبوط هنا:
            composable(
                route = "details/{bookId}",
                arguments = listOf(navArgument("bookId") { type = NavType.StringType })
            ) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId")
                BookDetailsScreen(bookId = bookId)
            }

            composable("category") { CategoryScreen() }
            composable("favorites") { FavoritesScreen() }
            composable("Profile") { ProfileScreen(auth, db, { navController.navigate("login") }, { navController.navigate("favorites") }) }
        }
    }
}