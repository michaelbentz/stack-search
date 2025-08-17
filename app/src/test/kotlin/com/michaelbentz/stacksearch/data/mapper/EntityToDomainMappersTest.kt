package com.michaelbentz.stacksearch.data.mapper

import com.michaelbentz.stacksearch.data.local.entity.AnswerEntity
import com.michaelbentz.stacksearch.data.local.entity.QuestionEntity
import com.michaelbentz.stacksearch.data.mapper.entity.toDomain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EntityToDomainMappersTest {

    @Test
    fun `AnswerEntity maps to Answer domain correctly`() {
        val entity = AnswerEntity(
            answerId = 101L,
            questionId = 111L,
            isAccepted = true,
            score = 42,
            body = "<p>Answer body</p>",
            creationDateEpochSec = 1_700_000_000L,
            lastActivityEpochSec = 1_700_000_100L,
            lastEditEpochSec = 1_700_000_200L,
            ownerDisplayName = "One &amp; Two",
            ownerProfileImage = "https://img",
            ownerReputation = 1234,
        )

        val domain = entity.toDomain()
        assertEquals(101L, domain.id)
        assertEquals(111L, domain.questionId)
        assertTrue(domain.isAccepted)
        assertEquals(42, domain.score)
        assertEquals("<p>Answer body</p>", domain.body)
        assertEquals(1_700_000_000L, domain.creationDateEpochSec)
        assertEquals(1_700_000_100L, domain.lastActivityEpochSec)
        assertEquals(1_700_000_200L, domain.lastEditEpochSec)
        assertEquals("One &amp; Two", domain.ownerDisplayName)
        assertEquals("https://img", domain.ownerProfileImage)
        assertEquals(1234, domain.ownerReputation)
    }

    @Test
    fun `QuestionEntity maps to Question domain correctly`() {
        val entity = QuestionEntity(
            questionId = 111L,
            title = "Title &amp; more",
            body = "<p>Body</p>",
            ownerDisplayName = "One &amp; Two",
            ownerReputation = 9001,
            ownerProfileImage = "https://avatar",
            tags = listOf("kotlin", "coroutines"),
            answerCount = 3,
            score = 10,
            viewCount = 1234,
            creationDateEpochSec = 1_700_000_000L,
            lastActivityEpochSec = 1_700_000_200L,
            lastEditEpochSec = 1_700_000_150L,
            hasAcceptedAnswer = true
        )

        val domain = entity.toDomain()
        assertEquals(111L, domain.id)
        assertEquals("Title &amp; more", domain.title)
        assertEquals("<p>Body</p>", domain.body)
        assertEquals("One &amp; Two", domain.ownerDisplayName)
        assertEquals(9001, domain.ownerReputation)
        assertEquals("https://avatar", domain.ownerProfileImage)
        assertEquals(listOf("kotlin", "coroutines"), domain.tags)
        assertEquals(3, domain.answerCount)
        assertEquals(10, domain.score)
        assertEquals(1234, domain.viewCount)
        assertEquals(1_700_000_000L, domain.creationDateEpochSec)
        assertEquals(1_700_000_200L, domain.lastActivityEpochSec)
        assertEquals(1_700_000_150L, domain.lastEditEpochSec)
        assertTrue(domain.hasAcceptedAnswer)
    }
}
