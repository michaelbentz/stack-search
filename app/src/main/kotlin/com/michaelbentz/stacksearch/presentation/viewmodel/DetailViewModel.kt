package com.michaelbentz.stacksearch.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.michaelbentz.stacksearch.domain.model.Answer
import com.michaelbentz.stacksearch.domain.usecase.FetchAnswersByQuestionIdUseCase
import com.michaelbentz.stacksearch.domain.usecase.GetAnswersByQuestionIdUseCase
import com.michaelbentz.stacksearch.domain.usecase.GetQuestionByIdUseCase
import com.michaelbentz.stacksearch.presentation.mapper.question.toDetailUiData
import com.michaelbentz.stacksearch.presentation.model.AnswerSortOrder
import com.michaelbentz.stacksearch.presentation.screen.Screen
import com.michaelbentz.stacksearch.presentation.state.DetailUiState
import com.michaelbentz.stacksearch.util.DetailUiDateTimeFormatter
import com.michaelbentz.stacksearch.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DetailViewModel @Inject constructor(
    @param:DetailUiDateTimeFormatter private val dateTimeFormatter: DateTimeFormatter,
    private val getQuestionByIdUseCase: GetQuestionByIdUseCase,
    private val getAnswersByQuestionIdUseCase: GetAnswersByQuestionIdUseCase,
    private val fetchAnswersByQuestionIdUseCase: FetchAnswersByQuestionIdUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _isRefreshing = MutableStateFlow(false)
    private val _refreshError = MutableStateFlow<String?>(null)
    val refreshError: StateFlow<String?> = _refreshError

    private val _questionId = MutableStateFlow<Long?>(null)
    private val _sortOrder = MutableStateFlow(AnswerSortOrder.Votes)
    val sortOrder: StateFlow<AnswerSortOrder> = _sortOrder

    val uiState: StateFlow<DetailUiState> = _questionId
        .filterNotNull()
        .flatMapLatest { questionId ->
            getUiState(questionId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = DetailUiState.Loading,
        )

    init {
        val questionId: Long = checkNotNull(
            savedStateHandle[Screen.Detail.ARG_QUESTION_ID]
        )
        _questionId.value = questionId

        viewModelScope.launch {
            refreshAnswers(questionId)
        }
    }

    private fun getUiState(questionId: Long) = combine(
        _isRefreshing,
        _refreshError,
        _sortOrder,
        getQuestionByIdUseCase(questionId),
        getAnswersByQuestionIdUseCase(questionId),
    ) { isRefreshing, refreshError, sortOrder, question, answers ->
        when {
            refreshError != null && !isRefreshing && question == null && answers.isEmpty() -> {
                DetailUiState.Error(refreshError)
            }

            question == null -> {
                DetailUiState.Error("Unable to load question")
            }

            else -> {
                val sortedAnswers = answers.sortedWith(sortOrder.comparator())
                DetailUiState.Data(
                    data = question.toDetailUiData(sortedAnswers, dateTimeFormatter),
                    isRefreshing = isRefreshing,
                )
            }
        }
    }

    fun retryRefresh() {
        val questionId = _questionId.value ?: return
        viewModelScope.launch {
            refreshAnswers(questionId)
        }
    }

    private suspend fun refreshAnswers(questionId: Long) = withRefresh {
        fetchAnswersByQuestionIdUseCase(questionId).collectLatest { resource ->
            handleResource(resource)
        }
    }

    fun setSortOrder(sort: AnswerSortOrder) {
        _sortOrder.value = sort
    }

    private fun AnswerSortOrder.comparator(): Comparator<Answer> = when (this) {
        AnswerSortOrder.Votes -> compareByDescending { it.score }
        AnswerSortOrder.Oldest -> compareBy { it.creationDateEpochSec }
        AnswerSortOrder.Active -> compareByDescending {
            it.lastActivityEpochSec ?: it.creationDateEpochSec
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
