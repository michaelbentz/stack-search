package com.michaelbentz.stacksearch.presentation.state

import com.michaelbentz.stacksearch.presentation.model.SearchUiData

sealed class SearchUiState {
    object Loading : SearchUiState()
    data class Data(
        val data: SearchUiData,
        val isRefreshing: Boolean = false,
    ) : SearchUiState()

    data class Error(val message: String) : SearchUiState()
}
