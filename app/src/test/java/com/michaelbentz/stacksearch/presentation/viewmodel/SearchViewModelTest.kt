package com.michaelbentz.stacksearch.presentation.viewmodel

import com.michaelbentz.stacksearch.domain.usecase.FetchNewestQuestionsUseCase
import com.michaelbentz.stacksearch.domain.usecase.GetQuestionsUseCase
import com.michaelbentz.stacksearch.domain.usecase.SearchQuestionsUseCase
import com.michaelbentz.stacksearch.presentation.state.SearchUiState
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
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var fetchNewestQuestionsUseCase: FetchNewestQuestionsUseCase

    @Mock
    private lateinit var searchQuestionsUseCase: SearchQuestionsUseCase

    @Mock
    private lateinit var getQuestionsUseCase: GetQuestionsUseCase

    private lateinit var viewModel: SearchViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        whenever(fetchNewestQuestionsUseCase()).thenReturn(flowOf(Resource.Success(Unit)))
        whenever(getQuestionsUseCase()).thenReturn(flowOf(emptyList()))
    }

    @Test
    fun `uiState is Loading initially`() = runTest {
        whenever(getQuestionsUseCase()).thenReturn(flowOf(emptyList()))
        whenever(fetchNewestQuestionsUseCase()).thenReturn(flowOf(Resource.Loading))

        viewModel = SearchViewModel(
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            fetchNewestQuestionsUseCase,
            searchQuestionsUseCase,
            getQuestionsUseCase,
        )

        assertEquals(SearchUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `uiState emits Error when fetchNewestQuestionsUseCase returns error and no questions`() = runTest {
        whenever(getQuestionsUseCase()).thenReturn(flowOf(emptyList()))
        whenever(fetchNewestQuestionsUseCase())
            .thenReturn(flowOf(Resource.Error(ERROR_FETCH_QUESTIONS)))

        viewModel = SearchViewModel(
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            fetchNewestQuestionsUseCase,
            searchQuestionsUseCase,
            getQuestionsUseCase,
        )

        val result = viewModel.awaitError()
        assertEquals(ERROR_FETCH_QUESTIONS, result.message)
    }

    @Test
    fun `uiState emits Data when there is no error and not refreshing`() = runTest {
        whenever(getQuestionsUseCase()).thenReturn(flowOf(emptyList()))
        whenever(fetchNewestQuestionsUseCase()).thenReturn(flowOf(Resource.Success(Unit)))

        viewModel = SearchViewModel(
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            fetchNewestQuestionsUseCase,
            searchQuestionsUseCase,
            getQuestionsUseCase,
        )

        val result = viewModel.awaitData()
        assertEquals(emptyList<Any>(), result.data.questions)
        assertEquals("", result.data.inputQuery)
        assertEquals("", result.data.submittedQuery)
    }

    @Test
    fun `searchQuestions calls use case and updates submittedQuery`() = runTest {
        whenever(getQuestionsUseCase()).thenReturn(flowOf(emptyList()))
        whenever(fetchNewestQuestionsUseCase()).thenReturn(flowOf(Resource.Success(Unit)))
        whenever(searchQuestionsUseCase(any())).thenReturn(flowOf(Resource.Success(Unit)))

        viewModel = SearchViewModel(
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            fetchNewestQuestionsUseCase,
            searchQuestionsUseCase,
            getQuestionsUseCase,
        )

        viewModel.searchQuestions("kotlin")

        val state = viewModel.awaitData()
        assertEquals("kotlin", state.data.submittedQuery)
        assertEquals(0, state.data.questions.size)

        verify(searchQuestionsUseCase).invoke("kotlin")
    }

    @Test
    fun `searchQuestions updates submittedQuery and triggers search`() = runTest {
        whenever(getQuestionsUseCase()).thenReturn(flowOf(emptyList()))
        whenever(fetchNewestQuestionsUseCase()).thenReturn(flowOf(Resource.Success(Unit)))
        whenever(searchQuestionsUseCase(any())).thenReturn(flowOf(Resource.Success(Unit)))

        viewModel = SearchViewModel(
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            fetchNewestQuestionsUseCase,
            searchQuestionsUseCase,
            getQuestionsUseCase,
        )

        viewModel.searchQuestions("kotlin")

        val state = viewModel.awaitData()
        assertEquals("kotlin", state.data.submittedQuery)
        assertEquals(0, state.data.questions.size)
    }

    @Test
    fun `retryRefresh runs search when inputQuery is not blank`() = runTest {
        whenever(getQuestionsUseCase()).thenReturn(flowOf(emptyList()))
        whenever(searchQuestionsUseCase(any())).thenReturn(flowOf(Resource.Success(Unit)))

        viewModel = SearchViewModel(
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            fetchNewestQuestionsUseCase,
            searchQuestionsUseCase,
            getQuestionsUseCase,
        )

        viewModel.updateQuery("compose")
        viewModel.retryRefresh()

        viewModel.awaitData()
        verify(searchQuestionsUseCase).invoke("compose")
        assertEquals(null, viewModel.refreshError.value)
    }

    @Test
    fun `retryRefresh fetches newest when query is blank`() = runTest {
        whenever(getQuestionsUseCase()).thenReturn(flowOf(emptyList()))
        whenever(fetchNewestQuestionsUseCase()).thenReturn(flowOf(Resource.Success(Unit)))

        viewModel = SearchViewModel(
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            fetchNewestQuestionsUseCase,
            searchQuestionsUseCase,
            getQuestionsUseCase,
        )

        viewModel.retryRefresh()
        advanceUntilIdle()

        assertEquals(null, viewModel.refreshError.value)
    }

    @Test
    fun `refreshError is set when fetchNewestQuestionsUseCase fails`() = runTest {
        whenever(getQuestionsUseCase()).thenReturn(flowOf(emptyList()))
        whenever(fetchNewestQuestionsUseCase())
            .thenReturn(flowOf(Resource.Error(ERROR_FETCH_QUESTIONS)))

        viewModel = SearchViewModel(
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            fetchNewestQuestionsUseCase,
            searchQuestionsUseCase,
            getQuestionsUseCase,
        )
        advanceUntilIdle()

        assertEquals(ERROR_FETCH_QUESTIONS, viewModel.refreshError.value)
    }

    private suspend fun SearchViewModel.awaitError(): SearchUiState.Error =
        uiState.first { it is SearchUiState.Error } as SearchUiState.Error

    private suspend fun SearchViewModel.awaitData(): SearchUiState.Data =
        uiState.first { it is SearchUiState.Data } as SearchUiState.Data

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    companion object {
        private const val ERROR_FETCH_QUESTIONS = "Failed to load questions"
    }
}
