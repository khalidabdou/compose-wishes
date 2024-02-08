package com.wishes.jetpackcompose.screens

import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.wishes.jetpackcompose.R
import com.wishes.jetpackcompose.utlis.Resource
import com.wishes.jetpackcompose.viewModel.ImagesViewModel
import kotlinx.coroutines.delay


@Composable
fun Splash(navController: NavHostController, viewModel: ImagesViewModel) {
    val context = LocalContext.current
    var startAnimation by remember { mutableStateOf(false) }
    val message = viewModel.message.collectAsState()
    val appDetails = viewModel.appDetails.collectAsState()

    LaunchedEffect(Unit) {
        Log.d("app", "Success")
        viewModel.getAppDetails()
    }
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 3000
        ), label = ""
    )



    when (appDetails.value) {
        is Resource.Loading -> {
            Log.d("app", "loading")
        }

        is Resource.Success -> {
            //navController.navigate(NavRoutes.Home.route)
        }

        else -> {
            LaunchedEffect(key1 = true) {
                viewModel.setMessage(context)
                startAnimation = true
                delay(2000)
                navController.popBackStack()
                //navController.navigate(NavRoutes.Home.route)
                //Toast.makeText(context, "${viewModel.adsList.value}", Toast.LENGTH_LONG).show()
            }
        }
    }


    Splash(alpha = alphaAnim.value, message.value)
}

@Composable
fun Splash(alpha: Float, message: String) {

    val images: List<String> = listOf("", "", "")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        AnimateImageScaleAndRotate()
        Text(
            text = stringResource(id = R.string.app_name),
            color = MaterialTheme.colorScheme.onBackground,
            style = TextStyle(fontSize = 19.sp)
        )
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}


@Composable
fun AnimateImageScaleAndRotate() {
    var scaledUp by remember { mutableStateOf(false) }
    val scale = animateFloatAsState(
        targetValue = if (scaledUp) 3f else 1f,
        animationSpec = tween(durationMillis = 2000), // 2 seconds duration
        label = ""
    )
    val rotation = animateFloatAsState(
        targetValue = if (scaledUp) 20f else 0f,
        animationSpec = tween(durationMillis = 2000), // 2 seconds duration
        label = ""
    )

    LaunchedEffect(key1 = Unit) {
        scaledUp = true
    }

    Image(
        painter = painterResource(id = R.drawable.image2),
        contentDescription = "Animated Image",
        modifier = Modifier.fillMaxWidth()
            .scale(scale.value)
            .rotate(rotation.value),

    )

}


