package com.michaelbentz.stacksearch.presentation.mapper

import com.michaelbentz.stacksearch.domain.model.Question
import com.michaelbentz.stacksearch.presentation.model.QuestionItemUiData
import java.time.Instant
import java.time.format.DateTimeFormatter

fun Question.toUiData(
    dateTimeFormatter: DateTimeFormatter,
): QuestionItemUiData = QuestionItemUiData(
    id = id,
    title = title,
    excerpt = (body ?: "").stripHtml().trim().take(160),
    owner = ownerName,
    askedDate = dateTimeFormatter.format(Instant.ofEpochSecond(creationDateEpochSec)),
    answers = answerCount,
    votes = score,
    views = viewCount,
)

private fun String.stripHtml(): String = replace(Regex("<[^>]*>"), " ").replace(Regex("\\s+"), " ")
