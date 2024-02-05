package com.example.wishes_jetpackcompose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.wishes.jetpackcompose.data.entities.Latest
import com.wishes.jetpackcompose.runtime.NavRoutes
import com.wishes.jetpackcompose.screens.Latest
import com.wishes.jetpackcompose.utlis.Resource
import com.wishes.jetpackcompose.viewModel.ImagesViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi


@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun Favorites(
    viewModel: ImagesViewModel,
    navHostController: NavHostController,
    paddingValues: PaddingValues,
) {

    val context = LocalContext.current
    val scrollState = rememberLazyGridState()
    val lazyGridState = LazyGridState
    val lifecycleOwner: LifecycleOwner
    val favorites = viewModel.favorites.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getFavoritesRoom()
    }
    Latest(
        scrollState = scrollState,
        paddingValues = paddingValues,
        latest = favorites.value
    ) {
        viewModel.setImagesForViewPager(ImagesViewModel.VIEW_PAGER.FAVORITES)
        navHostController.currentBackStackEntry?.savedStateHandle?.set(
            key = "page",
            value = it
        )
        navHostController.navigate(NavRoutes.ViewPager.route)

    }
}