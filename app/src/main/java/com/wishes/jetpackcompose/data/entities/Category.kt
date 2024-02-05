package com.wishes.jetpackcompose.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.wishes.jetpackcompose.utlis.Const.Companion.TABLE_CATEGORY
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Category(
    var id: Int,
    val name: String,
    val image_url: String,
) : Parcelable


