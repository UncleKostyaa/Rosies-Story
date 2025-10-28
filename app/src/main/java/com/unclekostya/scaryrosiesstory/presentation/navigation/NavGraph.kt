package com.unclekostya.scaryrosiesstory.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.unclekostya.scaryrosiesstory.presentation.catalog.CatalogScreen
import com.unclekostya.scaryrosiesstory.presentation.chat.ChatScreen
import com.unclekostya.scaryrosiesstory.presentation.profile.ProfileScreen
import com.unclekostya.scaryrosiesstory.presentation.settings.SettingsScreen

@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(navController = navController,startDestination = "catalog") {
        composable("catalog") {
            CatalogScreen()
        }
        composable("settings") {
            SettingsScreen()
        }
        composable("chat") {
            ChatScreen()
        }
        composable("profile") {
            ProfileScreen()
        }
    }
}