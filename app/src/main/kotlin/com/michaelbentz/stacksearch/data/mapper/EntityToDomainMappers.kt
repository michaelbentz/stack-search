package com.michaelbentz.stacksearch.data.mapper

import com.michaelbentz.stacksearch.data.local.entity.AnswerEntity
import com.michaelbentz.stacksearch.data.local.entity.QuestionEntity
import com.michaelbentz.stacksearch.domain.model.Answer
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

fun AnswerEntity.toDomain(): Answer = Answer(
    id = answerId,
    questionId = questionId,
    isAccepted = isAccepted,
    score = score,
    bodyHtml = bodyHtml,
    creationDateEpochSec = creationDateEpochSec,
    lastActivityEpochSec = lastActivityEpochSec,
    lastEditEpochSec = lastEditEpochSec,
    ownerDisplayName = ownerDisplayName,
    ownerProfileImage = ownerProfileImage,
    ownerReputation = ownerReputation
)
