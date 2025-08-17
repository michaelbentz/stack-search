package com.michaelbentz.stacksearch.presentation.mapper.answer

import com.michaelbentz.stacksearch.domain.model.Answer
import com.michaelbentz.stacksearch.presentation.model.AnswerUiData
import com.michaelbentz.stacksearch.presentation.util.format
import com.michaelbentz.stacksearch.presentation.util.formatThousands
import com.michaelbentz.stacksearch.presentation.util.formatVotes
import java.time.format.DateTimeFormatter

fun Answer.toAnswerUiData(
    dateTimeFormatter: DateTimeFormatter,
): AnswerUiData = AnswerUiData(
    id = id,
    isAccepted = isAccepted,
    score = score.toString(),
    scoreText = score.formatVotes(),
    body = body,
    authorName = ownerDisplayName.orEmpty(),
    reputation = (ownerReputation ?: 0).formatThousands(),
    created = creationDateEpochSec.format(dateTimeFormatter),
    avatarUrl = ownerProfileImage,
)
