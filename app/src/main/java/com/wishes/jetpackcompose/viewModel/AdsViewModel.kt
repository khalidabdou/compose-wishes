package com.wishes.jetpackcompose.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wishes_jetpackcompose.GridItemCategory
import com.google.android.gms.ads.nativead.NativeAd
import com.wishes.jetpackcompose.data.entities.App
import com.wishes.jetpackcompose.data.entities.Apps
import com.wishes.jetpackcompose.data.entities.Category
import com.wishes.jetpackcompose.data.entities.Image
import com.wishes.jetpackcompose.screens.GridItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AdsViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {


    private val _ads = MutableStateFlow<List<NativeAd>>(emptyList())

    // Publicly exposed as a read-only state flow
    val ads = _ads.asStateFlow()

    // Function to add a single ad to the list
    fun addAd(nativeAd: NativeAd) {
        viewModelScope.launch {
            val updatedList = _ads.value.toMutableList().apply {
                add(nativeAd)
            }
            _ads.emit(updatedList)
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

        images.forEachIndexed { index, imageUrl ->
            mixedItems.add(GridItem.Content(imageUrl))
            // Decide whether to insert an ad or an app promotion after every `adFrequency` images
            if ((index + 1) % adFrequency == 0) {
                if (_ads.value.isNotEmpty() && adIndex < _ads.value.size) {
                    // Insert an ad if available
                    mixedItems.add(GridItem.Ad(_ads.value[adIndex++]))
                } else if (apps.isNotEmpty() && appIndex < apps.size) {
                    // Fallback to inserting an app promotion if ads are not available
                    mixedItems.add(GridItem.App(apps[appIndex++])) // Assuming GridItem.App is your app promotion item
                }
                // Optionally handle the case where neither ads nor apps are available
            }
        }
        return mixedItems
    }


    fun injectAdsIntoCategoryList(
        categories: List<Category>,
        adFrequency: Int
    ): List<GridItemCategory> {
        val mixedItems = mutableListOf<GridItemCategory>()
        var adIndex = 0 // Keep track of which ad to insert next

        categories.forEachIndexed { index, category ->
            mixedItems.add(GridItemCategory.Content(category))
            // Inject an ad after every `adFrequency` images, if there are ads left to insert
            if ((index + 1) % adFrequency == 0 && adIndex < _ads.value.size) {
                mixedItems.add(GridItemCategory.Ad(_ads.value[adIndex++]))
                Log.d("adloader","$index")
            }
        }
        return mixedItems
    }
    // Function to clear all ads (e.g., when they are no longer needed)
    fun clearAds() {
        viewModelScope.launch {
            _ads.value.forEach { it.destroy() } // Important: Destroy each ad to free resources
            _ads.emit(emptyList())
        }
    }



}