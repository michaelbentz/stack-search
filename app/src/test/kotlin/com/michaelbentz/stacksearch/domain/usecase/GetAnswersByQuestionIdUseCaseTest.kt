package com.michaelbentz.stacksearch.domain.usecase

import com.michaelbentz.stacksearch.domain.model.Answer
import com.michaelbentz.stacksearch.domain.repository.AnswerRepository
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
class GetAnswersByQuestionIdUseCaseTest {
    @Mock
    private lateinit var answerRepository: AnswerRepository

    private lateinit var useCase: GetAnswersByQuestionIdUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        useCase = GetAnswersByQuestionIdUseCase(answerRepository)
    }

    @Test
    fun `invoke delegates to repository and returns list`() = runTest {
        val expected = listOf(
            Answer(
                id = 1L,
                questionId = 123L,
                isAccepted = true,
                score = 5,
                body = "A",
                creationDateEpochSec = 1L,
                lastActivityEpochSec = 2L,
                lastEditEpochSec = null,
                ownerDisplayName = "OP",
                ownerProfileImage = null,
                ownerReputation = 10,
            ),
            Answer(
                id = 2L,
                questionId = 123L,
                isAccepted = false,
                score = 2,
                body = "B",
                creationDateEpochSec = 1L,
                lastActivityEpochSec = 2L,
                lastEditEpochSec = null,
                ownerDisplayName = "User",
                ownerProfileImage = null,
                ownerReputation = 20,
            ),
        )
        whenever(answerRepository.getAnswersByQuestionId(any())).thenReturn(flowOf(expected))

        val result = useCase(123L).toList()
        assertEquals(listOf(expected), result)
    }

    @Test
    fun `invoke returns empty list`() = runTest {
        whenever(answerRepository.getAnswersByQuestionId(any())).thenReturn(flowOf(emptyList()))

        val result = useCase(123L).toList()
        assertEquals(listOf(emptyList<Answer>()), result)
    }
}
