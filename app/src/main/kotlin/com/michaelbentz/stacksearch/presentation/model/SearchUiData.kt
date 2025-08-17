package com.michaelbentz.stacksearch.presentation.model

data class SearchUiData(
    val query: String = "",
    val questions: List<QuestionUiData> = emptyList(),
)
