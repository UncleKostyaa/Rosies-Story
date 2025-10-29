package com.unclekostya.scaryrosiesstory.presentation.chat

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.unclekostya.scaryrosiesstory.data.local.entity.ChoiceEntity
import com.unclekostya.scaryrosiesstory.data.local.entity.MessageEntity

import com.unclekostya.scaryrosiesstory.presentation.story.StoryViewModel
import com.unclekostya.scaryrosiesstory.ui.theme.PurpleGrey80
import kotlinx.coroutines.flow.MutableStateFlow


@Composable
fun ChatScreen(
    storyId: Int,
    storyViewModel: StoryViewModel,
    navController: NavHostController
) {
    val uiState by storyViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
    ) {

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(uiState.messages.filterNotNull()) { item ->
                BubbleMessage(item)
            }
        }

        ChatBox(
            choices = uiState.choices,
            storyViewModel = storyViewModel,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
    }
}


@Composable
fun ChatBox(
    choices: List<ChoiceEntity>,
    storyViewModel: StoryViewModel,
    modifier: Modifier
) {
    var isChoiceSelected by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        choices.forEach { choice ->
            Button(
                onClick = {
                    if (!isChoiceSelected) {
                        isChoiceSelected = true
                        storyViewModel.onChoiceSelected(choice)
                    }
                },
                enabled = !isChoiceSelected,
                modifier = Modifier.padding(4.dp)
            ) {
                Text(text = choice.text)
            }
        }
    }
}

@Composable
fun BubbleMessage(
    message: MessageEntity,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Box(
            modifier = Modifier
                .align(
                    if (message.sender == "player")
                        Alignment.CenterEnd
                    else
                        Alignment.CenterStart
                )
                .clip(
                    RoundedCornerShape(
                        topStart = 48.dp,
                        topEnd = 48.dp,
                        bottomStart = if (message.sender == "player")48.dp else 0.dp,
                        bottomEnd = if (message.sender == "player") 0.dp else 48.dp
                    )
                )
                .background(
                    if (message.sender == "player") Color(0xFF6C63FF) else PurpleGrey80
                )
                .padding(16.dp)
        ) {
            Text(text = message.text)
        }
    }
}

