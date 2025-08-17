package com.michaelbentz.stacksearch.data.remote.model

import com.google.gson.annotations.SerializedName

data class QuestionSearchItemDto(
    @SerializedName("question_id") val questionId: Long,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String,
    @SerializedName("tags") val tags: List<String>,
    @SerializedName("answer_count") val answerCount: Int,
    @SerializedName("score") val score: Int,
    @SerializedName("view_count") val viewCount: Int,
    @SerializedName("creation_date") val creationDateEpochSec: Long,
    @SerializedName("last_activity_date") val lastActivityEpochSec: Long,
    @SerializedName("last_edit_date") val lastEditEpochSec: Long?,
    @SerializedName("is_answered") val isAnswered: Boolean,
    @SerializedName("owner") val questionOwnerDto: OwnerDto,
)
