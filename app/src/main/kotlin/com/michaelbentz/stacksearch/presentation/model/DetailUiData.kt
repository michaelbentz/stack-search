package com.michaelbentz.stacksearch.presentation.model

data class DetailUiData(
    val id: Long,
    val title: String,
    val askedDate: String,
    val askedExact: String,
    val modifiedDate: String,
    val views: Int,
    val body: String,
    val votes: Int,
    val tags: List<String>,
    val authorName: String,
    val authorReputation: Int,
    val authorAvatarUrl: String?,
    val answers: List<AnswerUiData>,
)

data class AnswerUiData(
    val id: Long,
    val isAccepted: Boolean,
    val score: Int,
    val body: String,
    val authorName: String,
    val reputation: Int,
    val created: String,
    val avatarUrl: String?,
)
