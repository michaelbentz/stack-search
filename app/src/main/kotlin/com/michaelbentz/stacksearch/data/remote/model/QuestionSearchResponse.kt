package com.michaelbentz.stacksearch.data.remote.model

import com.google.gson.annotations.SerializedName

data class QuestionSearchResponse(
    @SerializedName("items") val items: List<QuestionSearchItemDto>,
)
