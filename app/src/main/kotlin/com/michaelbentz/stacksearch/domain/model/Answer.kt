package com.michaelbentz.stacksearch.domain.model

data class Answer(
    val id: Long,
    val questionId: Long,
    val isAccepted: Boolean,
    val score: Int,
    val body: String,
    val creationDateEpochSec: Long,
    val lastActivityEpochSec: Long?,
    val lastEditEpochSec: Long?,
    val ownerDisplayName: String?,
    val ownerProfileImage: String?,
    val ownerReputation: Int?
)
