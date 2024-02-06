package com.wishes.jetpackcompose.utlis

import com.wishes.jetpackcompose.data.entities.Ad
import com.wishes.jetpackcompose.data.entities.AdProvider


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
        }
    }
}

