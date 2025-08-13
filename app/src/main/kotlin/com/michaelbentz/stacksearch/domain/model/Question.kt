package com.michaelbentz.stacksearch.domain.model

data class Question(
    val id: Long,
    val title: String,
    val body: String?,
    val ownerName: String,
    val ownerId: Long?,
    val ownerAvatar: String?,
    val link: String,
    val tags: List<String>,
    val isAnswered: Boolean,
    val answerCount: Int,
    val score: Int,
    val viewCount: Int,
    val creationDateEpochSec: Long,
    val lastActivityEpochSec: Long
)
