package com.wishes.jetpackcompose.data.entities

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

data class Ad(
    val id: Int,
    val adCount: Int? = 10,
    var showAd: Boolean,
    @SerializedName("pub_id") val pubId: String,
    val adType: String,
    val adPlatform: String
)



@Serializable
data class AppDetails(
    val id: Int,
    val name: String,
    @SerializedName("appPackage") val appPackage: String,
    val description: String?,
    val iconUrl: String?,
    val largePhotoUrl: String?,
    val privacyUrl: String?,
    val store :String?,
    @SerializedName("Applanguages")
    val Applanguages: List<AppLanguageWrapper>?,
    val ads: List<Ad>,
    val advertisements: List<App>,
    val advertisedBy: List<App>
)


class AdProvider {
    companion object {
        var Banner: Ad = Ad(
            id = 0,
            pubId = "your_pub_id_for_Banner",
            adType = "banner",
            showAd = false,
            adCount = null,
            adPlatform = "AdMob"
        )

        var Inter: Ad = Ad(
            id = 0,
            pubId = "your_pub_id_for_Inter",
            adType = "inter",
            showAd = true,
            adCount = 3,
            adPlatform = "AdMob"
        )

        var OpenAd: Ad = Ad(
            id = 0,
            pubId = "your_pub_id_for_OpenAd",
            adType = "open",
            showAd = false,
            adCount = null,
            adPlatform = "AdMob"
        )

        var Rewarded: Ad = Ad(
            id = 0,
            pubId = "your_pub_id_for_Reward",
            adType = "rewarded",
            showAd = false,
            adCount = null,
            adPlatform = "AdMob"
        )

        var BannerFAN: Ad = Ad(
            id = 0,
            pubId = "",
            adType = "banner_fan",
            showAd = false,
            adCount = null,
            adPlatform = "FAN"
        )

        var InterFAN: Ad = Ad(
            id = 0,
            pubId = "",
            adType = "inter_fan",
            showAd = false,
            adCount = 10,
            adPlatform = "FAN"
        )

        var BannerApplovin: Ad = Ad(
            id = 0,
            pubId = "",
            adType = "banner_applovin",
            showAd = false,
            adCount = null,
            adPlatform = "Applovin"
        )

        var InterApplovin: Ad = Ad(
            id = 0,
            pubId = "",
            adType = "inter_applovin",
            showAd = false,
            adCount = 10,
            adPlatform = "Applovin"
        )
    }
}

