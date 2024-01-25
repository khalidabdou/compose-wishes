package com.wishes.jetpackcompose.utlis

import com.google.gson.Gson

object JSONConverter {

    val gson = Gson()

    // Convert a generic object to JSON string
    inline fun <reified T> objectToJson(data: T): String {
        return gson.toJson(data)
    }

    // Convert JSON string to a generic object
    inline fun <reified T> jsonToObject(json: String): T? {
        return gson.fromJson(json, T::class.java)
    }
}
