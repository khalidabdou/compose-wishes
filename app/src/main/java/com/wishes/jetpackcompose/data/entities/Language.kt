package com.wishes.jetpackcompose.data.entities

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable


@Serializable
data class AppLanguage(
    val id :Int,
    val name :String,
)
@Serializable
data class AppLanguageWrapper(
    @SerializedName("AppLanguage") val appLanguage: AppLanguage
)