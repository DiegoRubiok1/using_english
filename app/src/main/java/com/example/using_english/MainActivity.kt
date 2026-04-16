package com.example.using_english

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.using_english.data.AppDatabase
import com.example.using_english.navigation.Screen
import com.example.using_english.repository.ExerciseRepository
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.using_english.ui.screens.ExercisesScreen
import com.example.using_english.ui.screens.CategorySelectionScreen
import com.example.using_english.ui.screens.ExerciseListScreen
import com.example.using_english.ui.screens.ExerciseDetailScreen
import com.example.using_english.ui.screens.HomeScreen
import com.example.using_english.ui.screens.SettingsScreen
import androidx.compose.animation.*
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.using_english.ui.theme.Using_englishTheme
import com.example.using_english.viewmodel.MainViewModel
import com.example.using_english.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val repository by lazy { ExerciseRepository(this, database.exerciseDao()) }
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        android.util.Log.d("MainActivity", "onCreate started")
        enableEdgeToEdge()
        try {
            setContent {
                val userStats by viewModel.userStats.collectAsState()
                Using_englishTheme(isBlackTheme = userStats?.isBlackTheme ?: false) {
                    Using_englishApp(viewModel)
                }
            }
            android.util.Log.d("MainActivity", "setContent completed")
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "FATAL: Error in setContent", e)
            throw e
        }
    }
}

@Composable
fun Using_englishApp(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val userStats by viewModel.userStats.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf(
        Screen.Home,
        Screen.Exercises,
        Screen.Settings
    )

    val showNavBar = currentRoute in items.map { it.route } || currentRoute == Screen.Exercises.route

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            items.forEach { screen ->
                item(
                    icon = {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = screen.title
                        )
                    },
                    label = { Text(screen.title) },
                    selected = currentRoute == screen.route,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            val padding = if (showNavBar) innerPadding else PaddingValues(0.dp)
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(padding)
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(userStats)
                }
                composable(Screen.Exercises.route) {
                    ExercisesScreen(onLevelSelected = { level ->
                        navController.navigate(Screen.CategorySelection.createRoute(level))
                    })
                }
                composable(
                    route = Screen.CategorySelection.route,
                    arguments = listOf(navArgument("level") { type = NavType.StringType })
                ) { backStackEntry ->
                    val level = backStackEntry.arguments?.getString("level") ?: ""
                    CategorySelectionScreen(
                        level = level,
                        onCategorySelected = { category ->
                            navController.navigate(Screen.ExerciseList.createRoute(level, category))
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(
                    route = Screen.ExerciseList.route,
                    arguments = listOf(
                        navArgument("level") { type = NavType.StringType },
                        navArgument("category") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val level = backStackEntry.arguments?.getString("level") ?: ""
                    val category = backStackEntry.arguments?.getString("category") ?: ""
                    ExerciseListScreen(
                        level = level,
                        category = category,
                        viewModel = viewModel,
                        onExerciseSelected = { exerciseId ->
                            navController.navigate(Screen.ExerciseDetail.createRoute(exerciseId))
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(
                    route = Screen.ExerciseDetail.route,
                    arguments = listOf(navArgument("id") { type = NavType.StringType }),
                    enterTransition = {
                        scaleIn(initialScale = 0.8f) + fadeIn()
                    },
                    exitTransition = {
                        scaleOut(targetScale = 0.8f) + fadeOut()
                    }
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id") ?: ""
                    ExerciseDetailScreen(
                        exerciseId = id,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.Settings.route) {
                    SettingsScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}