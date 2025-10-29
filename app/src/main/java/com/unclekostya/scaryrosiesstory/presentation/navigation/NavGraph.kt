package com.unclekostya.scaryrosiesstory.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.unclekostya.scaryrosiesstory.presentation.catalog.CatalogScreen
import com.unclekostya.scaryrosiesstory.presentation.chat.ChatScreen
import com.unclekostya.scaryrosiesstory.presentation.profile.ProfileScreen
import com.unclekostya.scaryrosiesstory.presentation.settings.SettingsScreen
import com.unclekostya.scaryrosiesstory.presentation.story.StoryViewModel
import com.unclekostya.scaryrosiesstory.presentation.viewmodel.CatalogViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    storyViewModel: StoryViewModel,
    catalogViewModel: CatalogViewModel,
    currentRoute: String?
) {
    NavHost(navController = navController,startDestination = "catalog") {
        composable("catalog") {
            CatalogScreen(
                storyViewModel = storyViewModel,
                navController = navController,
                catalogViewModel = catalogViewModel,
                currentRoute = currentRoute
            )
        }
        composable("settings") {
            SettingsScreen()
        }
        composable("profile") {
            ProfileScreen()
        }
        composable(
            route = "chat/{storyId}",
            arguments = listOf(navArgument("storyId") {type = NavType.IntType})
        ) { backStackEntry ->
            val storyId = backStackEntry.arguments?.getInt("storyId") ?: return@composable
            ChatScreen(
                storyId = storyId,
                storyViewModel  = storyViewModel,
                navController = navController
            )

        }
    }
}