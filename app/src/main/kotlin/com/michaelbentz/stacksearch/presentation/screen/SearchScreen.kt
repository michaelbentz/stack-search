package com.michaelbentz.stacksearch.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.michaelbentz.stacksearch.presentation.state.SearchUiState
import com.michaelbentz.stacksearch.presentation.viewmodel.SearchViewModel

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val refreshErrorMessage by viewModel.refreshErrorState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is SearchUiState.Loading, is SearchUiState.Data, is SearchUiState.Error -> Unit
    }
}
