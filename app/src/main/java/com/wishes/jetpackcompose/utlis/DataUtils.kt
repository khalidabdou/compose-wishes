package com.wishes.jetpackcompose.utlis

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore by preferencesDataStore(
    name = "PreferenceDataStore"
)
object DataUtils {

    val gson = Gson()

    // Generic function to save any model
    suspend inline fun <reified T> saveModelInfo(context: Context, model: T) {
        val json = gson.toJson(model)
        val key = object : TypeToken<T>() {}.type.toString()
        Log.e("TypeToken", key)
        Log.e("saveModelInfo", json)
        // Using Kotlin coroutines to save the data
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = json
        }
    }

    // Generic function to get any model
    // Generic function to get any model
    inline fun <reified T> getModelInfo(context: Context): Flow<T?> {
        val type = object : TypeToken<T>() {}.type
        val key = type.toString()
        return context.dataStore.data.map { preferences ->
            val json = preferences[stringPreferencesKey(key)]
            Log.e("getModelInfo", json.orEmpty())
            json?.let { gson.fromJson(it, type) }
        }
    }

    inline fun <reified T> jsonStringToModel(json: String): T {
        return gson.fromJson(json, object : TypeToken<T>() {}.type)
    }

    inline fun <reified T> modelToJson(model: T): String {
        return gson.toJson(model)
    }
}
