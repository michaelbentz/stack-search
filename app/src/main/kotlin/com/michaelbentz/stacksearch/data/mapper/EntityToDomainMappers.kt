package com.michaelbentz.stacksearch.data.mapper

import com.michaelbentz.stacksearch.data.local.entity.QuestionEntity
import com.michaelbentz.stacksearch.domain.model.Question

internal fun QuestionEntity.toDomain() = Question(
    id = questionId,
    title = title,
    body = body,
    ownerName = ownerDisplayName,
    ownerId = ownerUserId,
    ownerAvatar = ownerProfileImage,
    link = link,
    tags = tags,
    isAnswered = isAnswered,
    answerCount = answerCount,
    score = score,
    viewCount = viewCount,
    creationDateEpochSec = creationDateEpochSec,
    lastActivityEpochSec = lastActivityEpochSec
)
