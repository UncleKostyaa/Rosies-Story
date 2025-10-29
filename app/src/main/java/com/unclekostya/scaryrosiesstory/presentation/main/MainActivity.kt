package com.unclekostya.scaryrosiesstory.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.unclekostya.scaryrosiesstory.ui.theme.ScaryRosiesStoryTheme
import com.unclekostya.scaryrosiesstory.R
import com.unclekostya.scaryrosiesstory.data.local.database.StoryDatabase
import com.unclekostya.scaryrosiesstory.data.repository.StoryRepository
import com.unclekostya.scaryrosiesstory.data.repository.StoryRepositoryImpl
import com.unclekostya.scaryrosiesstory.presentation.chat.ChatTopBar
import com.unclekostya.scaryrosiesstory.presentation.navigation.NavGraph
import com.unclekostya.scaryrosiesstory.presentation.story.StoryViewModel
import com.unclekostya.scaryrosiesstory.presentation.viewmodel.CatalogViewModel
import com.unclekostya.scaryrosiesstory.presentation.viewmodel.CatalogViewModelFactory
import com.unclekostya.scaryrosiesstory.presentation.viewmodel.StoryViewModelFactory

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val comfortaFamily = FontFamily(
                Font(R.font.comforta_reg, FontWeight.Bold)
            )
            val roboticFamily = FontFamily(
                Font(R.font.robotic_bold, FontWeight.Bold)
            )

            //навигация
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val routes = listOf("catalog","profile","settings")

            //база данных
            val db = StoryDatabase.getDatabase(context = LocalContext.current)
            val dao = db.storyDao()
            val repository = StoryRepositoryImpl(dao)

            //viewModel для ОТДЕЛЬНОЙ переписки
            val storyViewModel: StoryViewModel = viewModel(factory = StoryViewModelFactory(repository))

            //viewModel для ВСЕХ переписок
            val catalogViewModel: CatalogViewModel = viewModel(factory = CatalogViewModelFactory(repository))

            //для смены title в ChatScreen
            val isChat = currentRoute?.startsWith("chat") == true



            ScaryRosiesStoryTheme {
                Scaffold(
                    topBar = {
                        if (!isChat) {
                            CenterAlignedTopAppBar(
                                title = {
                                    Text(
                                        text = stringResource(R.string.topbar_catalog_name),
                                        fontFamily = comfortaFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 28.sp
                                    )
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = colorResource(R.color.topbottombars_content_color),
                                    titleContentColor = colorResource(R.color.white)
                                )
                            )
                        } else  {
                            ChatTopBar(
                                navController = navController,
                                comfortaFamily = comfortaFamily,
                                storyViewModel = storyViewModel
                            )
                        }

                    },
                    bottomBar = {
                        BottomAppBar (
                            content = {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .padding(top = 8.dp, bottom = 8.dp)
                                        .fillMaxWidth()
                                ) {
                                    BottomNavigation(
                                        navController = navController,
                                        routesList = routes,
                                        currentRoute = currentRoute
                                    )
                                }
                            },
                            containerColor = colorResource(R.color.topbottombars_content_color)
                        )
                    },
                ) { innerPadding ->
                    Surface(modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                        color = colorResource(R.color.grey_catalog_color)
                    ) {
                        NavGraph(
                            navController = navController,
                            storyViewModel = storyViewModel,
                            catalogViewModel = catalogViewModel,
                            currentRoute = currentRoute
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigation(
    navController: NavController,
    routesList: List<String>,
    currentRoute: String?,
    modifier: Modifier = Modifier
) {
    routesList.forEach { route ->
        val imageResource = when(route) {
            "catalog" -> R.drawable.book_closed_filled_white
            "profile" -> R.drawable.person_filled_white
            "settings" -> R.drawable.settings_filled_white
            else -> 0
        }
        IconButton(
            onClick = {
                if(currentRoute != route) {
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) {saveState = true}
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            modifier = modifier
                .padding(start = 40.dp, end = 40.dp)

        ) {
            Icon(
                painter = painterResource(imageResource),
                contentDescription = route,
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(60.dp)
            )
        }
    }

}