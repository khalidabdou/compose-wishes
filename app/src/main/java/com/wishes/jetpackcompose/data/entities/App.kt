package com.wishes.jetpackcompose.data.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


data class App(
    val id: Int,
    val name: String,
    val appPackage: String,
    val appUrl: String?,
    val iconUrl: String,
    val description: String,
    val largePhotoUrl: String,
    val privacyUrl: String,
)


data class Apps(
    @SerializedName(value = "admobe", alternate = ["ads"])
    val ads: List<Ad>,

    @SerializedName("apps")
    val apps: List<App>
)