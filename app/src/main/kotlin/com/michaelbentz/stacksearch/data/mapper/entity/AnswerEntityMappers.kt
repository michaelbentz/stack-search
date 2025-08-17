package com.michaelbentz.stacksearch.data.mapper.entity

import com.michaelbentz.stacksearch.data.local.entity.AnswerEntity
import com.michaelbentz.stacksearch.domain.model.Answer

fun AnswerEntity.toDomain(): Answer = Answer(
    id = answerId,
    questionId = questionId,
    isAccepted = isAccepted,
    score = score,
    body = body,
    creationDateEpochSec = creationDateEpochSec,
    lastActivityEpochSec = lastActivityEpochSec,
    lastEditEpochSec = lastEditEpochSec,
    ownerDisplayName = ownerDisplayName,
    ownerProfileImage = ownerProfileImage,
    ownerReputation = ownerReputation,
)
