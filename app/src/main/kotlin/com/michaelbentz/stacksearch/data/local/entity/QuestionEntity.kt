package com.michaelbentz.stacksearch.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.michaelbentz.stacksearch.data.local.converter.TagsConverter

@Entity(tableName = "question")
@TypeConverters(TagsConverter::class)
data class QuestionEntity(
    @PrimaryKey val questionId: Long,
    val title: String,
    val body: String?,
    val ownerDisplayName: String,
    val ownerUserId: Long?,
    val ownerProfileImage: String?,
    val link: String,
    val tags: List<String>,
    val isAnswered: Boolean,
    val answerCount: Int,
    val score: Int,
    val viewCount: Int,
    val creationDateEpochSec: Long,
    val lastActivityEpochSec: Long,
)
