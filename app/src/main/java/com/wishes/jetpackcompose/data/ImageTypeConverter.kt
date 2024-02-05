package com.wishes.jetpackcompose.data

import androidx.room.TypeConverter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wishes.jetpackcompose.data.entities.Image


class imageTypeConverter {

    var gson = Gson()

    @TypeConverter
    fun imageToString(image: Image): String {
        return gson.toJson(image)
    }

    @TypeConverter
    fun StringToImage(data: String): Image {
        var listType = object : TypeToken<Image>() {}.type
        return gson.fromJson(data, listType)
    }


}