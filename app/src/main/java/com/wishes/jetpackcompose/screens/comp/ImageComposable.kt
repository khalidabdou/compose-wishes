package com.wishes.jetpackcompose.screens.comp

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.wishes.jetpackcompose.R
import com.wishes.jetpackcompose.admob.showInterstitialAfterClick

@Composable
fun ImageItem(painter: ImageBitmap?, onClick: () -> Unit) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .height(200.dp)
            .clickable {
                onClick()
                showInterstitialAfterClick(context)
            },
    ) {
        if (painter == null) {
            Image(
                painter = painterResource(id = R.drawable.placeholder), contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
        } else
            Image(
                bitmap = painter, contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
    }
}