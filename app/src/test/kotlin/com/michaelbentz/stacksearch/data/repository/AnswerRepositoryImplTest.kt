package com.michaelbentz.stacksearch.data.repository

import com.michaelbentz.stacksearch.data.local.dao.AnswerDao
import com.michaelbentz.stacksearch.data.local.entity.AnswerEntity
import com.michaelbentz.stacksearch.data.remote.model.AnswerItemDto
import com.michaelbentz.stacksearch.data.remote.model.AnswersResponse
import com.michaelbentz.stacksearch.data.remote.model.OwnerDto
import com.michaelbentz.stacksearch.data.remote.service.AnswerService
import com.michaelbentz.stacksearch.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class AnswerRepositoryImplTest {
    @Mock
    private lateinit var answerService: AnswerService

    @Mock
    private lateinit var answerDao: AnswerDao

    private lateinit var repository: AnswerRepositoryImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = AnswerRepositoryImpl(answerService, answerDao)
    }

    @Test
    fun `fetchAnswersByQuestionId emits Loading then Success and calls replaceForQuestion`() = runTest {
        val questionId = MOCK_QUESTION_ID
        val response = buildMockAnswersResponse(questionId = questionId)

        whenever(answerService.getAnswersForQuestion(questionId)).thenReturn(response)

        val emissions = repository.fetchAnswersByQuestionId(questionId).toList()
        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Success)
        verify(answerDao).replaceForQuestion(any(), any())
    }

    @Test
    fun `fetchAnswersByQuestionId emits Error on service exception`() = runTest {
        val questionId = MOCK_QUESTION_ID
        whenever(answerService.getAnswersForQuestion(questionId))
            .thenThrow(RuntimeException(ERROR))

        val emissions = repository.fetchAnswersByQuestionId(questionId).toList()

        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Error)
        assertEquals(ERROR_MESSAGE, (emissions[1] as Resource.Error).message)
        assertTrue((emissions[1] as Resource.Error).throwable is RuntimeException)
    }

    @Test
    fun `getAnswersByQuestionId returns mapped list from entities`() = runTest {
        val questionId = MOCK_QUESTION_ID
        val entities = listOf(buildMockAnswerEntity())

        whenever(answerDao.getByQuestionId(questionId)).thenReturn(flowOf(entities))

        val answers = repository.getAnswersByQuestionId(questionId).first()
        assertNotNull(answers)
        assertEquals(1, answers.size)

        val answer = answers[0]
        assertEquals(MOCK_ANSWER_ID, answer.id)
        assertEquals(MOCK_QUESTION_ID, answer.questionId)
        assertEquals(MOCK_IS_ACCEPTED, answer.isAccepted)
        assertEquals(MOCK_SCORE, answer.score)
        assertEquals(MOCK_BODY, answer.body)
        assertEquals(MOCK_CREATION_EPOCH, answer.creationDateEpochSec)
        assertEquals(MOCK_LAST_ACTIVITY_EPOCH, answer.lastActivityEpochSec)
        assertEquals(MOCK_LAST_EDIT_EPOCH, answer.lastEditEpochSec)
        assertEquals(MOCK_OWNER_NAME, answer.ownerDisplayName)
        assertEquals(MOCK_OWNER_IMAGE, answer.ownerProfileImage)
        assertEquals(MOCK_OWNER_REPUTATION, answer.ownerReputation)
    }

    @Test
    fun `getAnswersByQuestionId returns empty list when no entities`() = runTest {
        val questionId = MOCK_QUESTION_ID
        whenever(answerDao.getByQuestionId(questionId)).thenReturn(flowOf(emptyList()))

        val answers = repository.getAnswersByQuestionId(questionId).first()
        assertTrue(answers.isEmpty())
    }

    private fun buildMockAnswersResponse(
        answerId: Long = MOCK_ANSWER_ID,
        questionId: Long? = MOCK_QUESTION_ID,
        isAccepted: Boolean = MOCK_IS_ACCEPTED,
        score: Int = MOCK_SCORE,
        body: String = MOCK_BODY,
        creationDateEpochSec: Long = MOCK_CREATION_EPOCH,
        lastActivityEpochSec: Long? = MOCK_LAST_ACTIVITY_EPOCH,
        lastEditEpochSec: Long? = MOCK_LAST_EDIT_EPOCH,
        owner: OwnerDto = buildMockOwnerDto(),
    ): AnswersResponse {
        return AnswersResponse(
            items = listOf(
                AnswerItemDto(
                    isAccepted = isAccepted,
                    score = score,
                    answerId = answerId,
                    questionId = questionId,
                    body = body,
                    creationDateEpochSec = creationDateEpochSec,
                    lastActivityEpochSec = lastActivityEpochSec,
                    lastEditEpochSec = lastEditEpochSec,
                    answerOwnerDto = owner,
                )
            )
        )
    }

    private fun buildMockOwnerDto(
        displayName: String? = MOCK_OWNER_NAME,
        profileImage: String? = MOCK_OWNER_IMAGE,
        reputation: Int? = MOCK_OWNER_REPUTATION,
    ): OwnerDto = OwnerDto(
        displayName = displayName,
        profileImage = profileImage,
        reputation = reputation,
    )

    private fun buildMockAnswerEntity(
        answerId: Long = MOCK_ANSWER_ID,
        questionId: Long = MOCK_QUESTION_ID,
        isAccepted: Boolean = MOCK_IS_ACCEPTED,
        score: Int = MOCK_SCORE,
        body: String = MOCK_BODY,
        creationDateEpochSec: Long = MOCK_CREATION_EPOCH,
        lastActivityEpochSec: Long? = MOCK_LAST_ACTIVITY_EPOCH,
        lastEditEpochSec: Long? = MOCK_LAST_EDIT_EPOCH,
        ownerDisplayName: String? = MOCK_OWNER_NAME,
        ownerProfileImage: String? = MOCK_OWNER_IMAGE,
        ownerReputation: Int? = MOCK_OWNER_REPUTATION,
    ) = AnswerEntity(
        answerId = answerId,
        questionId = questionId,
        isAccepted = isAccepted,
        score = score,
        body = body,
        creationDateEpochSec = creationDateEpochSec,
        lastActivityEpochSec = lastActivityEpochSec,
        lastEditEpochSec = lastEditEpochSec,
        ownerDisplayName = ownerDisplayName,
        ownerProfileImage = ownerProfileImage,
        ownerReputation = ownerReputation,
    )

    companion object {
        private const val ERROR = "Error"
        private const val ERROR_MESSAGE = "Failed to load answers"
        private const val MOCK_ANSWER_ID = 101L
        private const val MOCK_QUESTION_ID = 123L
        private const val MOCK_IS_ACCEPTED = true
        private const val MOCK_SCORE = 42
        private const val MOCK_BODY = "<p>Answer body</p>"
        private const val MOCK_CREATION_EPOCH = 1_700_000_000L
        private const val MOCK_LAST_ACTIVITY_EPOCH = 1_700_000_100L
        private const val MOCK_LAST_EDIT_EPOCH = 1_700_000_200L
        private const val MOCK_OWNER_NAME = "Owner Name"
        private const val MOCK_OWNER_IMAGE = "https://example.com/p.png"
        private const val MOCK_OWNER_REPUTATION = 1234
    }
}
