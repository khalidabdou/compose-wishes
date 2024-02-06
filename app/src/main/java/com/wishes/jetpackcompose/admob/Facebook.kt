package com.wishes.jetpackcompose.admob

import android.app.Activity
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.InterstitialAd
import com.facebook.ads.InterstitialAdListener
import com.wishes.jetpackcompose.data.entities.AdProvider.Companion.InterFAN
import com.wishes.jetpackcompose.utlis.Const.Companion.applovinClass


class Facebook {


    companion object {

        lateinit var interstitialAd: InterstitialAd

        fun showInterstitial(activity: Activity) {
            if (!this::interstitialAd.isInitialized) {
                loadInterstitialFAN(activity)
                return
            }
            if (!interstitialAd.isAdLoaded) {
                loadInterstitialFAN(activity)
                return
            }
            if (countShow % InterFAN.adCount!! != 0) {
                return
            }
            interstitialAd.show()

        }

        fun loadInterstitialFAN(activity: Activity) {
            interstitialAd = InterstitialAd(
                activity,
                InterFAN.pubId
            )
            val interstitialAdListener: InterstitialAdListener = object : InterstitialAdListener {
                override fun onError(p0: Ad?, p1: AdError?) {

                    applovinClass.createInterstitialAd(activity)
                    InterFAN.showAd = false
                }

                override fun onAdLoaded(inter: Ad?) {

                }

                override fun onAdClicked(p0: Ad?) {

                }

                override fun onLoggingImpression(p0: Ad?) {

                }

                override fun onInterstitialDisplayed(p0: Ad?) {

                }

                override fun onInterstitialDismissed(p0: Ad?) {

                }
            }
            interstitialAd.loadAd(
                interstitialAd.buildLoadAdConfig()
                    .withAdListener(interstitialAdListener)
                    .build()
            )

        }

    }


}