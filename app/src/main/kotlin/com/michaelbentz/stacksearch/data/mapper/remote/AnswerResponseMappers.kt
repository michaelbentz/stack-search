package com.michaelbentz.stacksearch.data.mapper.remote

import com.michaelbentz.stacksearch.data.local.entity.AnswerEntity
import com.michaelbentz.stacksearch.data.remote.model.AnswersResponse
import com.michaelbentz.stacksearch.data.util.unescapeHtml

internal fun AnswersResponse.toEntities(): List<AnswerEntity> {
    return items.mapNotNull { item ->
        val questionId = item.questionId ?: return@mapNotNull null
        AnswerEntity(
            answerId = item.answerId,
            questionId = questionId,
            isAccepted = item.isAccepted,
            score = item.score,
            body = item.body,
            creationDateEpochSec = item.creationDateEpochSec,
            lastActivityEpochSec = item.lastActivityEpochSec,
            lastEditEpochSec = item.lastEditEpochSec,
            ownerDisplayName = item.answerOwnerDto.displayName?.unescapeHtml(),
            ownerProfileImage = item.answerOwnerDto.profileImage,
            ownerReputation = item.answerOwnerDto.reputation,
        )
    }
}
