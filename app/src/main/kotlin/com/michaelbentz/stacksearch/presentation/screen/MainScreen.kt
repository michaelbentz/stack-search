package com.michaelbentz.stacksearch.presentation.screen

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    Surface(
        modifier = modifier,
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Search.route,
        ) {
            composable(Screen.Search.route) {
                SearchScreen()
            }
            composable(Screen.Detail.route) {
                TODO()
            }
        }
    }
}

sealed class Screen(val route: String) {
    object Search : Screen("search")
    object Detail : Screen("detail")
}
