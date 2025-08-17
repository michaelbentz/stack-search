package com.michaelbentz.stacksearch.data.remote.model

import com.google.gson.annotations.SerializedName

data class AnswersResponse(
    @SerializedName("items") val items: List<AnswerItemDto>,
)
