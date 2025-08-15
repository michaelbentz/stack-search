package com.michaelbentz.stacksearch.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "question")
data class QuestionEntity(
    @PrimaryKey val questionId: Long,
    val title: String,
    val body: String,
    val ownerDisplayName: String,
    val ownerReputation: Int?,
    val ownerProfileImage: String?,
    val tags: List<String>,
    val answerCount: Int,
    val score: Int,
    val viewCount: Int,
    val creationDateEpochSec: Long,
    val lastActivityEpochSec: Long,
)
