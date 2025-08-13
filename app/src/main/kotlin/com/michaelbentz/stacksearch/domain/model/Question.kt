package com.michaelbentz.stacksearch.domain.model

data class Question(
    val id: Long,
    val title: String,
    val body: String?,
    val ownerName: String,
    val answerCount: Int,
    val score: Int,
    val viewCount: Int,
    val creationDateEpochSec: Long,
    val lastActivityEpochSec: Long
)
