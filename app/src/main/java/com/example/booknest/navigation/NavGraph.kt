package com.example.booknest.navigation

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.edit
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.booknest.ui.LoginAndSignup.LoginScreen
import com.example.booknest.ui.LoginAndSignup.SignupScreen
import com.example.booknest.ui.details.BookDetailsScreen
import com.example.booknest.ui.explore.ExploreScreen
import com.example.booknest.ui.favorites.FavoritesScreen
import com.example.booknest.ui.home.HomeScreen
import com.example.booknest.ui.profile.EditProfileScreen
import com.example.booknest.ui.profile.ProfileScreen
import com.example.booknest.ui.splash.OnboardingScreen
import com.example.booknest.ui.splash.UserPreferences
import com.example.booknest.ui.splash.dataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import androidx.navigation.NavType
import androidx.navigation.navArgument

private val routesWithoutBottomBar = setOf("login", "signup", "EditProfile", "onboarding","book_details/{bookId}")

@Composable
fun NavGraph(
    navController: NavHostController,
    auth: FirebaseAuth,
    db: FirebaseFirestore
) {
    val context = LocalContext.current
    var isFirstTime by remember { mutableStateOf<Boolean?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        context.dataStore.data.map { prefs ->
            prefs[UserPreferences.IS_FIRST_TIME] ?: true
        }.collect { isFirstTime = it }
    }

    if (isFirstTime == null) return

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
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController=navController,
            startDestination = if (isFirstTime == true) "onboarding" else "home",
            modifier = Modifier.padding(innerPadding)
        ){

            composable(
                route = "book_details/{bookId}",
                arguments = listOf(navArgument("bookId") { type = NavType.StringType })
            ) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId")
                BookDetailsScreen(bookId = bookId, navController = navController)
            }

            composable("onboarding") {
                OnboardingScreen(
                    onNavigateToSignUp = {
                        scope.launch { context.dataStore.edit { it[UserPreferences.IS_FIRST_TIME] = false } }
                        navController.navigate("signup")
                    },
                    onNavigateToSignIn = {
                        scope.launch { context.dataStore.edit { it[UserPreferences.IS_FIRST_TIME] = false } }
                        navController.navigate("login")
                    },
                    onNavigateToGuestHome = {
                        scope.launch { context.dataStore.edit { it[UserPreferences.IS_FIRST_TIME] = false } }
                        navController.navigate("home") { popUpTo("onboarding") { inclusive = true } }
                    }
                )
            }

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
                            popUpTo("signup") { inclusive = true }
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateToSignup = {
                        navController.navigate("signup") {
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
            composable("explore"){
                ExploreScreen(navController = navController)
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
