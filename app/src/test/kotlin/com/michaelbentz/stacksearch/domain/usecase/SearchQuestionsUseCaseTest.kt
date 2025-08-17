package com.michaelbentz.stacksearch.domain.usecase

import com.michaelbentz.stacksearch.domain.repository.QuestionRepository
import com.michaelbentz.stacksearch.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class SearchQuestionsUseCaseTest {
    @Mock
    private lateinit var questionRepository: QuestionRepository

    private lateinit var useCase: SearchQuestionsUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        useCase = SearchQuestionsUseCase(questionRepository)
    }

    @Test
    fun `invoke delegates to repository and returns Resource`() = runTest {
        val expected = listOf(Resource.Loading, Resource.Success(Unit))
        whenever(questionRepository.searchQuestions(any())).thenReturn(
            flowOf(Resource.Loading, Resource.Success(Unit))
        )

        val result = useCase(TITLE).toList()
        assertEquals(expected, result)
    }

    @Test
    fun `invoke returns Resource Error on repository error`() = runTest {
        val error = Resource.Error(ERROR)
        whenever(questionRepository.searchQuestions(any())).thenReturn(flowOf(error))

        val result = useCase(TITLE).toList()
        assertEquals(listOf(error), result)
    }

    private companion object {
        const val ERROR = "Error"
        const val TITLE = "kotlin coroutines"
    }
}
