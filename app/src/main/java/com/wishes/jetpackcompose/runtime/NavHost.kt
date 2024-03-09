package com.wishes.jetpackcompose.runtime

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.wishes_jetpackcompose.ByCat
import com.example.wishes_jetpackcompose.Categories
import com.example.wishes_jetpackcompose.Favorites
import com.example.wishes_jetpackcompose.Home
import com.wishes.jetpackcompose.data.entities.Page
import com.wishes.jetpackcompose.screens.Splash
import com.wishes.jetpackcompose.screens.ViewPager
import com.wishes.jetpackcompose.screens.comp.LanguageScreen
import com.wishes.jetpackcompose.viewModel.AdsViewModel
import com.wishes.jetpackcompose.viewModel.ImagesViewModel


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun NavigationHost(navController: NavHostController,viewModel: ImagesViewModel,adsViewModel: AdsViewModel) {

    NavHost(
        navController = navController,
        startDestination = NavRoutes.Splash.route,
    ) {


        composable(NavRoutes.Home.route) {
            Home(viewModel,adsViewModel, navController)
        }

        composable(NavRoutes.Languages.route) {
            LanguageScreen(viewModel)
        }

        composable(NavRoutes.Favorites.route) {
            Favorites(viewModel, adsViewModel = adsViewModel, navController, PaddingValues())
        }
        composable(NavRoutes.Categories.route) {
            Categories(navHostController = navController, viewModel =viewModel , adsViewModel =adsViewModel , paddingValues = PaddingValues() )
        }
        composable(NavRoutes.Splash.route) {
            Splash(navController,viewModel,adsViewModel)
        }

        composable(NavRoutes.ByCat.route+"/{id}") {
            val id=it.arguments?.getString("id")
            id.let { id->
                ByCat(viewModel, adsViewModel = adsViewModel,navController,id!!.toInt())
            }

        }

        composable(NavRoutes.ViewPager.route) {
             navController.previousBackStackEntry?.savedStateHandle?.get<Int>("page").let {
                ViewPager(viewModel,adsViewModel, it )
            }
        }

    }
}
