package com.example.wishes_jetpackcompose

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.wishes_jetpackcompose.runtime.NavBarItems
import com.example.wishes_jetpackcompose.runtime.NavigationHost
import com.example.wishes_jetpackcompose.ui.theme.Inter
import com.example.wishes_jetpackcompose.ui.theme.Wishes_jetpackComposeTheme
import com.example.wishes_jetpackcompose.utlis.AppUtil.openStore
import com.example.wishes_jetpackcompose.utlis.AppUtil.openUrl
import com.example.wishes_jetpackcompose.utlis.AppUtil.sendEmail
import com.example.wishes_jetpackcompose.utlis.AppUtil.share
import com.example.wishes_jetpackcompose.viewModel.ImagesViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Wishes_jetpackComposeTheme {

                val viewModel: ImagesViewModel = hiltViewModel()
                // A surface container using the 'background' color from the theme
                val context = LocalContext.current
                val navController = rememberNavController()
                var showAlertDialog by remember { mutableStateOf(false) }

                BackHandler {
                    showAlertDialog = true
                }



                Surface() {
                    var navigateClick by remember { mutableStateOf(false) }

                    val offSetAnim by animateDpAsState(
                        targetValue = if (navigateClick) 300.dp else 0.dp,
                        tween(1000)
                    )
                    val clipDp by animateDpAsState(
                        targetValue = if (navigateClick) 60.dp else 0.dp,
                        tween(1000)
                    )
                    val scaleAnim by animateFloatAsState(
                        targetValue = if (navigateClick) 0.5f else 1.0f,
                        tween(1000)
                    )

                    NavigationDrawer() {
                        navigateClick = false
                    }


                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(scaleAnim)
                            .offset(x = offSetAnim)
                            .clip(RoundedCornerShape(clipDp))
                        ,
                        contentColor = MaterialTheme.colorScheme.background,
                        topBar = {
                            TopBar() {
                                navigateClick = !navigateClick
                            }
                        },
                        bottomBar = {
                            if (currentRoute(navController) != "viewPager")
                                BottomNavigationBar(navController = navController)
                        },

                        )
                    {
                        Column(modifier = Modifier.padding(it)) {
                            NavigationHost(navController = navController)
                        }
                        if (showAlertDialog) {
                            AlertDialog(
                                onDismissRequest = {
                                    showAlertDialog = false
                                },
                                title = {
                                    Text(
                                        text = stringResource(R.string.rate_title),
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                },
                                text = {
                                    Text(
                                        stringResource(R.string.sure),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            openStore(context)

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
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {

    BottomNavigation(
        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route

        NavBarItems.BarItems.forEach { navItem ->
            BottomNavigationItem(
                selected = currentRoute == navItem.route,
                modifier = Modifier.background(MaterialTheme.colorScheme.primary),
                onClick = {
                    navController.navigate(navItem.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },

                icon = {
                    Icon(
                        imageVector = navItem.image,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = navItem.title
                    )
                },
                label = {
                    Text(
                        text = navItem.title, color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = Inter
                        )
                    )
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(onDrawer: () -> Unit) {
    val context = LocalContext.current
    var isMoreOptionPopupShowed by remember { mutableStateOf(false) }
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        title = {
            Text(
                text = "wishes 2022",
                color = MaterialTheme.colorScheme.onPrimary,
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
                    painter = painterResource(id = R.drawable.ic_setting),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = null
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
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
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


@Composable
fun NavigationDrawer(onClick: () -> Unit) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Column() {
            Box(
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Wishes",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            ItemDrawer("Invite", Icons.Default.Person) {
                share(context)
            }
            ItemDrawer("Rate", Icons.Default.Star) {
                openStore(context)
            }
            ItemDrawer("Our Apps", Icons.Default.List) {

            }
            ItemDrawer("Feedback", Icons.Default.Email) {
                sendEmail(context)
            }
            ItemDrawer("Privacy Policy", Icons.Default.Info) {
                openUrl(context)
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onClick()
                    }, horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Icon(Icons.Default.ArrowBack, contentDescription = "")
            }
        }
    }
}

@Composable
fun ItemDrawer(text: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .height(60.dp)
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(20.dp))
        Icon(icon, contentDescription = "")
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium
        )
    }

}


