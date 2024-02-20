package com.wishes.jetpackcompose.utlis

import com.google.android.gms.ads.nativead.NativeAd

fun <T> List<T>.integrateAds(newAds: List<NativeAd>, adConverter: (NativeAd) -> T): List<T> {
    val integratedList = mutableListOf<T>()
    val adIterator = newAds.iterator()

    this.forEachIndexed { index, item ->
        integratedList.add(item)

        // After every N items, add an ad if available
        if ((index + 1) % 4 == 0 && adIterator.hasNext()) {
            val ad = adIterator.next()
            integratedList.add(adConverter(ad)) // Convert NativeAd to T and add it to the list
        }
    }

    return integratedList
}
