package com.wishes.jetpackcompose.utlis

import android.util.Log
import com.example.wishes_jetpackcompose.GridItemCategory
import com.wishes.jetpackcompose.data.entities.Ad
import com.wishes.jetpackcompose.data.entities.AdProvider
import com.wishes.jetpackcompose.data.entities.App
import com.wishes.jetpackcompose.data.entities.Category
import com.wishes.jetpackcompose.data.entities.Image
import com.wishes.jetpackcompose.screens.GridItem


object AdsUtil {
    fun makeAd(ad: Ad) {
        when (ad.adPlatform) {
            "AdMob" -> updateAdConfiguration(ad)
            "Meta" -> updateAdConfiguration(ad)
            "AppLovin" -> updateAdConfiguration(ad)
            "IronSource"-> updateAdConfiguration(ad)
        }
    }

    private fun updateAdConfiguration(ad: Ad) {
        when (ad.adType) {
            "Banner" -> AdProvider.Banner = ad
            "Interstitial" -> AdProvider.Inter = ad
            "OpenAppAd" -> AdProvider.OpenAd = ad
            "RewardVideo" -> AdProvider.Rewarded = ad
            "Native" -> AdProvider.NativeAdmob = ad
        }
    }
}

fun injectAdsIntoImagesList(
    images: List<Image>,
    apps: List<App>, // Assuming apps is a list of app promotions
    adFrequency: Int
): List<GridItem> {
    val mixedItems = mutableListOf<GridItem>()
    var adIndex = 0
    var appIndex = 0 // Keep track of which app to insert next

//    images.forEachIndexed { index, imageUrl ->
//        mixedItems.add(GridItem.Content(imageUrl))
//        // Decide whether to insert an ad or an app promotion after every `adFrequency` images
//        if ((index + 1) % adFrequency == 0) {
//            if (_ads.value.isNotEmpty() && adIndex < _ads.value.size) {
//                // Insert an ad if available
//                mixedItems.add(GridItem.Ad(_ads.value[adIndex++]))
//            } else if (apps.isNotEmpty() && appIndex < apps.size) {
//                // Fallback to inserting an app promotion if ads are not available
//                mixedItems.add(GridItem.App(apps[appIndex++])) // Assuming GridItem.App is your app promotion item
//            }
//            // Optionally handle the case where neither ads nor apps are available
//        }
//    }
    return mixedItems
}


fun injectAdsIntoCategoryList(
    categories: List<Category>,
    adFrequency: Int
): List<GridItemCategory> {
    val mixedItems = mutableListOf<GridItemCategory>()
    var adIndex = 0 // Keep track of which ad to insert next

//    categories.forEachIndexed { index, category ->
//        mixedItems.add(GridItemCategory.Content(category))
//        // Inject an ad after every `adFrequency` images, if there are ads left to insert
//        if ((index + 1) % adFrequency == 0 && adIndex < _ads.value.size) {
//            mixedItems.add(GridItemCategory.Ad(_ads.value[adIndex++]))
//            Log.d("adloader","$index")
//        }
//    }
    return mixedItems
}
// Function to clear all ads (e.g., when they are no longer needed)





