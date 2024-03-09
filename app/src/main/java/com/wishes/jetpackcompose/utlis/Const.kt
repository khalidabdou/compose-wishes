package com.wishes.jetpackcompose.utlis

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.wishes.jetpackcompose.BuildConfig
import com.wishes.jetpackcompose.admob.applovin

class Const {

    companion object {

        const val DATABASE_NAME = "MY_DB"
        const val TABLE_IMAGE = "tbl_images"
        const val TABLE_CATEGORY = "tbl_category"
        const val TABLE_WALLPAPER = "tbl_wallpaper"

        //const val BASE_URL = "http://192.168.8.102:2024"
        //const val BASE_URL = "http://192.168.100.232:2024"
        const val BASE_URL = "http://192.168.192.10:2024"

        //192.168.100.205
        //192.168.43.214


        val directoryUpload: String = BuildConfig.api + "categories/"
        val directoryUploadCat: String = BuildConfig.api + "category/"

        //endpoints
        //images
        fun GET_LATEST(appPackage: String, offset: Int = 0) =
            BASE_URL + "/images/latest/$appPackage/?offset=$offset"

        fun GET_IMAGES_BY_CATEGORY(categoryId: Int) = BASE_URL + "/images/category/$categoryId"
        fun GET_CATEGORIES(packageName: String, language: String) =
            "$BASE_URL/category/categories/$packageName/$language"

        fun GET_APP_DETAILS (packageName: String) = BASE_URL + "/app/$packageName/details"


        fun GET_LANGUAGES(packageName: String) = BASE_URL+"/languages/$packageName"

        var LANGUAGE_ID: Int = 4
        var RANDOM = (0..10000).random()

        val applovinClass = applovin()

        fun hasConnection(application: Application): Boolean {
            val connectivityManager = application.getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilites =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilites.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilites.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilites.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
    }
}