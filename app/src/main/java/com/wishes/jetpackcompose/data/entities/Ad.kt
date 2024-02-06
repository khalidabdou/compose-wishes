package com.wishes.jetpackcompose.data.entities

import com.google.gson.annotations.SerializedName

data class Ad(
    var id: Int,
    var adTypeId: String,
    var pub_id: String,
    var showAd: Boolean,
    var adCount: Int?,
    )

data class Setting(
    @SerializedName("package")
    val package_name: String,
    val dynamic_link: String,
    val email: String
)


data class AppAdContainer(
    val ads: List<Ad>,
    val AppAdvertisements: List<App>
)

object AdFactory {

    val bannerAd = Ad(0, "banner", "banner", false, null)
    val interstitialAd = Ad(0, "inter", "inter", true, 3)
    val openAd = Ad(0, "open", "open", false, null)
    val rewardedAd = Ad(0, "rewarded", "rewarded", false, null)

    val bannerFanAd = Ad(0, "banner_fan", "banner_fan", false, null)
    val interstitialFanAd = Ad(0, "inter_fan", "inter_fan", false, 10)
    val bannerApplovinAd = Ad(0, "banner_applovin", "banner_applovin", false, null)
    val interstitialApplovinAd = Ad(0, "inter_Applovin", "inter_Applovin", false, 10)
}