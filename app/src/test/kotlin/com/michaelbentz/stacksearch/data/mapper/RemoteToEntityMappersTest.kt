package com.michaelbentz.stacksearch.data.mapper

import com.michaelbentz.stacksearch.data.mapper.remote.toEntities
import com.michaelbentz.stacksearch.data.remote.model.AnswerItemDto
import com.michaelbentz.stacksearch.data.remote.model.AnswersResponse
import com.michaelbentz.stacksearch.data.remote.model.OwnerDto
import com.michaelbentz.stacksearch.data.remote.model.QuestionSearchItemDto
import com.michaelbentz.stacksearch.data.remote.model.QuestionSearchResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RemoteToEntityMappersTest {

    @Test
    fun `AnswersResponse maps to AnswerEntity list, filters null questionId, unescapes owner name`() {
        val dtoWithQuestion = AnswerItemDto(
            isAccepted = true,
            score = 5,
            answerId = 201L,
            questionId = 777L,
            body = "<p>Body</p>",
            creationDateEpochSec = 1_700_000_000L,
            lastActivityEpochSec = 1_700_000_001L,
            lastEditEpochSec = null,
            answerOwnerDto = OwnerDto(
                displayName = "One &amp; Two",
                profileImage = "https://img",
                reputation = 321,
            )
        )
        val dtoWithoutQuestion = AnswerItemDto(
            isAccepted = false,
            score = 0,
            answerId = 202L,
            questionId = null,
            body = "x",
            creationDateEpochSec = 1L,
            lastActivityEpochSec = null,
            lastEditEpochSec = null,
            answerOwnerDto = OwnerDto(displayName = null, profileImage = null, reputation = null)
        )

        val response = AnswersResponse(items = listOf(dtoWithQuestion, dtoWithoutQuestion))
        val entities = response.toEntities()

        assertEquals(1, entities.size)
        val entity = entities.first()
        assertEquals(201L, entity.answerId)
        assertEquals(777L, entity.questionId)
        assertTrue(entity.isAccepted)
        assertEquals(5, entity.score)
        assertEquals("<p>Body</p>", entity.body)
        assertEquals(1_700_000_000L, entity.creationDateEpochSec)
        assertEquals(1_700_000_001L, entity.lastActivityEpochSec)
        assertNull(entity.lastEditEpochSec)
        assertEquals("One & Two", entity.ownerDisplayName)
        assertEquals("https://img", entity.ownerProfileImage)
        assertEquals(321, entity.ownerReputation)
    }

    @Test
    fun `QuestionSearchResponse maps to QuestionEntity list, unescapes title and owner name`() {
        val questionSearchItemDto = QuestionSearchItemDto(
            questionId = 333L,
            title = "Title &amp; stuff",
            body = "<p>Question body</p>",
            tags = listOf("android", "kotlin"),
            answerCount = 2,
            score = 7,
            viewCount = 456,
            creationDateEpochSec = 1_700_000_000L,
            lastActivityEpochSec = 1_700_000_100L,
            lastEditEpochSec = 1_700_000_050L,
            isAnswered = true,
            questionOwnerDto = OwnerDto(
                displayName = "Owner &amp; Name",
                profileImage = "https://avatar",
                reputation = 888,
            )
        )
        val response = QuestionSearchResponse(items = listOf(questionSearchItemDto))
        val entities = response.toEntities()
        assertEquals(1, entities.size)

        val entity = entities.first()
        assertEquals(333L, entity.questionId)
        assertEquals("Title & stuff", entity.title)
        assertEquals("<p>Question body</p>", entity.body)
        assertEquals("Owner & Name", entity.ownerDisplayName)
        assertEquals(888, entity.ownerReputation)
        assertEquals("https://avatar", entity.ownerProfileImage)
        assertEquals(listOf("android", "kotlin"), entity.tags)
        assertEquals(2, entity.answerCount)
        assertEquals(7, entity.score)
        assertEquals(456, entity.viewCount)
        assertEquals(1_700_000_000L, entity.creationDateEpochSec)
        assertEquals(1_700_000_100L, entity.lastActivityEpochSec)
        assertEquals(1_700_000_050L, entity.lastEditEpochSec)
        assertTrue(entity.hasAcceptedAnswer)
    }
}
