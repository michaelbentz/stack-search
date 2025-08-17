package com.michaelbentz.stacksearch.presentation.model

data class SearchUiData(
    val inputQuery: String = "",
    val submittedQuery: String = "",
    val questions: List<QuestionUiData> = emptyList(),
)
