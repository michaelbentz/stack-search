package com.michaelbentz.stacksearch.presentation.mapper

import com.michaelbentz.stacksearch.domain.model.Answer
import com.michaelbentz.stacksearch.domain.model.Question
import com.michaelbentz.stacksearch.presentation.model.AnswerUiData
import com.michaelbentz.stacksearch.presentation.model.DetailUiData
import com.michaelbentz.stacksearch.presentation.model.QuestionItemUiData
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

internal fun Question.toQuestionItemUiData(
    dateTimeFormatter: DateTimeFormatter,
): QuestionItemUiData = QuestionItemUiData(
    id = id,
    title = title,
    excerpt = body.stripHtml().trim().take(160),
    owner = ownerName,
    askedDate = creationDateEpochSec.format(dateTimeFormatter),
    answers = answerCount,
    votes = score,
    views = viewCount,
)

internal fun Question.toDetailUiData(
    answers: List<Answer>,
    formatter: DateTimeFormatter
): DetailUiData = DetailUiData(
    id = id,
    title = title,
    askedDate = creationDateEpochSec.format(formatter),
    activeDate = lastActivityEpochSec.format(formatter),
    views = viewCount,
    votes = score,
    body = body,
    tags = tags,
    authorName = ownerName,
    authorReputation = ownerReputation ?: 0,
    authorAvatarUrl = ownerProfileImage,
    answers = answers.map {
        it.toAnswerUiData(formatter)
    }
)

private fun Answer.toAnswerUiData(
    dateTimeFormatter: DateTimeFormatter
): AnswerUiData = AnswerUiData(
    id = id,
    isAccepted = isAccepted,
    score = score,
    body = body,
    author = ownerDisplayName.orEmpty(),
    reputation = ownerReputation ?: 0,
    created = creationDateEpochSec.format(dateTimeFormatter)
)

fun String.stripHtml(): String =
    replace(Regex("<[^>]*>"), " ").replace(Regex("\\s+"), " ")

private fun Long.format(formatter: DateTimeFormatter): String =
    Instant.ofEpochSecond(this).atZone(ZoneId.systemDefault()).format(formatter)
