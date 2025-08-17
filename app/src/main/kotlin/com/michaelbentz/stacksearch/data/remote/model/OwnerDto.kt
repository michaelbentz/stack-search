package com.michaelbentz.stacksearch.data.remote.model

import com.google.gson.annotations.SerializedName

data class OwnerDto(
    @SerializedName("display_name") val displayName: String?,
    @SerializedName("profile_image") val profileImage: String?,
    @SerializedName("reputation") val reputation: Int?,
)
