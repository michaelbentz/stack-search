package com.michaelbentz.stacksearch.data.mapper.entity

import com.michaelbentz.stacksearch.data.local.entity.QuestionEntity
import com.michaelbentz.stacksearch.domain.model.Question

internal fun QuestionEntity.toDomain() = Question(
    id = questionId,
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
