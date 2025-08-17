package com.michaelbentz.stacksearch.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "answer",
    indices = [Index(value = ["questionId"])]
)
data class AnswerEntity(
    @PrimaryKey val answerId: Long,
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
