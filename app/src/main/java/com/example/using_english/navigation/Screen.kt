package com.example.using_english.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Exercises : Screen("exercises", "Exercises", Icons.AutoMirrored.Filled.List)
    object CategorySelection : Screen("category_selection/{level}", "Categories",
        Icons.AutoMirrored.Filled.List
    ) {
        fun createRoute(level: String) = "category_selection/$level"
    }
    object ExerciseList : Screen("exercise_list/{level}/{category}", "Exercises",
        Icons.AutoMirrored.Filled.List
    ) {
        fun createRoute(level: String, category: String) = "exercise_list/$level/$category"
    }
    object ExerciseDetail : Screen("exercise_detail/{id}", "Exercise",
        Icons.AutoMirrored.Filled.List
    ) {
        fun createRoute(id: String) = "exercise_detail/$id"
    }
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}
