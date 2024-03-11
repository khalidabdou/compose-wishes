package com.wishes.jetpackcompose.screens

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.wishes.jetpackcompose.R
import com.wishes.jetpackcompose.runtime.NavRoutes
import com.wishes.jetpackcompose.screens.comp.Ads.loadNativeAd
import com.wishes.jetpackcompose.utlis.Resource
import com.wishes.jetpackcompose.viewModel.AdsViewModel
import com.wishes.jetpackcompose.viewModel.ImagesViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun Splash(navController: NavHostController, viewModel: ImagesViewModel,adsViewModel: AdsViewModel) {
    val context = LocalContext.current
    var startAnimation by remember { mutableStateOf(false) }
    val appDetails = viewModel.appDetails.collectAsState()
    val language = viewModel.appLanguage.collectAsState()
    //var nativeAd by remember { mutableStateOf<NativeAd?>(null) }
    LaunchedEffect(Unit) {
        Log.d("app", "Success")
        viewModel.getAppDetails()
    }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            loadNativeAd(context, "ca-app-pub-3940256099942544/2247696110",) { ad ->
                viewModel.addAd(ad)
            }
        }
    }
    when (appDetails.value) {
        is Resource.Loading -> {
            Log.d("app", "loading")
        }
        is Resource.Success -> {
            if (language.value != null)
            navController.navigate(NavRoutes.Home.route)
            else   navController.navigate(NavRoutes.Languages.route)
        }

        else -> {
            LaunchedEffect(key1 = true) {
                viewModel.setMessage(context)
                startAnimation = true
                delay(2000)
                navController.popBackStack()
                navController.navigate(NavRoutes.Languages.route)
                //Toast.makeText(context, "${viewModel.adsList.value}", Toast.LENGTH_LONG).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        AnimateImageScaleAndRotate()
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Welcome Back!",
                style = MaterialTheme.typography.headlineMedium, // Adjust the text style as needed
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Description Text
            Text(
                text = "Good Morning",
                style = MaterialTheme.typography.bodyLarge, // Adjust the text style as needed
                modifier = Modifier.padding(
                    horizontal = 32.dp,
                    vertical = 8.dp
                ) // Add some padding around the description for better readability
            )

            // Circular Progress Bar
            CircularProgressIndicator(
                modifier = Modifier.padding(16.dp), // Add some space around the progress bar
                color = MaterialTheme.colorScheme.primary // Use the primary color from the theme
            )
        }

    }
//    nativeAd?.let { ad ->
//        NativeAdComposable(ad){
//           nativeAd!!.callToAction
//
//
//        }
//    }
}




@Composable
fun AnimateImageScaleAndRotate() {
    var scaledUp by remember { mutableStateOf(false) }
    val scale = animateFloatAsState(
        targetValue = if (scaledUp) 3f else 2f,
        animationSpec = tween(durationMillis = 5000), // 2 seconds duration
        label = ""
    )
    val rotation = animateFloatAsState(
        targetValue = if (scaledUp) 10f else 0f,
        animationSpec = tween(durationMillis = 5000), // 2 seconds duration
        label = ""
    )

    LaunchedEffect(key1 = Unit) {
        scaledUp = true
    }

    Box() {
        Image(
            painter = painterResource(id = R.drawable.morning),
            contentDescription = "Animated Image",
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale.value)
                .rotate(rotation.value)
            //.clip(TriangleShape())
            ,
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .matchParentSize() // Make the Box the same size as the parent Box
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent, // Top color
                            MaterialTheme.colorScheme.background,

                            )
                    )
                )
        )
    }


}


class TriangleShape : Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        density: androidx.compose.ui.unit.Density
    ): androidx.compose.ui.graphics.Outline {
        val path = Path().apply {
            val topWidthOffset =
                size.width * 0.2f // Adjust for the desired top width of the trapezoid, making the top narrower
            val bottomWidthOffset = 0f // Keeps the bottom edge at its maximum width
            moveTo(topWidthOffset, 0f) // Top left starting point, adjusted for trapezoid shape
            lineTo(size.width - topWidthOffset, 0f) // Top right
            lineTo(size.width - bottomWidthOffset, size.height) // Bottom right
            lineTo(bottomWidthOffset, size.height) // Bottom left
            close() // Closes the path by connecting the last point with the first

        }
        return androidx.compose.ui.graphics.Outline.Generic(path)
    }
}

