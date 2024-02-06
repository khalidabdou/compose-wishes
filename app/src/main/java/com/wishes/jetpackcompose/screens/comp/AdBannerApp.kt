package com.wishes.jetpackcompose.screens.comp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wishes.jetpackcompose.R
import com.wishes.jetpackcompose.data.entities.App
import com.wishes.jetpackcompose.utlis.AppUtil.openUrl
import com.wishes.jetpackcompose.utlis.DEFAULT_RECIPE_IMAGE
import com.wishes.jetpackcompose.utlis.loadPicture

@Composable
fun AdBannerApp(app:App?) {
    val context= LocalContext.current
    if (app != null)
        Box {
            val largImage = loadPicture(
                url = app.largePhotoUrl,
                defaultImage = DEFAULT_RECIPE_IMAGE
            ).value

            largImage?.let {
                Image(
                    bitmap = it.asImageBitmap(), contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth().height(200.dp).clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    //.background(MaterialTheme.colorScheme.primary)
                    .padding(5.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {

                Spacer(modifier = Modifier.width(6.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val image = loadPicture(
                        url = app.iconUrl,
                        defaultImage = DEFAULT_RECIPE_IMAGE
                    ).value

                    image?.let {
                        Image(
                            bitmap = it.asImageBitmap(), contentDescription = null,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Text(
                        text = app.name,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            modifier = Modifier,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                            onClick = {
                                openUrl(context, app.appUrl!!)
                            }
                        ) {
                            Text(
                                text = stringResource(R.string.install),
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                }

            }
            Text(
                text = "Ad",
                modifier = Modifier.background(MaterialTheme.colorScheme.onPrimary),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )

        }


}