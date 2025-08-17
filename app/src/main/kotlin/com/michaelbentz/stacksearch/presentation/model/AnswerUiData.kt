package com.michaelbentz.stacksearch.presentation.model

data class AnswerUiData(
    val id: Long,
    val isAccepted: Boolean,
    val score: String,
    val scoreText: String,
    val body: String,
    val authorName: String,
    val reputation: String,
    val created: String,
    val avatarUrl: String?,
)
