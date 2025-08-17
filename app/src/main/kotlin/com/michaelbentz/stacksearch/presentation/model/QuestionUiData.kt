package com.michaelbentz.stacksearch.presentation.model

data class QuestionUiData(
    val id: Long,
    val title: String,
    val excerpt: String,
    val owner: String,
    val askedDate: String,
    val answers: Int,
    val votes: Int,
    val views: Int,
    val isAccepted: Boolean,
)
