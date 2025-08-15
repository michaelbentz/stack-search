package com.michaelbentz.stacksearch.data.remote.model

import com.google.gson.annotations.SerializedName

data class QuestionSearchResponse(
    @SerializedName("items") val items: List<QuestionSearchItem>,
)

data class QuestionSearchItem(
    @SerializedName("question_id") val questionId: Long,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String,
    @SerializedName("owner") val questionOwner: QuestionOwner,
    @SerializedName("tags") val tags: List<String>,
    @SerializedName("answer_count") val answerCount: Int,
    @SerializedName("score") val score: Int,
    @SerializedName("view_count") val viewCount: Int,
    @SerializedName("creation_date") val creationDateEpochSec: Long,
    @SerializedName("last_activity_date") val lastActivityEpochSec: Long,
    @SerializedName("last_edit_date") val lastEditEpochSec: Long?,
)

data class QuestionOwner(
    @SerializedName("display_name") val displayName: String,
    @SerializedName("reputation") val reputation: Int?,
    @SerializedName("profile_image") val profileImage: String?,
)
