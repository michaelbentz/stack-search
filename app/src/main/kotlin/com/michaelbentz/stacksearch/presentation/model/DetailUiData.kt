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
