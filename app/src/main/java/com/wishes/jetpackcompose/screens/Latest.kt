package com.wishes.jetpackcompose.screens

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap

import com.wishes.jetpackcompose.data.entities.Latest
import com.wishes.jetpackcompose.data.entities.Page
import com.wishes.jetpackcompose.runtime.NavRoutes
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
    onClick: (Page) -> Unit
) {
    LazyVerticalGrid(modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
        state = scrollState,
        columns = GridCells.Fixed(2),
        content = {
            when (latest) {
                is Resource.Success -> {
                    latest.data?.let { imagesdata ->
                        imagesdata.latest.let { imageSize ->
                            items(imageSize.size) {
                                val image: MutableState<Bitmap?>? = loadPicturetemmp(
                                    url = Const.BASE_URL + latest.data?.latest!![it].url,
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

                    }
                }

                is Resource.Loading -> {
                    items(15) {
                        LoadingShimmerEffectImage()
                    }
                }

                is Resource.Error -> {
                    //Toast.makeText(context, latest.message, Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        })
}