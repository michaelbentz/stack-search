package com.michaelbentz.stacksearch.data.remote.model

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("items") val items: List<Question>,
    @SerializedName("has_more") val hasMore: Boolean,
    @SerializedName("quota_max") val quotaMax: Int,
    @SerializedName("quota_remaining") val quotaRemaining: Int,
)

data class Question(
    @SerializedName("question_id") val questionId: Long,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String?,
    @SerializedName("link") val link: String,
    @SerializedName("tags") val tags: List<String>,
    @SerializedName("owner") val owner: Owner,
    @SerializedName("is_answered") val isAnswered: Boolean,
    @SerializedName("answer_count") val answerCount: Int,
    @SerializedName("score") val score: Int,
    @SerializedName("view_count") val viewCount: Int,
    @SerializedName("creation_date") val creationDateEpochSec: Long,
    @SerializedName("last_activity_date") val lastActivityEpochSec: Long,
)

data class Owner(
    @SerializedName("user_id") val userId: Long?,
    @SerializedName("display_name") val displayName: String,
    @SerializedName("profile_image") val profileImage: String?,
    @SerializedName("link") val link: String?,
)
