package com.wishes.jetpackcompose.screens.comp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.wishes.jetpackcompose.R

@Composable
fun EmptyState(text: String?="") {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.placeholder),
            contentDescription = if (text.isNullOrBlank()) "Empty" else text,
            modifier = Modifier.size(120.dp)
        )
        Text("No images available", modifier = Modifier.padding(top = 16.dp), color = MaterialTheme.colors.background)
    }
}
