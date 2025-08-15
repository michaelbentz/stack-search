package com.michaelbentz.stacksearch.domain.model

data class Question(
    val id: Long,
    val title: String,
    val body: String,
    val ownerDisplayName: String?,
    val ownerReputation: Int?,
    val ownerProfileImage: String?,
    val tags: List<String>,
    val answerCount: Int,
    val score: Int,
    val viewCount: Int,
    val creationDateEpochSec: Long,
    val lastActivityEpochSec: Long,
    val lastEditEpochSec: Long?,
)
