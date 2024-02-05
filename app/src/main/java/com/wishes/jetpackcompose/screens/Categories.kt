package com.example.wishes_jetpackcompose

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.wishes.jetpackcompose.runtime.NavRoutes
import com.wishes.jetpackcompose.utlis.Const
import com.wishes.jetpackcompose.utlis.DEFAULT_RECIPE_IMAGE
import com.wishes.jetpackcompose.utlis.Resource
import com.wishes.jetpackcompose.utlis.loadPicture
import com.wishes.jetpackcompose.viewModel.ImagesViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi


@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun Categories(
    viewModel: ImagesViewModel,
    navHostController: NavHostController,
    paddingValues: PaddingValues,
) {
    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current
    var categories = viewModel.categories.collectAsState()

    LaunchedEffect(categories.value.data?.size) {

        if (categories.value.data.isNullOrEmpty()){
            Log.d("images", "categories")
            viewModel.getCategories()
        }

    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        when (categories.value) {
            is Resource.Success -> {
                items(categories.value.data!!.size) {
                    val category = categories.value.data!![it]
                    val fixedImageUrl = category.image_url.replace("\\", "/")
                    //Log.d("images",Const.BASE_URL + "/"+category.image_url)
                    val image = loadPicture(
                        url = Const.BASE_URL + "/" + fixedImageUrl,
                        defaultImage = DEFAULT_RECIPE_IMAGE
                    ).value
                    image?.let { img ->
                        ItemCategory(category.name, img.asImageBitmap()) {
                            navHostController.navigate(NavRoutes.ByCat.route + "/" + category.id)
                        }
                    }
                }
            }

            is Resource.Loading -> {
                item {
                    repeat(10) {
                        LoadingShimmerEffect()
                    }
                }
            }

            is Resource.Error -> {
                Toast.makeText(context, "try later", Toast.LENGTH_SHORT).show()
            }

            else -> {

            }
        }
    }
}


@Composable
fun ItemCategory(text: String, painter: ImageBitmap, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .clickable {
                onClick()
            },

        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            val context = LocalContext.current
            androidx.compose.foundation.Image(
                bitmap = painter, contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = "$text",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun LoadingShimmerEffect() {
    //These colors will be used on the brush. The lightest color should be in the middle
    val gradient = listOf(
        MaterialTheme.colorScheme.primary.copy(alpha = 0.9f), //darker grey (90% opacity)
        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), //lighter grey (30% opacity)
        MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
    )

    val transition = rememberInfiniteTransition() // animate infinite times

    val translateAnimation = transition.animateFloat( //animate the transition
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000, // duration for the animation
                easing = FastOutLinearInEasing
            )
        )
    )
    val brush = Brush.linearGradient(
        colors = gradient,
        start = Offset(200f, 200f),
        end = Offset(
            x = translateAnimation.value,
            y = translateAnimation.value
        )
    )
    ShimmerGridItem(brush = brush)
}

@Composable
fun ShimmerGridItem(brush: Brush) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 8.dp), verticalAlignment = Alignment.Top
    ) {
        Spacer(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(brush)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(verticalArrangement = Arrangement.Center) {
            Spacer(
                modifier = Modifier
                    .height(20.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .fillMaxWidth(fraction = 0.5f)
                    .background(brush)
            )

            Spacer(modifier = Modifier.height(10.dp)) //creates an empty space between
            Spacer(
                modifier = Modifier
                    .height(20.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .fillMaxWidth(fraction = 0.7f)
                    .background(brush)
            )

            Spacer(modifier = Modifier.height(10.dp)) //creates an empty space between
            Spacer(
                modifier = Modifier
                    .height(20.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .fillMaxWidth(fraction = 0.9f)
                    .background(brush)
            )
        }
    }
}