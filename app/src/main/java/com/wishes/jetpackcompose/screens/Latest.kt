package com.wishes.jetpackcompose.screens

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import com.wishes.jetpackcompose.data.entities.Latest
import com.wishes.jetpackcompose.data.entities.Page
import com.wishes.jetpackcompose.screens.comp.EmptyState
import com.wishes.jetpackcompose.screens.comp.ImageItem
import com.wishes.jetpackcompose.screens.comp.LoadingShimmerEffectImage
import com.wishes.jetpackcompose.utlis.Const
import com.wishes.jetpackcompose.utlis.DEFAULT_RECIPE_IMAGE
import com.wishes.jetpackcompose.utlis.Resource
import com.wishes.jetpackcompose.utlis.loadPicturetemmp

@Composable
fun Latest(
    scrollState: LazyGridState,
    paddingValues: PaddingValues,
    latest: Resource<Latest>,
    loadMore: () -> Unit,
    onClick: (Page) -> Unit
) {



    val itemCount = latest.data?.images?.size ?: 0
    LaunchedEffect(
        scrollState.firstVisibleItemIndex,
    ) {
        if (itemCount > 0 && scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == itemCount - 1) {
            Log.d("scrolling", "${scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index}")
            //load more
            loadMore()
        }
    }

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
                        LazyVerticalGrid(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues),
                            state = scrollState,
                            columns = GridCells.Fixed(2),
                            content = {
                                latest.data?.let { imagesdata ->
                                    items(imageSize.size) {
                                        val image: MutableState<Bitmap?>? = loadPicturetemmp(
                                            url = Const.BASE_URL + latest.data?.images!![it].url,
                                            defaultImage = DEFAULT_RECIPE_IMAGE
                                        )
                                        ImageItem(
                                            image?.value?.asImageBitmap(),
                                        ) {
                                            val page = Page(
                                                page = it,
                                                imagesList = ImagesFrom.Latest.route,
                                                null
                                            )
                                            onClick(page)

                                        }
                                    }
                                }

                            })
                    }
                }
            }
        }

        is Resource.Loading -> {
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                state = scrollState,
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

