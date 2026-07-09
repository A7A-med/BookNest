package com.example.booknest

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.booknest.navigation.bottomNavItem
import com.example.booknest.ui.LoginAndSignup.LoginScreen
import com.example.booknest.ui.LoginAndSignup.SignupScreen
import com.example.booknest.ui.category.CategoryScreen
import com.example.booknest.ui.favorites.FavoritesScreen
import com.example.booknest.ui.home.HomeScreen
import com.example.booknest.ui.profile.EditProfileScreen
import com.example.booknest.ui.profile.ProfileScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private val routesWithoutBottomBar = setOf("login","signup")
@Composable
fun NavGraph(
    navController: NavHostController,
    auth: FirebaseAuth,
    db: FirebaseFirestore
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    Scaffold(
        bottomBar = {
            if (currentRoute !in routesWithoutBottomBar){
                NavigationBar{
                    bottomNavItem.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                if(currentRoute != item.route){
                                    navController.navigate(item.route){
                                        popUpTo(navController.graph.startDestinationId){
                                            saveState=true
                                        }
                                        launchSingleTop=true
                                        restoreState=true
                                    }
                                }
                            },
                            icon = {androidx.compose.material3.Icon(item.icon, contentDescription = item.label)},
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController=navController,
            startDestination = "Home",
            modifier = Modifier.padding(innerPadding)
        ){
            composable("EditProfile") {
                EditProfileScreen(
                    auth = auth,
                    db = db,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("signup") {
                SignupScreen(
                    auth = auth,
                    db = db,
                    onNavigateToLogin = {
                        navController.navigate("login") {
                            // نحن نغادر Signup، فنزيلها من الـ Stack
                            popUpTo("signup") { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("login") {
                LoginScreen(
                    auth = auth,
                    onLoginSuccess = {
                        navController.navigate("home") {
                            // بعد النجاح، نزيل كل صفحات الـ Auth من الـ Stack
                            popUpTo("signup") { inclusive = true }
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateToSignup = {
                        navController.navigate("signup") {
                            // نحن نغادر Login، فنزيلها من الـ Stack
                            popUpTo("login") { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable("home"){
                HomeScreen(navController = navController)
            }
            composable("category"){
                CategoryScreen()
            }
            composable("favorites"){
                FavoritesScreen()
            }
            composable("Profile"){
                ProfileScreen(
                    auth=auth,
                    db=db,
                    onLogout = {
                        navController.navigate("home"){
                            popUpTo("home") {inclusive=true}
                        }
                    },
                    onNavigateToFavorites = {navController.navigate("favorites")},
                    onLoginClick = {
                        navController.navigate("login")
                    },
                    onSignUpClick = {
                        navController.navigate("signup")
                    },
                    onEditProfileClick = {
                        navController.navigate("EditProfile")
                    }
                )
            }
        }
    }
}
