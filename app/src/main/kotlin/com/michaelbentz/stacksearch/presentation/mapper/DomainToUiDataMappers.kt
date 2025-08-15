package com.michaelbentz.stacksearch.presentation.mapper

import com.michaelbentz.stacksearch.domain.model.Answer
import com.michaelbentz.stacksearch.domain.model.Question
import com.michaelbentz.stacksearch.presentation.model.AnswerUiData
import com.michaelbentz.stacksearch.presentation.model.DetailUiData
import com.michaelbentz.stacksearch.presentation.model.QuestionItemUiData
import com.michaelbentz.stacksearch.presentation.util.htmlToPlainText
import com.michaelbentz.stacksearch.presentation.util.oneLineExcerpt
import com.michaelbentz.stacksearch.presentation.util.stripHtml
import com.michaelbentz.stacksearch.presentation.util.toTimeAgo
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

internal fun Question.toQuestionItemUiData(
    dateTimeFormatter: DateTimeFormatter,
): QuestionItemUiData = QuestionItemUiData(
    id = id,
    title = title,
    excerpt = body.htmlToPlainText().oneLineExcerpt(340),
    owner = ownerDisplayName.orEmpty(),
    askedDate = creationDateEpochSec.format(dateTimeFormatter),
    answers = answerCount,
    votes = score,
    views = viewCount,
)

internal fun Question.toDetailUiData(
    answers: List<Answer>,
    dateTimeFormatter: DateTimeFormatter,
): DetailUiData = DetailUiData(
    id = id,
    title = title,
    askedDate = creationDateEpochSec.toTimeAgo(),
    modifiedDate = (lastEditEpochSec ?: lastActivityEpochSec).toTimeAgo(),
    askedExact = creationDateEpochSec.format(dateTimeFormatter),
    views = viewCount,
    votes = score,
    body = body.stripHtml().take(340),
    tags = tags,
    authorName = ownerDisplayName.orEmpty(),
    authorReputation = ownerReputation ?: 0,
    authorAvatarUrl = ownerProfileImage,
    answers = answers.map {
        it.toAnswerUiData(dateTimeFormatter)
    }
)

private fun Answer.toAnswerUiData(
    dateTimeFormatter: DateTimeFormatter,
): AnswerUiData = AnswerUiData(
    id = id,
    isAccepted = isAccepted,
    score = score,
    body = body.stripHtml().take(340),
    authorName = ownerDisplayName.orEmpty(),
    reputation = ownerReputation ?: 0,
    created = creationDateEpochSec.format(dateTimeFormatter),
    avatarUrl = ownerProfileImage,
)

private fun Long.format(formatter: DateTimeFormatter): String =
    Instant.ofEpochSecond(this).atZone(ZoneId.systemDefault()).format(formatter)
