package com.wishes.jetpackcompose.screens.comp

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonColors
import androidx.compose.material.Text
import androidx.compose.material3.RadioButtonDefaults
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
import com.wishes.jetpackcompose.data.entities.AppLanguage
import com.wishes.jetpackcompose.utlis.Resource
import com.wishes.jetpackcompose.viewModel.ImagesViewModel


@Composable
fun LanguageScreen(viewModel: ImagesViewModel) {
    val languageResource by viewModel.appDetails.collectAsState()
    var selectedLanguage by remember { mutableStateOf<AppLanguage?>(null) }

    Column {
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
            AnimatedVisibility(visible = selectedLanguage!=null) {
                Button(
                    onClick = { selectedLanguage?.let {} },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    enabled = selectedLanguage != null
                ) {
                    Text("Next")
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
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colors.background),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,

            onClick = { onLanguageSelected(language) }
        )
        Text(
            text = language.name,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}