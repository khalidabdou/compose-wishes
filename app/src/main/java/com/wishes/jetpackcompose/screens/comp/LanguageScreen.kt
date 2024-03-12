package com.wishes.jetpackcompose.screens.comp

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.wishes.jetpackcompose.data.entities.AppLanguage
import com.wishes.jetpackcompose.runtime.NavRoutes
import com.wishes.jetpackcompose.utlis.Resource
import com.wishes.jetpackcompose.viewModel.ImagesViewModel


@Composable
fun LanguageScreen(navHostController: NavHostController, viewModel: ImagesViewModel) {
    val languageResource by viewModel.appDetails.collectAsState()
    var selectedLanguage by remember { mutableStateOf<AppLanguage?>(null) }


    Column {
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Select Your Language",
                color = MaterialTheme.colors.onPrimary,
                style = MaterialTheme.typography.h6
            )
        }

        when (languageResource) {
            is Resource.Loading -> {
                // Display a loading indicator
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }


            is Resource.Success -> {
                Log.d("languages", languageResource.data.toString())
                languageResource.data?.Applanguages?.let { languages ->

                    Log.d("languages", languages.toString())
                    LazyColumn(
                        contentPadding = PaddingValues(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        )
                    ) {
                        items(languages.size) { index ->
                            ItemLanguage(
                                language = languages[index].appLanguage,
                                isSelected = languages[index].appLanguage == selectedLanguage
                            ) {
                                selectedLanguage = it

                            }
                        }
                    }
                }
            }

            is Resource.Error -> {
                // Display an error message
                Text(
                    "Error: ${languageResource.message}",
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }

            else -> {
                // Handle Idle or any other state
                Text("Please wait...", modifier = Modifier.padding(16.dp))
            }
        }



        selectedLanguage?.let {
            AnimatedVisibility(
                visible = selectedLanguage != null,
                enter = slideInVertically(
                    // Start the animation from the bottom of the component
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 300)
                ),
                exit = slideOutVertically(
                    // Exit towards the bottom of the screen
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 300)
                )
            ) {
                Button(
                    onClick = {
                        selectedLanguage?.let {
                            viewModel.saveLanguage(it)
                            navHostController.popBackStack()
                            navHostController.navigate(NavRoutes.Home.route)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    enabled = selectedLanguage != null
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
                        Spacer(Modifier.width(8.dp)) // Add space between the icon and text
                        Text("Next")
                    }
                }
            }
        }


    }
}

@Composable
fun ItemLanguage(
    language: AppLanguage,
    isSelected: Boolean,
    onLanguageSelected: (AppLanguage) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp)) // Keep the rounded corners
            .then(
                if (isSelected) {
                    Modifier
                        .border(
                            width = 2.dp, // You can adjust the border thickness here
                            color = MaterialTheme.colors.onPrimary, // This can be any color to indicate selection
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(MaterialTheme.colors.primarySurface)
                } else Modifier.background(MaterialTheme.colors.primary)
            )
            //
            .clickable { onLanguageSelected(language) } // Adding clickable here to make the whole Row clickable
            .padding(16.dp), // Padding inside the row, adjust as needed
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                text = language.name,
                color = MaterialTheme.colors.onPrimary,
                style = MaterialTheme.typography.subtitle1
            )
            Text(
                text = language.label ?: "",
                color = MaterialTheme.colors.onPrimary,
                style = MaterialTheme.typography.subtitle2
            )
        }
    }
}
