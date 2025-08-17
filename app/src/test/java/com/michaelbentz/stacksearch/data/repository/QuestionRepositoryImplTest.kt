package com.michaelbentz.stacksearch.data.repository

import com.michaelbentz.stacksearch.data.local.dao.QuestionDao
import com.michaelbentz.stacksearch.data.local.entity.QuestionEntity
import com.michaelbentz.stacksearch.data.remote.model.OwnerDto
import com.michaelbentz.stacksearch.data.remote.model.QuestionSearchItemDto
import com.michaelbentz.stacksearch.data.remote.model.QuestionSearchResponse
import com.michaelbentz.stacksearch.data.remote.service.QuestionService
import com.michaelbentz.stacksearch.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class QuestionRepositoryImplTest {
    @Mock
    private lateinit var questionService: QuestionService

    @Mock
    private lateinit var questionDao: QuestionDao

    private lateinit var repository: QuestionRepositoryImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = QuestionRepositoryImpl(questionService, questionDao)
    }

    @Test
    fun `fetchNewestQuestions emits Loading then Success and calls replaceAll`() = runTest {
        val response = buildMockQuestionSearchResponse()
        whenever(questionService.getNewestQuestions()).thenReturn(response)

        val emissions = repository.fetchNewestQuestions().toList()
        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Success)
        verify(questionDao).replaceAll(any())
    }

    @Test
    fun `fetchNewestQuestions emits Error on service exception`() = runTest {
        whenever(questionService.getNewestQuestions()).thenThrow(RuntimeException(ERROR))

        val emissions = repository.fetchNewestQuestions().toList()
        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Error)
        assertEquals(ERROR_MESSAGE, (emissions[1] as Resource.Error).message)
        assertTrue((emissions[1] as Resource.Error).throwable is RuntimeException)
    }

    @Test
    fun `searchQuestions emits Loading then Success and calls replaceAll`() = runTest {
        val query = "kotlin coroutines"
        val response = buildMockQuestionSearchResponse()
        whenever(questionService.searchQuestions(query)).thenReturn(response)

        val emissions = repository.searchQuestions(query).toList()
        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Success)
        verify(questionDao).replaceAll(any())
    }

    @Test
    fun `searchQuestions emits Error on service exception`() = runTest {
        val query = "compose"
        whenever(questionService.searchQuestions(query)).thenThrow(RuntimeException(ERROR))

        val emissions = repository.searchQuestions(query).toList()
        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Error)
        assertEquals(ERROR_MESSAGE, (emissions[1] as Resource.Error).message)
        assertTrue((emissions[1] as Resource.Error).throwable is RuntimeException)
    }

    @Test
    fun `getQuestions returns mapped list from entities`() = runTest {
        val entities =
            listOf(
                buildMockQuestionEntity(),
                buildMockQuestionEntity(questionId = 202L, title = "Another")
            )
        whenever(questionDao.getAll()).thenReturn(flowOf(entities))

        val questions = repository.getQuestions().first()
        assertEquals(2, questions.size)

        val question = questions[0]
        assertEquals(MOCK_QUESTION_ID, question.id)
        assertEquals(MOCK_TITLE, question.title)
        assertEquals(MOCK_BODY, question.body)
        assertEquals(MOCK_OWNER_NAME, question.ownerDisplayName)
        assertEquals(MOCK_OWNER_IMAGE, question.ownerProfileImage)
        assertEquals(MOCK_OWNER_REPUTATION, question.ownerReputation)
        assertEquals(MOCK_TAGS, question.tags)
        assertEquals(MOCK_ANSWER_COUNT, question.answerCount)
        assertEquals(MOCK_SCORE, question.score)
        assertEquals(MOCK_VIEW_COUNT, question.viewCount)
        assertEquals(MOCK_CREATION_EPOCH, question.creationDateEpochSec)
        assertEquals(MOCK_LAST_ACTIVITY_EPOCH, question.lastActivityEpochSec)
        assertEquals(MOCK_LAST_EDIT_EPOCH, question.lastEditEpochSec)
        assertEquals(MOCK_IS_ANSWERED, question.hasAcceptedAnswer)
    }

    @Test
    fun `getQuestionById returns mapped Question`() = runTest {
        val questionId = MOCK_QUESTION_ID
        whenever(questionDao.getById(questionId)).thenReturn(flowOf(buildMockQuestionEntity()))

        val question = repository.getQuestionById(questionId).first()
        assertNotNull(question)
        requireNotNull(question)
        assertEquals(MOCK_QUESTION_ID, question.id)
        assertEquals(MOCK_TITLE, question.title)
    }

    @Test
    fun `getQuestionById returns null when entity is null`() = runTest {
        val id = 999L
        whenever(questionDao.getById(id)).thenReturn(flowOf(null))

        val question = repository.getQuestionById(id).first()
        assertNull(question)
    }

    private fun buildMockQuestionSearchResponse(
        items: List<QuestionSearchItemDto> = listOf(buildMockQuestionSearchItemDto()),
    ): QuestionSearchResponse = QuestionSearchResponse(items = items)

    private fun buildMockQuestionSearchItemDto(
        questionId: Long = MOCK_QUESTION_ID,
        title: String = MOCK_TITLE,
        body: String = MOCK_BODY,
        tags: List<String> = MOCK_TAGS,
        answerCount: Int = MOCK_ANSWER_COUNT,
        score: Int = MOCK_SCORE,
        viewCount: Int = MOCK_VIEW_COUNT,
        creationDateEpochSec: Long = MOCK_CREATION_EPOCH,
        lastActivityEpochSec: Long = MOCK_LAST_ACTIVITY_EPOCH,
        lastEditEpochSec: Long? = MOCK_LAST_EDIT_EPOCH,
        isAnswered: Boolean = MOCK_IS_ANSWERED,
        owner: OwnerDto = buildMockOwnerDto(),
    ): QuestionSearchItemDto = QuestionSearchItemDto(
        questionId = questionId,
        title = title,
        body = body,
        tags = tags,
        answerCount = answerCount,
        score = score,
        viewCount = viewCount,
        creationDateEpochSec = creationDateEpochSec,
        lastActivityEpochSec = lastActivityEpochSec,
        lastEditEpochSec = lastEditEpochSec,
        isAnswered = isAnswered,
        questionOwnerDto = owner,
    )

    private fun buildMockOwnerDto(
        displayName: String? = MOCK_OWNER_NAME,
        profileImage: String? = MOCK_OWNER_IMAGE,
        reputation: Int? = MOCK_OWNER_REPUTATION,
    ) = OwnerDto(
        displayName = displayName,
        profileImage = profileImage,
        reputation = reputation,
    )

    private fun buildMockQuestionEntity(
        questionId: Long = MOCK_QUESTION_ID,
        title: String = MOCK_TITLE,
        body: String = MOCK_BODY,
        ownerDisplayName: String? = MOCK_OWNER_NAME,
        ownerReputation: Int? = MOCK_OWNER_REPUTATION,
        ownerProfileImage: String? = MOCK_OWNER_IMAGE,
        tags: List<String> = MOCK_TAGS,
        answerCount: Int = MOCK_ANSWER_COUNT,
        score: Int = MOCK_SCORE,
        viewCount: Int = MOCK_VIEW_COUNT,
        creationDateEpochSec: Long = MOCK_CREATION_EPOCH,
        lastActivityEpochSec: Long = MOCK_LAST_ACTIVITY_EPOCH,
        lastEditEpochSec: Long? = MOCK_LAST_EDIT_EPOCH,
        hasAcceptedAnswer: Boolean = MOCK_IS_ANSWERED,
    ) = QuestionEntity(
        questionId = questionId,
        title = title,
        body = body,
        ownerDisplayName = ownerDisplayName,
        ownerReputation = ownerReputation,
        ownerProfileImage = ownerProfileImage,
        tags = tags,
        answerCount = answerCount,
        score = score,
        viewCount = viewCount,
        creationDateEpochSec = creationDateEpochSec,
        lastActivityEpochSec = lastActivityEpochSec,
        lastEditEpochSec = lastEditEpochSec,
        hasAcceptedAnswer = hasAcceptedAnswer,
    )

    companion object {
        private const val ERROR = "Error"
        private const val ERROR_MESSAGE = "Failed to load questions"
        private const val MOCK_QUESTION_ID = 111L
        private const val MOCK_TITLE = "How to test coroutines?"
        private const val MOCK_BODY = "<p>Body with <code>code</code></p>"
        private val MOCK_TAGS = listOf("kotlin", "coroutines", "unit-testing")
        private const val MOCK_ANSWER_COUNT = 3
        private const val MOCK_SCORE = 10
        private const val MOCK_VIEW_COUNT = 1234
        private const val MOCK_CREATION_EPOCH = 1_700_000_000L
        private const val MOCK_LAST_ACTIVITY_EPOCH = 1_700_000_200L
        private const val MOCK_LAST_EDIT_EPOCH = 1_700_000_150L
        private const val MOCK_IS_ANSWERED = true
        private const val MOCK_OWNER_NAME = "Owner Name"
        private const val MOCK_OWNER_IMAGE = "https://example.com/avatar.png"
        private const val MOCK_OWNER_REPUTATION = 9001
    }
}
