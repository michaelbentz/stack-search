package com.michaelbentz.stacksearch.data.remote.model

import com.google.gson.annotations.SerializedName

data class AnswerItemDto(
    @SerializedName("is_accepted") val isAccepted: Boolean,
    @SerializedName("score") val score: Int,
    @SerializedName("answer_id") val answerId: Long,
    @SerializedName("question_id") val questionId: Long?,
    @SerializedName("body") val body: String,
    @SerializedName("creation_date") val creationDateEpochSec: Long,
    @SerializedName("last_activity_date") val lastActivityEpochSec: Long?,
    @SerializedName("last_edit_date") val lastEditEpochSec: Long?,
    @SerializedName("owner") val answerOwnerDto: OwnerDto,
)
