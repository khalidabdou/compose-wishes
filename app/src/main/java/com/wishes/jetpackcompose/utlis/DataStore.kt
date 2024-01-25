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



class DataStore(private val context: Context) {

    private val TOKEN_KEY = stringPreferencesKey("token")
    private val THEME_KEY = intPreferencesKey("theme")
    private val LANGUAGE_KEY = stringPreferencesKey("language")
    val USER_KEY = stringPreferencesKey("user")
    val TEXT_KEY = stringPreferencesKey("text_key")



    // Function to save token

    val getToken: Flow<String?> = context.dataStore.data.map { preferences ->
        val token = preferences[TOKEN_KEY]
        if (token.isNullOrBlank())
            null
        else
            token
    }



}


