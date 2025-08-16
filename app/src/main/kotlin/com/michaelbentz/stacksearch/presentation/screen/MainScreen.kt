package com.michaelbentz.stacksearch.presentation.screen

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.michaelbentz.stacksearch.presentation.dialog.NoConnectivityDialog
import com.michaelbentz.stacksearch.presentation.viewmodel.MainViewModel

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    val isOffline by mainViewModel.isOffline.collectAsStateWithLifecycle()

    var showNoConnectivityDialog by remember { mutableStateOf(false) }
    if (showNoConnectivityDialog) {
        NoConnectivityDialog(
            onDismiss = {
                showNoConnectivityDialog = false
            },
        )
    }
    LaunchedEffect(isOffline) {
        showNoConnectivityDialog = isOffline
    }

    Surface(modifier = modifier) {
        NavHost(
            navController = navController,
            startDestination = Screen.Search.route,
        ) {
            composable(Screen.Search.route) {
                SearchScreen(
                    navController = navController,
                )
            }
            composable(
                route = Screen.Detail.route,
                arguments = listOf(
                    navArgument(Screen.Detail.ARG_QUESTION_ID) {
                        type = NavType.LongType
                    },
                ),
            ) {
                DetailScreen(
                    navController = navController,
                )
            }
        }
    }
}

sealed class Screen(val route: String) {
    data object Search : Screen("search")
    data object Detail : Screen("detail/{questionId}") {
        fun withArg(questionId: Long) = "detail/$questionId"
        const val ARG_QUESTION_ID = "questionId"
    }
}
