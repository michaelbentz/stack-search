package com.michaelbentz.stacksearch.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.michaelbentz.stacksearch.domain.model.Answer
import com.michaelbentz.stacksearch.domain.model.Question
import com.michaelbentz.stacksearch.domain.usecase.FetchAnswersByQuestionIdUseCase
import com.michaelbentz.stacksearch.domain.usecase.GetAnswersByQuestionIdUseCase
import com.michaelbentz.stacksearch.domain.usecase.GetQuestionByIdUseCase
import com.michaelbentz.stacksearch.presentation.model.AnswerSortOrder
import com.michaelbentz.stacksearch.presentation.screen.Screen
import com.michaelbentz.stacksearch.presentation.state.DetailUiState
import com.michaelbentz.stacksearch.util.Resource
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @Mock
    lateinit var getQuestionByIdUseCase: GetQuestionByIdUseCase

    @Mock
    lateinit var getAnswersByQuestionIdUseCase: GetAnswersByQuestionIdUseCase

    @Mock
    lateinit var fetchAnswersByQuestionIdUseCase: FetchAnswersByQuestionIdUseCase

    private lateinit var viewModel: DetailViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        whenever(getQuestionByIdUseCase(mockQuestionId)).thenReturn(flowOf(mockQuestion))
        whenever(getAnswersByQuestionIdUseCase(mockQuestionId)).thenReturn(flowOf(emptyList()))
        whenever(fetchAnswersByQuestionIdUseCase(any())).thenReturn(flowOf(Resource.Success(Unit)))

        viewModel = DetailViewModel(
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            getQuestionByIdUseCase,
            getAnswersByQuestionIdUseCase,
            fetchAnswersByQuestionIdUseCase,
            SavedStateHandle(mapOf(Screen.Detail.ARG_QUESTION_ID to mockQuestionId)),
        )
    }

    @Test
    fun `uiState is Loading initially`() = runTest {
        assertEquals(DetailUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `uiState emits Data when question and answers are available`() = runTest {
        whenever(getAnswersByQuestionIdUseCase(mockQuestionId)).thenReturn(flowOf(listOf(mockAnswer1)))

        advanceUntilIdle()

        val dataState = viewModel.awaitData()
        assertEquals(mockQuestion.id, dataState.data.id)
        assertEquals(1, dataState.data.answers.size)
    }

    @Test
    fun `setSortOrder Oldest sorts by creationDate ascending`() = runTest {
        whenever(getAnswersByQuestionIdUseCase(mockQuestionId))
            .thenReturn(flowOf(listOf(mockAnswer1, mockAnswer2)))

        advanceUntilIdle()
        viewModel.setSortOrder(AnswerSortOrder.Oldest)
        advanceUntilIdle()

        val data = viewModel.awaitData()
        val answerIds = data.data.answers.map { it.id }
        assertEquals(listOf(mockAnswer2.id, mockAnswer1.id), answerIds)
    }

    @Test
    fun `setSortOrder Votes sorts by score descending`() = runTest {
        whenever(getAnswersByQuestionIdUseCase(mockQuestionId))
            .thenReturn(flowOf(listOf(mockAnswer1, mockAnswer2)))

        advanceUntilIdle()
        viewModel.setSortOrder(AnswerSortOrder.Votes)
        advanceUntilIdle()

        val data = viewModel.awaitData()
        val answerIds = data.data.answers.map { it.id }
        assertEquals(listOf(mockAnswer2.id, mockAnswer1.id), answerIds)
    }

    @Test
    fun `setSortOrder Active sorts by lastActivityEpochSec descending`() = runTest {
        whenever(getAnswersByQuestionIdUseCase(mockQuestionId))
            .thenReturn(flowOf(listOf(mockAnswer1, mockAnswer2)))

        advanceUntilIdle()
        viewModel.setSortOrder(AnswerSortOrder.Active)
        advanceUntilIdle()

        val data = viewModel.awaitData()
        val answerIds = data.data.answers.map { it.id }
        assertEquals(listOf(mockAnswer2.id, mockAnswer1.id), answerIds)
    }

    @Test
    fun `refreshError is set when fetch fails`() = runTest {
        whenever(fetchAnswersByQuestionIdUseCase(any()))
            .thenReturn(flowOf(Resource.Error(ERROR_FETCH_ANSWERS)))

        viewModel.retryRefresh()
        advanceUntilIdle()
        assertEquals(ERROR_FETCH_ANSWERS, viewModel.refreshError.value)
    }

    @Test
    fun `uiState emits Error when fetch returns error and no data`() = runTest {
        whenever(getQuestionByIdUseCase(mockQuestionId)).thenReturn(flowOf(null))
        whenever(getAnswersByQuestionIdUseCase(mockQuestionId)).thenReturn(flowOf(emptyList()))
        whenever(fetchAnswersByQuestionIdUseCase(any()))
            .thenReturn(flowOf(Resource.Error(ERROR_FETCH_ANSWERS)))

        viewModel.retryRefresh()
        val error = viewModel.awaitError()
        assertEquals(ERROR_FETCH_ANSWERS, error.message)
    }

    @Test
    fun `uiState emits Error when question is null`() = runTest {
        whenever(getQuestionByIdUseCase(mockQuestionId)).thenReturn(flowOf(null))
        whenever(getAnswersByQuestionIdUseCase(mockQuestionId)).thenReturn(flowOf(listOf(mockAnswer1)))

        val error = viewModel.awaitError()
        assertEquals("Unable to load question", error.message)
    }

    private val mockQuestionId = 12L

    private val mockQuestion = Question(
        id = mockQuestionId,
        title = "Test question",
        body = "Question body",
        ownerDisplayName = "Alice",
        ownerReputation = 1234,
        ownerProfileImage = null,
        tags = listOf("kotlin"),
        answerCount = 2,
        score = 10,
        viewCount = 100,
        creationDateEpochSec = 1_700_000_000L,
        lastActivityEpochSec = 1_700_000_500L,
        lastEditEpochSec = null,
        hasAcceptedAnswer = false,
    )

    private val mockAnswer1 = Answer(
        id = 1L,
        questionId = mockQuestionId,
        isAccepted = false,
        score = 5,
        body = "Answer 1",
        creationDateEpochSec = 1_700_000_100L,
        lastActivityEpochSec = 1_700_000_200L,
        lastEditEpochSec = null,
        ownerDisplayName = "Display name",
        ownerProfileImage = null,
        ownerReputation = 200,
    )

    private val mockAnswer2 = Answer(
        id = 2L,
        questionId = mockQuestionId,
        isAccepted = false,
        score = 10,
        body = "Answer 2",
        creationDateEpochSec = 1_700_000_050L,
        lastActivityEpochSec = 1_700_000_300L,
        lastEditEpochSec = null,
        ownerDisplayName = "Display name",
        ownerProfileImage = null,
        ownerReputation = 300
    )

    private suspend fun DetailViewModel.awaitError(): DetailUiState.Error =
        uiState.first { it is DetailUiState.Error } as DetailUiState.Error

    private suspend fun DetailViewModel.awaitData(): DetailUiState.Data =
        uiState.first { it is DetailUiState.Data } as DetailUiState.Data

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    companion object {
        private const val ERROR_FETCH_ANSWERS = "Failed to load answers"
    }
}
