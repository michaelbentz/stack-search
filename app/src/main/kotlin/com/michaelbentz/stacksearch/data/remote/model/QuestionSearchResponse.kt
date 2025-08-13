package com.michaelbentz.stacksearch.data.remote.model

import com.google.gson.annotations.SerializedName

data class QuestionSearchResponse(
    @SerializedName("items") val items: List<Question>,
)

data class Question(
    @SerializedName("question_id") val questionId: Long,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String?,
    @SerializedName("owner") val owner: Owner,
    @SerializedName("answer_count") val answerCount: Int,
    @SerializedName("score") val score: Int,
    @SerializedName("view_count") val viewCount: Int,
    @SerializedName("creation_date") val creationDateEpochSec: Long,
    @SerializedName("last_activity_date") val lastActivityEpochSec: Long,
)

data class Owner(
    @SerializedName("display_name") val displayName: String,
)
