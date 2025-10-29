package com.unclekostya.scaryrosiesstory.presentation.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.unclekostya.scaryrosiesstory.R
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
    val sideWidth = 120.dp

    TopAppBar(
        navigationIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .widthIn(min = sideWidth)
                    .padding(start = 4.dp)
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ios_arrow_back_white_filled),
                            contentDescription = "back to the catalog",
                            modifier = Modifier.size(22.dp),
                            tint = Color.Unspecified
                        )
                    }
                }
            }
        },
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = chatTitle,
                    fontFamily = comfortaFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    maxLines = 1,
                    color = Color.White
                )
            }
        },
        actions = {
            Spacer(Modifier.widthIn(min = sideWidth))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(com.unclekostya.scaryrosiesstory.R.color.topbottombars_content_color),
            titleContentColor = colorResource(com.unclekostya.scaryrosiesstory.R.color.white)
        )
    )
}