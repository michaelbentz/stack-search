package com.michaelbentz.stacksearch.data.mapper

import com.michaelbentz.stacksearch.data.local.entity.AnswerEntity
import com.michaelbentz.stacksearch.data.local.entity.QuestionEntity
import com.michaelbentz.stacksearch.data.remote.model.AnswersResponse
import com.michaelbentz.stacksearch.data.remote.model.QuestionSearchResponse

internal fun QuestionSearchResponse.toEntities(): List<QuestionEntity> {
    return items.map { question ->
        QuestionEntity(
            questionId = question.questionId,
            title = question.title,
            body = question.body,
            ownerDisplayName = question.questionOwner.displayName,
            answerCount = question.answerCount,
            score = question.score,
            viewCount = question.viewCount,
            creationDateEpochSec = question.creationDateEpochSec,
            lastActivityEpochSec = question.lastActivityEpochSec
        )
    }
}

internal fun AnswersResponse.toEntities(): List<AnswerEntity> {
    return items.mapNotNull { item ->
        val questionId = item.questionId ?: return@mapNotNull null
        AnswerEntity(
            answerId = item.answerId,
            questionId = questionId,
            isAccepted = item.isAccepted,
            score = item.score,
            bodyHtml = item.body,
            creationDateEpochSec = item.creationDateEpochSec,
            lastActivityEpochSec = item.lastActivityEpochSec,
            lastEditEpochSec = item.lastEditEpochSec,
            ownerDisplayName = item.answerOwner.displayName,
            ownerProfileImage = item.answerOwner.profileImage,
            ownerReputation = item.answerOwner.reputation
        )
    }
}
