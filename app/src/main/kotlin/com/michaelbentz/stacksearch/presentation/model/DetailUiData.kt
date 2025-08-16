package com.michaelbentz.stacksearch.presentation.model

data class DetailUiData(
    val id: Long,
    val title: String,
    val askedDate: String,
    val askedExact: String,
    val modifiedDate: String,
    val views: String,
    val body: String,
    val votes: Int,
    val tags: List<String>,
    val authorName: String,
    val authorReputation: String,
    val authorAvatarUrl: String?,
    val answers: List<AnswerUiData>,
)

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
