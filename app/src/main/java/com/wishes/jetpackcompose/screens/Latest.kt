package com.wishes.jetpackcompose.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import com.google.android.gms.ads.nativead.NativeAd
import com.wishes.jetpackcompose.data.entities.App
import com.wishes.jetpackcompose.data.entities.Image
import com.wishes.jetpackcompose.data.entities.Latest
import com.wishes.jetpackcompose.screens.comp.Ads.MyAppNativeSmallAdComposable
import com.wishes.jetpackcompose.screens.comp.Ads.NativeAdComposable
import com.wishes.jetpackcompose.screens.comp.EmptyState
import com.wishes.jetpackcompose.screens.comp.ImageItem
import com.wishes.jetpackcompose.screens.comp.LoadingShimmerEffectImage
import com.wishes.jetpackcompose.utlis.Const
import com.wishes.jetpackcompose.utlis.DEFAULT_RECIPE_IMAGE
import com.wishes.jetpackcompose.utlis.Resource
import com.wishes.jetpackcompose.utlis.loadPicturetemmp
import com.wishes.jetpackcompose.viewModel.AdsViewModel

@Composable
fun Latest(
    scrollState: LazyListState,
    paddingValues: PaddingValues,
    latest: Resource<Latest>,
    apps: List<App> = emptyList(),
    showLoadMore: Boolean? = false,
    loadMore: () -> Unit,
    adsViewModel: AdsViewModel,
    onClick: (Int) -> Unit
) {

    when (latest) {
        is Resource.Success -> {
            latest.data?.let { imagesdata ->
                imagesdata.images.let { imageSize ->
                    if (imageSize.isNullOrEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            EmptyState()
                        }
                    } else {
                        val imagesUrls = latest!!.data!!.images!!
                        val mixedItems =
                            adsViewModel.injectAdsIntoImagesList(imagesUrls, apps, 4)

                        ImageGridWithAds(mixedItems, paddingValues, loadMore = {
                            loadMore()
                        }) {
                            Log.d("click", "$it")
                            onClick(it)
                        }
                    }
                }
            }
        }

        is Resource.Loading -> {
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                columns = GridCells.Fixed(2),
                content = {

                    repeat(20) {
                        item {
                            LoadingShimmerEffectImage()
                        }
                    }
                })

        }

        else -> {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                EmptyState()
            }
        }


    }

}

@SuppressLint("UnrememberedMutableState")
@Composable
fun ImageGridWithAds(
    listItems: List<GridItem>,
    paddingValues: PaddingValues,
    loadMore: () -> Unit,
    onClick: (Int) -> Unit
) {
    val scrollStates = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    // Directly derive the last visible index for easier reading
    val lastIndexVisible = remember {
        derivedStateOf {
            scrollStates.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
        }
    }

    // Correctly initialize hasReachedBottoms to detect if the bottom has been reached
    val hasReachedBottoms = remember {
        derivedStateOf {
            val totalRows = listItems.chunked(2).size
            // lastIndexVisible is a derived state, so we use .value to access its current value
            lastIndexVisible.value >= totalRows - 1
        }
    }
    LaunchedEffect(scrollStates.layoutInfo.visibleItemsInfo.lastOrNull()?.index) {
        snapshotFlow { scrollStates.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastIndex ->
                // Assuming each chunk (including ads as separate chunks) contributes to one row.
                val totalRows = listItems.chunked(2).size

                // Convert the last visible item's index to what it represents in terms of rows.
                // This is necessary because the lastIndex from visibleItemsInfo corresponds to the rows, not the original listItems.
                val hasReachedBottom = lastIndex != null && lastIndex >= totalRows - 1

                if (hasReachedBottom && hasReachedBottoms.value) {
                    Log.d(
                        "scrolling",
                        "Reached the bottom: Last visible index: $lastIndex, Total rows: $totalRows"
                    )
                    // Trigger loading more content or any desired action here.
                    loadMore()
                }
            }
    }




    LazyColumn(
        modifier = Modifier.padding(paddingValues),
        state = scrollStates
    ) {
        // Use itemsIndexed if you want to keep the index for each original list item before chunking
        val chunkedItems = listItems.chunked(2)
        chunkedItems.forEachIndexed { chunkIndex, pair ->
            item {
                Row {
                    pair.forEachIndexed { indexWithinChunk, gridItem ->
                        // Calculate the actual index in the original list
                        val actualIndex = chunkIndex * 2 + indexWithinChunk
                        when (gridItem) {
                            is GridItem.Content -> {
                                val image: MutableState<Bitmap?>? = loadPicturetemmp(
                                    url = Const.BASE_URL + gridItem.image.url,
                                    defaultImage = DEFAULT_RECIPE_IMAGE
                                )
                                Box(modifier = Modifier.weight(1f)) {
                                    ImageItem(painter = image?.value?.asImageBitmap(), onClick = {
                                        // Pass the actual index here
                                        onClick(actualIndex)
                                    })
                                }
                            }

                            is GridItem.App -> {
                                MyAppNativeSmallAdComposable(gridItem.app)
                            }

                            is GridItem.Ad -> {
                                // Handle ad display
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    NativeAdComposable(gridItem.nativeAd) {}
                                }
                            }
                        }
                        // Spacer logic remains unchanged
                        if (pair.indexOf(gridItem) == 0 && pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }

}


sealed class GridItem {
    data class Content(val image: Image) : GridItem()
    data class App(val app: com.wishes.jetpackcompose.data.entities.App) : GridItem()
    data class Ad(val nativeAd: NativeAd) : GridItem()
}


