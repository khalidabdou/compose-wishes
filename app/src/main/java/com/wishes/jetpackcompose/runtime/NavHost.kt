package com.wishes.jetpackcompose.runtime

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
import com.wishes.jetpackcompose.viewModel.ImagesViewModel


@Composable
fun NavigationHost(navController: NavHostController,viewModel: ImagesViewModel) {

    NavHost(
        navController = navController,
        startDestination = NavRoutes.Home.route,
    ) {


        composable(NavRoutes.Home.route) {
            Home(viewModel, navController)
        }

        composable(NavRoutes.Favorites.route) {
            Favorites(viewModel, navController, PaddingValues())
        }
        composable(NavRoutes.Categories.route) {
            Categories(viewModel, navController, PaddingValues())
        }
        composable(NavRoutes.Splash.route) {
            Splash(navController,viewModel)
        }

        composable(NavRoutes.ByCat.route+"/{id}") {
            val id=it.arguments?.getString("id")
            id.let { id->
                ByCat(viewModel,navController,id!!.toInt())
            }

        }

        composable(NavRoutes.ViewPager.route) {
             navController.previousBackStackEntry?.savedStateHandle?.get<Page>("page").let {
                ViewPager(viewModel,navController, it?.page, it?.imagesList,it?.cat)
            }
        }

    }
}
