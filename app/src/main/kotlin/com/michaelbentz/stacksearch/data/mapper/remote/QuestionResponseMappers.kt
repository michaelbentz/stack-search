package com.michaelbentz.stacksearch.data.mapper.remote

import com.michaelbentz.stacksearch.data.local.entity.QuestionEntity
import com.michaelbentz.stacksearch.data.remote.model.QuestionSearchResponse
import com.michaelbentz.stacksearch.data.util.unescapeHtml

internal fun QuestionSearchResponse.toEntities(): List<QuestionEntity> {
    return items.map { question ->
        QuestionEntity(
            questionId = question.questionId,
            title = question.title.unescapeHtml(),
            body = question.body,
            ownerDisplayName = question.questionOwnerDto.displayName?.unescapeHtml(),
            ownerReputation = question.questionOwnerDto.reputation,
            ownerProfileImage = question.questionOwnerDto.profileImage,
            tags = question.tags,
            answerCount = question.answerCount,
            score = question.score,
            viewCount = question.viewCount,
            creationDateEpochSec = question.creationDateEpochSec,
            lastActivityEpochSec = question.lastActivityEpochSec,
            lastEditEpochSec = question.lastEditEpochSec,
            hasAcceptedAnswer = question.isAnswered,
        )
    }
}
