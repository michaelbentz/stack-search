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
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class FetchNewestQuestionsUseCaseTest {
    @Mock
    private lateinit var questionRepository: QuestionRepository

    private lateinit var useCase: FetchNewestQuestionsUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        useCase = FetchNewestQuestionsUseCase(questionRepository)
    }

    @Test
    fun `invoke delegates to repository and returns Resource`() = runTest {
        val expected = listOf(Resource.Loading, Resource.Success(Unit))
        whenever(questionRepository.fetchNewestQuestions()).thenReturn(
            flowOf(Resource.Loading, Resource.Success(Unit))
        )

        val result = useCase().toList()
        assertEquals(expected, result)
    }

    @Test
    fun `invoke returns Resource Error on repository error`() = runTest {
        val error = Resource.Error(ERROR)
        whenever(questionRepository.fetchNewestQuestions()).thenReturn(flowOf(error))

        val result = useCase().toList()
        assertEquals(listOf(error), result)
    }

    private companion object {
        const val ERROR = "Error"
    }
}
