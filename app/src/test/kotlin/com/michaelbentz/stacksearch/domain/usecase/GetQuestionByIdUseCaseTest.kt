package com.michaelbentz.stacksearch.domain.usecase

import com.michaelbentz.stacksearch.domain.model.Question
import com.michaelbentz.stacksearch.domain.repository.QuestionRepository
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
class GetQuestionByIdUseCaseTest {
    @Mock
    private lateinit var questionRepository: QuestionRepository

    private lateinit var useCase: GetQuestionByIdUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        useCase = GetQuestionByIdUseCase(questionRepository)
    }

    @Test
    fun `invoke delegates to repository and returns Question`() = runTest {
        val expected = Question(
            id = 111L,
            title = "How to test?",
            body = "Body",
            ownerDisplayName = "Display Name",
            ownerReputation = 9001,
            ownerProfileImage = null,
            tags = listOf("kotlin"),
            answerCount = 1,
            score = 10,
            viewCount = 100,
            creationDateEpochSec = 1L,
            lastActivityEpochSec = 2L,
            lastEditEpochSec = null,
            hasAcceptedAnswer = true,
        )
        whenever(questionRepository.getQuestionById(any())).thenReturn(flowOf(expected))

        val result = useCase(111L).toList()
        assertEquals(listOf(expected), result)
    }

    @Test
    fun `invoke returns null`() = runTest {
        whenever(questionRepository.getQuestionById(any())).thenReturn(flowOf(null))

        val result = useCase(111L).toList()
        assertEquals(listOf(null), result)
    }
}
