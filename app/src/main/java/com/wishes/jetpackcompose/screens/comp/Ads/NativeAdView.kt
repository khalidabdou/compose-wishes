package com.wishes.jetpackcompose.screens.comp.Ads

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.wishes.jetpackcompose.R
import com.wishes.jetpackcompose.data.entities.Ad
import com.wishes.jetpackcompose.data.entities.App
import com.wishes.jetpackcompose.utlis.AppUtil
import kotlinx.coroutines.delay


fun loadNativeAd(context: Context, adUnitId: String, onAdLoaded: (NativeAd) -> Unit) {
    val adLoader = AdLoader.Builder(context, adUnitId)
        .forNativeAd { nativeAd ->
            // If the ad is loaded successfully, pass the ad to the caller
            onAdLoaded(nativeAd)
        }
        .withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                // Handle ad load failure
                Log.d("AdLoader", "Ad failed to load: ${adError.message}")
            }

            override fun onAdLoaded() {
                Log.d("AdLoader", "Ad loaded")

                super.onAdLoaded()
            }
        })
        .withNativeAdOptions(NativeAdOptions.Builder()
            .setRequestCustomMuteThisAd(true)
            .build())
        .build()


    adLoader.loadAds(AdRequest.Builder().build(),5)
}

@Composable
fun NativeAdComposable(nativeAd: NativeAd,onCTAClicked: () -> Unit) {
    val context = LocalContext.current
    val cta = nativeAd.callToAction
    Box(modifier = Modifier.fillMaxWidth()) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.onPrimary)

        ) {
            nativeAd.adChoicesInfo?.let { adChoicesInfo ->
                Row(
                    modifier = Modifier
                        .padding(8.dp)

                        .clickable {
                            // Handle click on AdChoices info, possibly opening a browser to more info
                        }
                ) {
                    adChoicesInfo.images.forEach { image ->
                        AsyncImage(
                            model = image.drawable,
                            contentDescription = "AdChoices Icon",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        text = adChoicesInfo.text.toString(),
                        color = Color.Magenta,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            nativeAd.images?.let { images ->
                AutoSlidingImageCarousel(
                    images = images,
                    modifier = Modifier.fillMaxWidth() // Specify the desired height
                )
            }
            Row {
                nativeAd.icon?.let { icon ->
                    AsyncImage(
                        model = icon.drawable,
                        contentDescription = "Ad Icon",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .padding(4.dp)
                    )
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    // Ad headline
                    Text(
                        text = nativeAd.headline ?: "",
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(8.dp),
                        style = TextStyle(color = Color.White, fontWeight = FontWeight.Bold)
                    )
                    Row {
                        Text(
                            text = "Ad",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(3.dp)
                        )
                        StarRating(nativeAd.starRating)
                    }
                }
            }

            // Ad body
            nativeAd.body?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 8.dp),
                    style = TextStyle(color = Color.Gray)
                )
            }

            // Call to action button
            Button(
                onClick = {
                    onCTAClicked()
                    try {
                        nativeAd.performClick(Bundle.EMPTY)
                        Log.d("AdLoader", "Call to Action clicked.")
                    } catch (e: Exception) {
                        Log.e("AdLoader", "Error performing click: ${e.message}")
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)
            ) {
                Text(text = nativeAd.callToAction ?: "Learn More")
            }
        }

    }

}
@Composable
fun NativeSmallAdComposable(nativeAd: NativeAd,onCTAClicked: () -> Unit) {
    val context = LocalContext.current
    val cta = nativeAd.callToAction
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.onPrimary)

        ) {
            nativeAd.adChoicesInfo?.let { adChoicesInfo ->
                Row(
                    modifier = Modifier
                        .padding(8.dp)

                        .clickable {
                            // Handle click on AdChoices info, possibly opening a browser to more info
                        }
                ) {
                    adChoicesInfo.images.forEach { image ->
                        AsyncImage(
                            model = image.drawable,
                            contentDescription = "AdChoices Icon",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        text = adChoicesInfo.text.toString(),
                        color = Color.Magenta,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
            Row {
                nativeAd.icon?.let { icon ->
                    AsyncImage(
                        model = icon.drawable,
                        contentDescription = "Ad Icon",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .padding(4.dp)
                    )
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    // Ad headline
                    Text(
                        text = nativeAd.headline ?: "",
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(8.dp),
                        style = TextStyle(color = Color.White, fontWeight = FontWeight.Bold)
                    )
                    Row {
                        Text(
                            text = "Ad",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(3.dp)
                        )
                        StarRating(nativeAd.starRating)
                    }
                }
            }

            // Ad body
            nativeAd.body?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 8.dp),
                    style = TextStyle(color = Color.Gray)
                )
            }

            // Call to action button
            Button(
                onClick = {
                    onCTAClicked()
                    try {
                        nativeAd.performClick(Bundle.EMPTY)
                        Log.d("AdLoader", "Call to Action clicked.")
                    } catch (e: Exception) {
                        Log.e("AdLoader", "Error performing click: ${e.message}")
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)
            ) {
                Text(text = nativeAd.callToAction ?: "Learn More")
            }
        }

    }

}

@Composable
fun MyAppNativeSmallAdComposable(app:App) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.onPrimary)


        ) {

            Row {
                app.iconUrl?.let { icon ->

                    AsyncImage(
                        model = icon,
                        contentDescription = "Ad Icon",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(60.dp)
                            .padding(2.dp)
                            .clip(RoundedCornerShape(12.dp))

                    )
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    // Ad headline
                    Text(
                        text = app.name ?: "",
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(8.dp),
                        style = TextStyle(color = Color.White, fontWeight = FontWeight.Bold)
                    )
                    Row {
                        Text(
                            text = "Ad",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(3.dp)
                        )
                        StarRating(4.5)
                    }
                }
            }

            // Ad body
            app.description?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(horizontal = 8.dp),
                    style = TextStyle(color = Color.Gray)
                )
            }

            // Call to action button
            Button(
                onClick = {
                    try {
                        AppUtil.openUrl(context,app.appUrl!!)
                    } catch (e: Exception) {
                        Log.e("AdLoader", "Error performing click: ${e.message}")
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)
            ) {
                Text(text = app.callToAction ?: "Learn More")
            }
        }

    }

}
@Composable
fun AutoSlidingImageCarousel(
    images: List<NativeAd.Image>,
    modifier: Modifier = Modifier,
    intervalMillis: Long = 3000 // Interval time in milliseconds to change the image
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = remember { mutableStateOf(0) }

    // Automatically cycle through the images
    LaunchedEffect(key1 = images) {
        while (true) {
            delay(intervalMillis)
            pagerState.value = (pagerState.value + 1) % images.size
        }
    }

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        state = rememberLazyListState(initialFirstVisibleItemIndex = pagerState.value)
    ) {
        items(images.size) { index ->
            ImageCarouselItem(image = images[index])
        }
    }
}

@Composable
fun ImageCarouselItem(image: NativeAd.Image) {
    image.drawable?.let { drawable ->
        AsyncImage(
            model = drawable,
            contentDescription = "Ad Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
    }
}

@Composable
fun StarRating(rating: Double?, maxRating: Int = 5) {
    val filledStars = rating?.toInt() ?: 0
    val halfStars = if ((rating ?: 0.0) % 1 >= 0.5) 1 else 0
    val emptyStars = maxRating - filledStars - halfStars

    Row {
        repeat(filledStars) {
            Icon(
                Icons.Filled.Star,
                contentDescription = "Filled Star",
                modifier = Modifier.size(24.dp),
                tint = Color.Yellow
            )
        }

        repeat(halfStars) {
            Icon(
                Icons.AutoMirrored.Filled.StarHalf,
                contentDescription = "Half Star",
                modifier = Modifier.size(24.dp),
                tint = Color.Yellow
            )
        }

        repeat(emptyStars) {
            Icon(
                Icons.Filled.StarOutline,
                contentDescription = "Empty Star",
                modifier = Modifier.size(24.dp),
                tint = Color.Yellow
            )
        }
    }
}



