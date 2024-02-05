package com.example.wishes_jetpackcompose

import android.app.Activity
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.wishes.jetpackcompose.R
import com.wishes.jetpackcompose.runtime.NavRoutes
import com.wishes.jetpackcompose.screens.Latest
import com.wishes.jetpackcompose.screens.NavigationDrawer
import com.wishes.jetpackcompose.screens.comp.AdBannerApp
import com.wishes.jetpackcompose.ui.theme.Inter
import com.wishes.jetpackcompose.utlis.AppUtil
import com.wishes.jetpackcompose.utlis.Resource
import com.wishes.jetpackcompose.utlis.ViewUtils.Companion.customTabIndicatorOffset
import com.wishes.jetpackcompose.viewModel.ImagesViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlin.random.Random


@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalPagerApi::class,
    ExperimentalFoundationApi::class
)
@ExperimentalCoroutinesApi
@Composable
fun Home(viewModel: ImagesViewModel, navHostController: NavHostController) {
    val scrollState = rememberLazyGridState(0)
    val context = LocalContext.current
    val message = viewModel.message.collectAsState()
    val latest = viewModel.latest.collectAsState(Resource.Loading())
    val lazyGridState = LazyGridState
    var isCategoriesSelected by remember {
        mutableStateOf(false)
    }
    var isDrawerOpen by remember { mutableStateOf(false) }
    var showAlertDialog by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        viewModel.getLatestImages(page = 0)
    }

    LaunchedEffect(isCategoriesSelected) {
        Log.d("images", "selected")
    }
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) {
        3
    }

    Surface() {
        BackHandler() {
            showAlertDialog = true
        }
        //create animations

        val offSetAnim by animateDpAsState(
            targetValue = if (isDrawerOpen) 300.dp else 0.dp,
            tween(1000), label = ""
        )
        val clipDp by animateDpAsState(
            targetValue = if (isDrawerOpen) 20.dp else 0.dp,
            tween(1000), label = ""
        )
        val scaleAnim by animateFloatAsState(
            targetValue = if (isDrawerOpen) 0.5f else 1.0f,
            tween(1000), label = ""
        )

        val rotate by animateFloatAsState(
            targetValue = if (isDrawerOpen) 10f else 0f,
            tween(1000), label = ""
        )

        NavigationDrawer() {
            scope.launch {
                isDrawerOpen = false
            }
        }

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .scale(scaleAnim)
                .offset(x = offSetAnim)
                .rotate(rotate)
                .clip(RoundedCornerShape(clipDp))
                .clickable(indication = null,
                    interactionSource = remember { MutableInteractionSource() } // This is mandatory
                ) {
                    if (isDrawerOpen)
                        isDrawerOpen = !isDrawerOpen
                },
            contentColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopBar(message.value) {
                    scope.launch {
                        isDrawerOpen = !isDrawerOpen
                    }
                }
            },
            bottomBar = {
                //BottomNavigationBar(navController = navHostController)
                Column {
                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                modifier = Modifier.customTabIndicatorOffset(tabPositions[pagerState.currentPage]),
                                height = 5.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                        divider = {}
                    ) {
                        tabs.forEachIndexed { index, item ->
                            Tab(
                                selected = index == pagerState.currentPage,
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = stringResource(id = item.title))
                                    }
                                },
                                onClick = {
                                    if (isDrawerOpen)
                                        isDrawerOpen = !isDrawerOpen
                                    else
                                        scope.launch {
                                            pagerState.animateScrollToPage(index)
                                        }
                                },
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

            },


            ) {

            HorizontalPager(
                state = pagerState,
                pageContent = { page ->
                    when (page) {
                        0 -> {
                            Latest(
                                scrollState = scrollState,
                                paddingValues = it,
                                latest = latest.value
                            ) {
                                viewModel.setImagesForViewPager(ImagesViewModel.VIEW_PAGER.LATEST)
                                navHostController.currentBackStackEntry?.savedStateHandle?.set(
                                    key = "page",
                                    value = it
                                )
                                navHostController.navigate(NavRoutes.ViewPager.route)
                            }
                        }

                        1 -> {

                            Categories(
                                viewModel = viewModel,
                                navHostController = navHostController,
                                it
                            )
                        }

                        2 -> {
                            Favorites(
                                viewModel = viewModel,
                                navHostController = navHostController,
                                it
                            )
                        }
                    }
                }
            )

            if (showAlertDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showAlertDialog = false
                    },
                    title = {
                        Text(
                            stringResource(R.string.sure),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    text = {
                        val apps = viewModel.apps.value

                        if (!apps.isNullOrEmpty()) {
                            val app = apps.get(Random.nextInt(0, apps.size))
                            AdBannerApp(app)
                        }

                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                AppUtil.rateApp(context)

                            }) {
                            Text(stringResource(R.string.rate))
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                //showAlertDialog=false
                                (context as Activity).finish()
                            }) {
                            Text(stringResource(R.string.quit))
                        }
                    },
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(title: String, onDrawer: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition()
    TopAppBar(
        modifier = Modifier.clickable { onDrawer() },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        title = {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = Inter
                )
            )
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    onDrawer()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    contentDescription = null,
                    modifier = Modifier
                )
            }
        },
        actions = {


            /*if (isMoreOptionPopupShowed) {
                MoreOptionPopup(
                    options = listOf(
                        stringResource(id = R.string.rate),
                        stringResource(id = R.string.rate),
                        stringResource(id = R.string.share),

                        ),
                    onDismissRequest = {
                        isMoreOptionPopupShowed = false
                    },
                    onClick = { i ->
                        when (i) {
                            0 -> {
                                AppUtil.openStore(context)
                            }
                            1 -> {

                            }
                            2 -> {
                                AppUtil.share(context)
                            }

                        }
                    },
                    modifier = Modifier
                        .padding(8.dp)
                )
            }*/
        }
    )
}


@Composable
fun MoreOptionPopup(
    options: List<String>,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onClick: (Int) -> Unit
) {

    Popup(
        alignment = Alignment.BottomCenter,
        onDismissRequest = onDismissRequest
    ) {
        Card(
            shape = MaterialTheme.shapes.medium,
            modifier = modifier
        ) {
            options.forEachIndexed { i, label ->
                Row {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = Inter
                        ),
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                onDismissRequest()
                                onClick(i)
                            }
                    )
                }

            }
        }
    }
}

data class TabItem(
    val title: Int,
    val icon: ImageVector,
    val screen: @Composable () -> Unit,
)


val tabs = listOf(
    TabItem(
        title = R.string.Home,
        icon = Icons.Filled.Favorite,
        screen = { ->

        },
    ),
    TabItem(
        title = R.string.categories,
        icon = Icons.Filled.List,
        screen = {  ->

        },
    ),
    TabItem(
        title = R.string.fav,
        icon = Icons.Filled.List,
        screen = { ->

        },
    ),
)