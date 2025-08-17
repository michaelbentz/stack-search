package com.michaelbentz.stacksearch.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.michaelbentz.stacksearch.domain.usecase.FetchActiveQuestionsUseCase
import com.michaelbentz.stacksearch.domain.usecase.GetQuestionsUseCase
import com.michaelbentz.stacksearch.domain.usecase.SearchQuestionsUseCase
import com.michaelbentz.stacksearch.presentation.mapper.question.toQuestionItemUiData
import com.michaelbentz.stacksearch.presentation.model.SearchUiData
import com.michaelbentz.stacksearch.presentation.state.SearchUiState
import com.michaelbentz.stacksearch.util.Resource
import com.michaelbentz.stacksearch.util.SearchUiDateTimeFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    @param:SearchUiDateTimeFormatter private val dateTimeFormatter: DateTimeFormatter,
    private val fetchActiveQuestionsUseCase: FetchActiveQuestionsUseCase,
    private val searchQuestionsUseCase: SearchQuestionsUseCase,
    getQuestionsUseCase: GetQuestionsUseCase,
) : ViewModel() {
    private val _isRefreshing = MutableStateFlow(false)
    private val _refreshError = MutableStateFlow<String?>(null)
    val refreshError: StateFlow<String?> = _refreshError

    private val _query = MutableStateFlow("")

    val uiState: StateFlow<SearchUiState> = combine(
        _isRefreshing,
        _refreshError,
        _query,
        getQuestionsUseCase(),
    ) { isRefreshing, refreshError, query, questions ->
        val questionUiDataList = questions.map { question ->
            question.toQuestionItemUiData(dateTimeFormatter)
        }
        when {
            refreshError != null && !isRefreshing && questionUiDataList.isEmpty() -> {
                SearchUiState.Error(refreshError)
            }

            isRefreshing && questionUiDataList.isEmpty() -> {
                SearchUiState.Loading
            }

            else -> SearchUiState.Data(
                data = SearchUiData(
                    query = query,
                    questions = questionUiDataList,
                ),
                isRefreshing = isRefreshing,
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = SearchUiState.Loading,
    )

    init {
        refreshInitialQuestions()
    }

    private fun refreshInitialQuestions() {
        viewModelScope.launch {
            withRefresh {
                fetchActiveQuestionsUseCase().collect { resource ->
                    handleResource(resource)
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

    fun updateQuery(query: String) {
        _query.value = query
    }

    fun retryRefresh() {
        viewModelScope.launch {
            withRefresh {
                val query = _query.value
                val resourceFlow = if (query.isBlank()) {
                    fetchActiveQuestionsUseCase()
                } else {
                    searchQuestionsUseCase(query)
                }
                resourceFlow.collect { resource ->
                    handleResource(resource)
                }
            }
        }
    }

    private suspend fun withRefresh(block: suspend () -> Unit) {
        _refreshError.value = null
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
            is Resource.Success -> _refreshError.value = null
            is Resource.Error -> _refreshError.value = resource.message
        }
    }

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
