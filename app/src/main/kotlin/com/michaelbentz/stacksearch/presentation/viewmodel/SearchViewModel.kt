package com.michaelbentz.stacksearch.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.michaelbentz.stacksearch.domain.usecase.FetchLatestQuestionsUseCase
import com.michaelbentz.stacksearch.domain.usecase.GetQuestionsUseCase
import com.michaelbentz.stacksearch.domain.usecase.SearchQuestionsUseCase
import com.michaelbentz.stacksearch.presentation.model.SearchUiData
import com.michaelbentz.stacksearch.presentation.state.SearchUiState
import com.michaelbentz.stacksearch.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val fetchLatestQuestionsUseCase: FetchLatestQuestionsUseCase,
    private val searchQuestionsUseCase: SearchQuestionsUseCase,
    private val getQuestionsUseCase: GetQuestionsUseCase,
) : ViewModel() {
    private val _isRefreshing = MutableStateFlow(false)
    private val _refreshErrorState = MutableStateFlow<String?>(null)

    val refreshErrorState: StateFlow<String?> = _refreshErrorState

    val uiState: StateFlow<SearchUiState> = combine(
        _isRefreshing,
        _refreshErrorState,
        getQuestionsUseCase(),
    ) { isRefreshing, refreshError, questions ->
        when {
            questions.isNotEmpty() -> {
                SearchUiState.Data(
                    data = SearchUiData(),
                    isRefreshing = isRefreshing,
                )
            }

            refreshError != null && !isRefreshing -> SearchUiState.Error(refreshError)
            else -> SearchUiState.Loading
        }
    }.stateIn(
        viewModelScope,
        WhileSubscribed(STOP_TIMEOUT_MILLIS),
        SearchUiState.Loading,
    )

    init {
        loadInitialQuestions()
    }

    private fun loadInitialQuestions() {
        viewModelScope.launch {
            val existingQuestions = getQuestionsUseCase().first()
            if (existingQuestions.isEmpty()) {
                withRefresh {
                    fetchLatestQuestionsUseCase().collect { resource ->
                        handleResource(resource)
                    }
                }
            }
        }
    }

    fun searchQuestions(query: String) {
        viewModelScope.launch {
            withRefresh {
                searchQuestionsUseCase(query).collect { resource ->
                    handleResource(resource)
                }
            }
        }
    }

    private suspend fun withRefresh(block: suspend () -> Unit) {
        _refreshErrorState.value = null
        _isRefreshing.value = true
        try {
            block()
        } finally {
            _isRefreshing.value = false
        }
    }

    private fun handleResource(resource: Resource<Unit>) {
        when (resource) {
            is Resource.Loading -> _isRefreshing.value = true
            is Resource.Success -> _refreshErrorState.value = null
            is Resource.Error -> _refreshErrorState.value = resource.message
        }
    }

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
