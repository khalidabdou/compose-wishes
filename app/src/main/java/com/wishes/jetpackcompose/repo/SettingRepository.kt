package com.wishes.jetpackcompose.repo


import com.google.gson.Gson
import com.wishes.jetpackcompose.data.LocalDataSource
import com.wishes.jetpackcompose.data.entities.AppLanguage
import com.wishes.jetpackcompose.utlis.DataStoreManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class SettingRepository @Inject constructor(
    private val dataStoreManager: DataStoreManager
) {


   suspend fun saveLanguage(appLanguage: AppLanguage) {
        val languageJson = serializeAppLanguage(appLanguage) // Implement this method to serialize your object
        dataStoreManager.save("appLanguage", languageJson)
    }

    fun readAppLanguage(): Flow<AppLanguage?> {
        // Assuming the key used to save the language was "appLanguage"
        return dataStoreManager.read<String>("appLanguage", "").map { languageJson ->
            if (languageJson?.isNotEmpty() == true) {
                try {
                    deserializeAppLanguage(languageJson)
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }
        }
    }


    fun serializeAppLanguage(appLanguage: AppLanguage): String {
        return Gson().toJson(appLanguage)
    }

    fun deserializeAppLanguage(languageJson: String): AppLanguage {
        return Gson().fromJson(languageJson, AppLanguage::class.java)
    }

}
