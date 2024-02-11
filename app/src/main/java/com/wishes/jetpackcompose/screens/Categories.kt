package com.example.wishes_jetpackcompose

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.android.gms.ads.nativead.NativeAd
import com.wishes.jetpackcompose.data.entities.Category
import com.wishes.jetpackcompose.runtime.NavRoutes
import com.wishes.jetpackcompose.screens.comp.Ads.NativeSmallAdComposable
import com.wishes.jetpackcompose.screens.comp.LoadingShimmerEffect
import com.wishes.jetpackcompose.utlis.Const
import com.wishes.jetpackcompose.utlis.DEFAULT_RECIPE_IMAGE
import com.wishes.jetpackcompose.utlis.Resource
import com.wishes.jetpackcompose.utlis.loadPicture
import com.wishes.jetpackcompose.viewModel.AdsViewModel
import com.wishes.jetpackcompose.viewModel.ImagesViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi


@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun Categories(
    navHostController: NavHostController,
    viewModel: ImagesViewModel,
    adsViewModel: AdsViewModel,
    paddingValues: PaddingValues,
) {
    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current
    var categories = viewModel.categories.collectAsState()

    LaunchedEffect(categories.value.data?.size) {

        if (categories.value.data.isNullOrEmpty()) {
            Log.d("images", "categories")
            viewModel.getCategories()
        }

    }


    when (categories.value) {
        is Resource.Success -> {
            //val mixedItems =
               // categories.value.data?.let { adsViewModel.injectAdsIntoCategoryList(it, 2) }
            //CategoryListWithAds(mixedItems!!,paddingValues,navHostController)
        }

        is Resource.Loading -> {
            LazyColumn() {
                item {
                    repeat(10) {
                        LoadingShimmerEffect()
                    }
                }
            }

        }

            is Resource.Error -> {
                Toast.makeText(context, "try later", Toast.LENGTH_SHORT).show()
            }

            else -> {

            }
        }

}


@Composable
fun ItemCategory(text: String, painter: ImageBitmap, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .clickable {
                onClick()
            },

        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            val context = LocalContext.current
            androidx.compose.foundation.Image(
                bitmap = painter, contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = "$text",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}


@Composable
fun CategoryListWithAds(
    mixedItems: List<GridItemCategory>,
    paddingValues: PaddingValues,
    navHostController: NavHostController,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        items(mixedItems.size) { index ->
            when (val item = mixedItems[index]) {
                is GridItemCategory.Content -> {
                    val category = item.category
                    val fixedImageUrl = category.image_url.replace("\\", "/")
                    val image = loadPicture(
                        url = Const.BASE_URL + "/" + fixedImageUrl,
                        defaultImage = DEFAULT_RECIPE_IMAGE
                    ).value
                    image?.let { img ->
                        ItemCategory(category.name, img.asImageBitmap()) {
                            navHostController.navigate(NavRoutes.ByCat.route + "/" + category.id)
                        }
                    }
                }

                is GridItemCategory.Ad -> {
                    NativeSmallAdComposable(item.nativeAd) {}
                }
            }
        }
    }
}


sealed class GridItemCategory {
    data class Content(val category: Category) : GridItemCategory()
    data class Ad(val nativeAd: NativeAd) : GridItemCategory() // Updated to hold a NativeAd
}

