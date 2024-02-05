package com.wishes.jetpackcompose.screens

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
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
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.wishes.jetpackcompose.R
import com.wishes.jetpackcompose.admob.showInterstitialAfterClick
import com.wishes.jetpackcompose.data.entities.App
import com.wishes.jetpackcompose.data.entities.Image
import com.wishes.jetpackcompose.utlis.AppUtil.getUriImage
import com.wishes.jetpackcompose.utlis.AppUtil.imagesBitmap
import com.wishes.jetpackcompose.utlis.AppUtil.shareImageUri
import com.wishes.jetpackcompose.utlis.Const.Companion.BASE_URL
import com.wishes.jetpackcompose.utlis.DEFAULT_RECIPE_IMAGE
import com.wishes.jetpackcompose.utlis.Resource
import com.wishes.jetpackcompose.utlis.loadPicture
import com.wishes.jetpackcompose.viewModel.ImagesViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.random.Random


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalPagerApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun ViewPager(
    viewModel: ImagesViewModel,
    navController: NavController,
    page: Int?,
    route: String?,
    CatId: Int?
) {
    val context = LocalContext.current


    val images = viewModel.currentListImage.collectAsState(initial = Resource.Loading())


    when (images.value) {
        is Resource.Idle -> {
            Toast.makeText(context, "idle", Toast.LENGTH_SHORT).show()
        }

        is Resource.Loading -> {
            Toast.makeText(context, "loading", Toast.LENGTH_SHORT).show()
        }

        is Resource.Success -> {
            ViewPagerImages(
                images = images.value.data!!.images,
                page = page,
                apps = emptyList(),
                context = context,
                viewModel = viewModel
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
    images: List<Image>,
    page: Int?,
    apps: List<App>,
    context: Context,
    viewModel: ImagesViewModel,
    addOrRemoveFromFav: (Image) -> Unit
) {
    Column() {
        var currentPage = page ?: 0
        val pagerState = rememberPagerState(currentPage)
        val scope = rememberCoroutineScope()
        val isFav = viewModel.isImageFavorited.collectAsState()

        LaunchedEffect(key1 = images[pagerState.currentPage] , block = {
            viewModel.checkIfImageFavorited(images[pagerState.currentPage])
            Log.d("fav", isFav.value.toString())
        })


        HorizontalPager(
            modifier = Modifier.weight(9f),
            count = images.size,
            state = pagerState
        ) { page ->

            if (page % 21 == 0 && !apps.isNullOrEmpty()) {
                val app = apps.get(Random.nextInt(0, apps.size))
                Ad_app(app, context)

            } else {
                currentPage = page

                val url = BASE_URL + images[page].url
                val image = loadPicture(
                    url = url,
                    defaultImage = DEFAULT_RECIPE_IMAGE
                ).value

                Box(modifier = Modifier.fillMaxSize()) {
                    image?.let { img ->
                        Image(
                            bitmap = img.asImageBitmap(), contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                        imagesBitmap[images[page].id] = img
                        //imageBitmap=img
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
                addOrRemoveFromFav(images[pagerState.currentPage])
                showInterstitialAfterClick(context)
            }


            /*Action("Download", Icons.Outlined.KeyboardArrowDown) {
                Toast.makeText(context,"Download success",Toast.LENGTH_LONG).show()
            }*/
            Action(stringResource(R.string.share_icon), Icons.Outlined.Share) {
                imagesBitmap[images[pagerState.currentPage].id]?.let {
                    val uri: Uri? = getUriImage(it, context)
                    shareImageUri(uri!!, context)
                }
                showInterstitialAfterClick(context)
            }
        }

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