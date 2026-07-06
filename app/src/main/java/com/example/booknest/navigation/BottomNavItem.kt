package com.example.booknest.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem (
    val route:String,
    val label: String,
    val icon: ImageVector
){
    object Category: BottomNavItem("category","Category", Icons.Default.List)
    object Home: BottomNavItem("home","Home", Icons.Default.Home)
    object Favorites: BottomNavItem("favorites","Favorites", Icons.Default.Favorite)
    object Profile: BottomNavItem("profile","Profile",Icons.Default.Person)
}
val bottomNavItem = listOf(
    BottomNavItem.Category,
    BottomNavItem.Home,
    BottomNavItem.Favorites,
    BottomNavItem.Profile
)