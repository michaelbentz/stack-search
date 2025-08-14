package com.michaelbentz.stacksearch.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.michaelbentz.stacksearch.domain.usecase.FetchAnswersByQuestionIdUseCase
import com.michaelbentz.stacksearch.domain.usecase.GetAnswersByQuestionIdUseCase
import com.michaelbentz.stacksearch.domain.usecase.GetQuestionByIdUseCase
import com.michaelbentz.stacksearch.presentation.mapper.toDetailUiData
import com.michaelbentz.stacksearch.presentation.screen.Screen
import com.michaelbentz.stacksearch.presentation.state.DetailUiState
import com.michaelbentz.stacksearch.util.UiDateFormatter
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
    @param:UiDateFormatter private val dateTimeFormatter: DateTimeFormatter,
    private val getQuestionByIdUseCase: GetQuestionByIdUseCase,
    private val getAnswersByQuestionIdUseCase: GetAnswersByQuestionIdUseCase,
    private val fetchAnswersByQuestionIdUseCase: FetchAnswersByQuestionIdUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _questionIdFlow = MutableStateFlow<Long?>(null)

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
    ) { question, answers ->
        if (question == null) {
            DetailUiState.Error("Question not found")
        } else {
            DetailUiState.Data(
                data = question.toDetailUiData(
                    answers = answers,
                    formatter = dateTimeFormatter,
                )
            )
        }
    }

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
