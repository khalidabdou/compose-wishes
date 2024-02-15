package com.wishes.jetpackcompose.utlis

import com.google.android.gms.ads.nativead.NativeAd
import com.wishes.jetpackcompose.screens.GridItem

fun List<GridItem>.integrateAds(newAds: List<NativeAd>): List<GridItem> {
    val integratedList = mutableListOf<GridItem>()
    val adIterator = newAds.iterator()

    this.forEachIndexed { index, gridItem ->
        integratedList.add(gridItem)

        // After every N items, add an ad if available
        if ((index + 1) % 4 == 0 && adIterator.hasNext()) {
            val ad = adIterator.next()
            integratedList.add(GridItem.Ad(ad))
        }
    }

    return integratedList
}