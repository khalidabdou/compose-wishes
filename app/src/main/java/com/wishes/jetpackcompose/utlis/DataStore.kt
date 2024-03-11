package com.wishes.jetpackcompose.utlis

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

import androidx.datastore.preferences.core.*
import com.google.gson.Gson


import kotlinx.coroutines.flow.map

// Assuming the Context.dataStore extension property is defined somewhere else in your codebase, like:




class DataStoreManager(val context: Context) {
    val Context.dataStore by preferencesDataStore(name = "my_data_store")

    internal val gson = Gson()

    suspend inline fun <reified T> save(key: String, value: T) {
        val dataKey = stringPreferencesKey(key)
        val valueToSave: String = when (value) {
            is String, is Int, is Boolean, is Float, is Long -> value.toString()
            else -> Gson().toJson(value) // Serialize complex objects
        }
        context.dataStore.edit { preferences ->
            preferences[dataKey] = valueToSave
        }
    }

    inline fun <reified T> read(key: String, defaultValue: T): Flow<T?> = context.dataStore.data.map { preferences ->
        val dataKey = stringPreferencesKey(key)
        val value = preferences[dataKey] ?: return@map defaultValue

        when (T::class) {
            String::class, Int::class, Boolean::class, Float::class, Long::class -> value
            else -> try {
                Gson().fromJson(value, T::class.java)
            } catch (e: Exception) {
                defaultValue
            }
        } as T?
    }
}




