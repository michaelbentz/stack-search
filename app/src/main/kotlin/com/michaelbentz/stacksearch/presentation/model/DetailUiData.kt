package com.michaelbentz.stacksearch.presentation.model

data class DetailUiData(
    val id: Long,
    val title: String,
    val askedDate: String,
    val views: Int,
    val votes: Int,
    val answers: List<AnswerUiData>
)

data class AnswerUiData(
    val id: Long,
    val isAccepted: Boolean,
    val score: Int,
    val bodyHtml: String,
    val author: String,
    val reputation: Int,
    val created: String
)
