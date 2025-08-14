package com.michaelbentz.stacksearch.presentation.state

import com.michaelbentz.stacksearch.presentation.model.DetailUiData

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Data(
        val data: DetailUiData,
        val isRefreshing: Boolean = false,
    ) : DetailUiState()

    data class Error(val message: String) : DetailUiState()
}
