package com.wishes.jetpackcompose.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.wishes.jetpackcompose.utlis.Const.Companion.TABLE_IMAGE

import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = TABLE_IMAGE)
data class Image(
    @PrimaryKey
    var id: Int,
    val url: String?,
    val categoryId: Int,
    val viewCount: Int,
    val downloadCount: Int,
    val shareCount: Int,
    var isfav: Int?,
    val likeCount: Int?,
    val language_app: Int?,
    @SerializedName("language")
    val languageLable: String?

) : Parcelable

data class Latest(
    val images: List<Image>
)