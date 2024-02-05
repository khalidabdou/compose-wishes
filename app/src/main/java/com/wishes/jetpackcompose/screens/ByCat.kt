package com.example.wishes_jetpackcompose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.wishes.jetpackcompose.runtime.NavRoutes
import com.wishes.jetpackcompose.screens.Latest
import com.wishes.jetpackcompose.viewModel.ImagesViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi


@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun ByCat(viewModel: ImagesViewModel, navHostController: NavHostController, catId: Int) {

    val context = LocalContext.current
    val scrollState = rememberLazyGridState()
    val images = viewModel.imagesByCategory.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.getImageByCategory(catId)
    }

    Latest(scrollState = scrollState, paddingValues = PaddingValues(), latest = images.value) {
        viewModel.setImagesForViewPager(ImagesViewModel.VIEW_PAGER.BY_CATEGORY)
        navHostController.currentBackStackEntry?.savedStateHandle?.set(
            key = "page",
            value = it
        )
        navHostController.navigate(NavRoutes.ViewPager.route)
    }
}