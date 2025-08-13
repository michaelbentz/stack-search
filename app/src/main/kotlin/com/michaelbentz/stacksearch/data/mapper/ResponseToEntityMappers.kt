package com.michaelbentz.stacksearch.data.mapper

import com.michaelbentz.stacksearch.data.local.entity.QuestionEntity
import com.michaelbentz.stacksearch.data.remote.model.QuestionSearchResponse

internal fun QuestionSearchResponse.toEntities(): List<QuestionEntity> {
    return items.map { question ->
        QuestionEntity(
            questionId = question.questionId,
            title = question.title,
            body = question.body,
            ownerDisplayName = question.owner.displayName,
            answerCount = question.answerCount,
            score = question.score,
            viewCount = question.viewCount,
            creationDateEpochSec = question.creationDateEpochSec,
            lastActivityEpochSec = question.lastActivityEpochSec
        )
    }
}
