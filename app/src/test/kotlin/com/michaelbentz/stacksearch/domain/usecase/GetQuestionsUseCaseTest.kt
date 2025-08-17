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
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class GetQuestionsUseCaseTest {
    @Mock
    private lateinit var questionRepository: QuestionRepository

    private lateinit var useCase: GetQuestionsUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        useCase = GetQuestionsUseCase(questionRepository)
    }

    @Test
    fun `invoke delegates to repository and returns list`() = runTest {
        val expected = listOf(
            Question(
                id = 1L,
                title = "Q1",
                body = "B1",
                ownerDisplayName = "A",
                ownerReputation = 1,
                ownerProfileImage = null,
                tags = listOf("kotlin"),
                answerCount = 0,
                score = 0,
                viewCount = 0,
                creationDateEpochSec = 1L,
                lastActivityEpochSec = 2L,
                lastEditEpochSec = null,
                hasAcceptedAnswer = false,
            ),
            Question(
                id = 2L,
                title = "Q2",
                body = "B2",
                ownerDisplayName = "B",
                ownerReputation = 2,
                ownerProfileImage = null,
                tags = listOf("coroutines"),
                answerCount = 1,
                score = 2,
                viewCount = 5,
                creationDateEpochSec = 3L,
                lastActivityEpochSec = 4L,
                lastEditEpochSec = 5L,
                hasAcceptedAnswer = true,
            ),
        )
        whenever(questionRepository.getQuestions()).thenReturn(flowOf(expected))

        val result = useCase().toList()
        assertEquals(listOf(expected), result)
    }

    @Test
    fun `invoke returns empty list`() = runTest {
        whenever(questionRepository.getQuestions()).thenReturn(flowOf(emptyList()))

        val result = useCase().toList()
        assertEquals(listOf(emptyList<Question>()), result)
    }
}
