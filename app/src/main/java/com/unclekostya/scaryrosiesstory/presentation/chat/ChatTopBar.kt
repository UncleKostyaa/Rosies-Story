package com.unclekostya.scaryrosiesstory.presentation.chat

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.unclekostya.scaryrosiesstory.presentation.story.StoryViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(
    navController: NavHostController,
    comfortaFamily: FontFamily,
    storyViewModel: StoryViewModel
) {
    val uiState by storyViewModel.uiState.collectAsState()
    val chatTitle = uiState.storyTitle ?: "Chat"
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = chatTitle,
                fontFamily = comfortaFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(com.unclekostya.scaryrosiesstory.R.color.topbottombars_content_color),
            titleContentColor = colorResource(com.unclekostya.scaryrosiesstory.R.color.white)
        )
    )
}