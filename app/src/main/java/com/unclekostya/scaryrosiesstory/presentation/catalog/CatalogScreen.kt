package com.unclekostya.scaryrosiesstory.presentation.catalog

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.unclekostya.scaryrosiesstory.presentation.story.StoryViewModel


@Composable
fun CatalogScreen(
    modifier: Modifier = Modifier,
    storyViewModel: StoryViewModel,
    navController: NavHostController
) {
    Text("YOUR STORY", color = Color.White)
}