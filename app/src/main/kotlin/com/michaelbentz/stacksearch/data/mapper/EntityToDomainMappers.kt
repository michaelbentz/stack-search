package com.michaelbentz.stacksearch.data.mapper

import com.michaelbentz.stacksearch.data.local.entity.QuestionEntity
import com.michaelbentz.stacksearch.domain.model.Question

internal fun QuestionEntity.toDomain() = Question(
    id = questionId,
    title = title,
    body = body,
    ownerName = ownerDisplayName,
    answerCount = answerCount,
    score = score,
    viewCount = viewCount,
    creationDateEpochSec = creationDateEpochSec,
    lastActivityEpochSec = lastActivityEpochSec
)
