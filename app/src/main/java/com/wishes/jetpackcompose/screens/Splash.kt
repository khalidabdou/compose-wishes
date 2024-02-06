package com.wishes.jetpackcompose.screens

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.wishes.jetpackcompose.R
import com.wishes.jetpackcompose.runtime.NavRoutes
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
            navController.navigate(NavRoutes.Home.route)
        }

        else -> {
            LaunchedEffect(key1 = true) {
                viewModel.setMessage(context)
                startAnimation = true
                delay(2000)
                navController.popBackStack()
                navController.navigate(NavRoutes.Home.route)
                //Toast.makeText(context, "${viewModel.adsList.value}", Toast.LENGTH_LONG).show()
            }
        }
    }


    Splash(alpha = alphaAnim.value, message.value)
}

@Composable
fun Splash(alpha: Float, message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Image(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape),
            painter = painterResource(id = R.mipmap.ic_launcher_foreground),
            contentDescription = "Logo Icon",

            //tint = Color.DarkGray
        )
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

