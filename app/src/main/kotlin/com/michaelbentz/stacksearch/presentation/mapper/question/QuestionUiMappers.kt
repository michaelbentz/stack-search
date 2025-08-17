package com.michaelbentz.stacksearch.presentation.mapper.question

import com.michaelbentz.stacksearch.domain.model.Answer
import com.michaelbentz.stacksearch.domain.model.Question
import com.michaelbentz.stacksearch.presentation.mapper.answer.toAnswerUiData
import com.michaelbentz.stacksearch.presentation.model.DetailUiData
import com.michaelbentz.stacksearch.presentation.model.QuestionUiData
import com.michaelbentz.stacksearch.presentation.util.format
import com.michaelbentz.stacksearch.presentation.util.formatThousands
import com.michaelbentz.stacksearch.presentation.util.formatViews
import com.michaelbentz.stacksearch.presentation.util.htmlExcerpt
import com.michaelbentz.stacksearch.presentation.util.toTimeAgo
import java.time.format.DateTimeFormatter

internal fun Question.toQuestionItemUiData(
    dateTimeFormatter: DateTimeFormatter,
): QuestionUiData = QuestionUiData(
    id = id,
    title = title,
    excerpt = body.htmlExcerpt(340),
    owner = ownerDisplayName.orEmpty(),
    askedDate = creationDateEpochSec.format(dateTimeFormatter),
    answers = answerCount,
    votes = score,
    views = viewCount,
    isAccepted = hasAcceptedAnswer,
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
    views = viewCount.formatViews(),
    votes = score,
    body = body,
    tags = tags,
    authorName = ownerDisplayName.orEmpty(),
    authorReputation = (ownerReputation ?: 0).formatThousands(),
    authorAvatarUrl = ownerProfileImage,
    answers = answers.map {
        it.toAnswerUiData(dateTimeFormatter)
    }
)
