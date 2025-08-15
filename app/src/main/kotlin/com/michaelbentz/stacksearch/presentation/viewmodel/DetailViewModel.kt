package com.michaelbentz.stacksearch.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.michaelbentz.stacksearch.domain.model.Answer
import com.michaelbentz.stacksearch.domain.usecase.FetchAnswersByQuestionIdUseCase
import com.michaelbentz.stacksearch.domain.usecase.GetAnswersByQuestionIdUseCase
import com.michaelbentz.stacksearch.domain.usecase.GetQuestionByIdUseCase
import com.michaelbentz.stacksearch.presentation.mapper.toDetailUiData
import com.michaelbentz.stacksearch.presentation.model.AnswerSortOrder
import com.michaelbentz.stacksearch.presentation.screen.Screen
import com.michaelbentz.stacksearch.presentation.state.DetailUiState
import com.michaelbentz.stacksearch.util.DetailUiDateTimeFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
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
    private val _questionIdFlow = MutableStateFlow<Long?>(null)
    private val _sortOrder = MutableStateFlow(AnswerSortOrder.Votes)
    val sortOrder: StateFlow<AnswerSortOrder> = _sortOrder

    val uiState: StateFlow<DetailUiState> = _questionIdFlow
        .filterNotNull()
        .flatMapLatest(::getUiState)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = DetailUiState.Loading,
        )

    init {
        val questionId: Long = checkNotNull(
            savedStateHandle[Screen.Detail.ARG_QUESTION_ID]
        )
        _questionIdFlow.value = questionId

        viewModelScope.launch {
            fetchAnswersByQuestionIdUseCase(questionId).collect()
        }
    }

    private fun getUiState(questionId: Long) = combine(
        getQuestionByIdUseCase(questionId),
        getAnswersByQuestionIdUseCase(questionId),
        _sortOrder,
    ) { question, answers, sortOrder ->
        if (question == null) {
            DetailUiState.Error("Question not found")
        } else {
            val sortedAnswers = answers.sortedWith(sortOrder.comparator())
            DetailUiState.Data(
                data = question.toDetailUiData(
                    answers = sortedAnswers,
                    dateTimeFormatter = dateTimeFormatter,
                )
            )
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

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
