package com.wishes.jetpackcompose.screens

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.composeadmobkit.NativeAdComposable
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.wishes.jetpackcompose.R
import com.wishes.jetpackcompose.admob.showInterstitialAfterClick
import com.wishes.jetpackcompose.data.entities.Image
import com.wishes.jetpackcompose.screens.comp.Ads.MyAppNativeSmallAdComposable
import com.wishes.jetpackcompose.screens.comp.EmptyState
import com.wishes.jetpackcompose.utlis.AppUtil.getUriImage
import com.wishes.jetpackcompose.utlis.AppUtil.imagesBitmap
import com.wishes.jetpackcompose.utlis.AppUtil.shareImageUri
import com.wishes.jetpackcompose.utlis.DEFAULT_RECIPE_IMAGE
import com.wishes.jetpackcompose.utlis.Resource
import com.wishes.jetpackcompose.utlis.loadPicture
import com.wishes.jetpackcompose.viewModel.AdsViewModel
import com.wishes.jetpackcompose.viewModel.ImagesViewModel


@SuppressLint("UnrememberedMutableState")
@Composable
fun ViewPager(
    viewModel: ImagesViewModel,
    adsViewModel: AdsViewModel,
    page: Int?,
) {
    val context = LocalContext.current
    val images = viewModel.currentListImage.collectAsState(initial = Resource.Loading())
    when (images.value) {
        is Resource.Idle -> {}

        is Resource.Loading -> {}

        is Resource.Success -> {
            ViewPagerImages(
                page = page,
                context = context,
                viewModel = viewModel,
            ) {
                viewModel.addToFav(it)
            }
        }

        is Resource.Error -> {
            Toast.makeText(context, "error", Toast.LENGTH_SHORT).show()
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ViewPagerImages(
    page: Int?,
    context: Context,
    viewModel: ImagesViewModel,
    addOrRemoveFromFav: (Image) -> Unit
) {

    Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
        var currentPage = page ?: 0
        val pagerState = rememberPagerState(currentPage)
        val scope = rememberCoroutineScope()
        val isFav = viewModel.isImageFavorited.collectAsState()

        var images = viewModel.currentListImage.collectAsState().value

        // Determine the content to display based on the current view pager type
        when (viewModel.currentViewPagerType) {
            ImagesViewModel.VIEW_PAGER.LATEST -> {
                images = viewModel.imagesWithAd.collectAsState().value
            }
            ImagesViewModel.VIEW_PAGER.FAVORITES -> {
                images = viewModel.favorites.collectAsState().value
            }
            ImagesViewModel.VIEW_PAGER.BY_CATEGORY -> {
                images = viewModel.imagesByCategory.collectAsState().value
            }
        }
        LaunchedEffect(key1 = images.data?.get(pagerState.currentPage)) {
            val currentItem = images.data?.get(pagerState.currentPage)
            // Assuming GridItem.Content represents your images
            if (currentItem is GridItem.Content) {
                // Now you need to extract the image information from GridItem.Content
                // and check if it's favorited
                viewModel.checkIfImageFavorited(currentItem.image) // Assuming you have a URL or similar identifier
            }
        }


        if (!images.data.isNullOrEmpty()) {
            HorizontalPager(
                modifier = Modifier.weight(9f),
                count = images.data!!.size, // Use the size of mixedItems
                state = pagerState,
            ) { page ->
                when (val item = images.data!![page]) {
                    is GridItem.Ad -> {
                        // Correctly cast and pass the nativeAd from the item
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.onPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            NativeAdComposable(nativeAd = item.nativeAd) {

                            }
                        }

                    }

                    is GridItem.App -> {
                        MyAppNativeSmallAdComposable(item.app)

                    }

                    is GridItem.Content -> {
                        currentPage = page
                        // Use item.imageUrl which is already a complete URL from your mixedItems list
                        val image = loadPicture(
                            url = item.image.url!!,
                            defaultImage = DEFAULT_RECIPE_IMAGE
                        ).value
                        Box(modifier = Modifier.fillMaxSize()) {
                            image?.let { img ->
                                Image(
                                    bitmap = img.asImageBitmap(), contentDescription = null,
                                    modifier = Modifier.fillMaxSize()
                                )
                                // Assuming imagesBitmap is a way to cache or keep track of loaded images
                                // images[page].id might not directly correspond here due to ads adjustment
                                // You might need a mapping or a way to retrieve the correct image ID if necessary
                            }
                        }
                    }
                }
            }



            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(6.dp)
            ) {

                Action(
                    stringResource(R.string.fav),
                    if (isFav.value) (Icons.Default.Favorite) else (Icons.Default.FavoriteBorder)
                ) {
                    val currentItem = images.data!![pagerState.currentPage]
                    if (currentItem is GridItem.Content)
                        addOrRemoveFromFav(currentItem.image)
                    showInterstitialAfterClick(context)
                }


                /*Action("Download", Icons.Outlined.KeyboardArrowDown) {
                    Toast.makeText(context,"Download success",Toast.LENGTH_LONG).show()
                }*/
                Action(stringResource(R.string.share_icon), Icons.Outlined.Share) {

                    val currentItem = images.data!![pagerState.currentPage]
                    if (currentItem is GridItem.Content)
                        imagesBitmap[currentItem.image.id]?.let {
                            val uri: Uri? = getUriImage(it, context)
                            shareImageUri(uri!!, context)
                        }
                    showInterstitialAfterClick(context)
                }
            }
        } else EmptyState()

    }
}

@Composable
fun Action(text: String, icon: ImageVector, onClickAction: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable {
            onClickAction()
        }) {
        Icon(icon, contentDescription = "", tint = MaterialTheme.colorScheme.onPrimaryContainer)
        Text(
            text = text, style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}