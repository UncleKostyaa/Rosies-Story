package com.unclekostya.scaryrosiesstory.presentation.chat

import android.R
import android.icu.text.CaseMap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.unclekostya.scaryrosiesstory.data.local.entity.ChoiceEntity
import com.unclekostya.scaryrosiesstory.data.local.entity.MessageEntity

import com.unclekostya.scaryrosiesstory.presentation.story.StoryViewModel
import com.unclekostya.scaryrosiesstory.presentation.viewmodel.CatalogViewModel
import com.unclekostya.scaryrosiesstory.ui.theme.PurpleGrey80
import kotlinx.coroutines.flow.MutableStateFlow


@Composable
fun ChatScreen(
    storyId: Int,
    storyViewModel: StoryViewModel,
    navController: NavHostController
) {
    val uiState by storyViewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    LaunchedEffect(uiState.messages.size) {
        listState.animateScrollToItem(uiState.messages.size)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            itemsIndexed(uiState.messages) { index, item ->
                val previous = uiState.messages.getOrNull(index - 1)
                val showAvatar = previous?.sender != item.sender
                BubbleMessage(item, showAvatar)
            }
        }
        if (uiState.typingMessage != null) {
            val lastNonPlayer = uiState.messages.lastOrNull { it.sender != "player" }
            TypingIndicator(
                senderImagePath = lastNonPlayer?.senderImagePath,
                text = uiState.typingMessage!!
            )
        }
        else if (uiState.choices.isNotEmpty()) {
            ChatBox(
                choices = uiState.choices,
                storyViewModel = storyViewModel,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
        else if (uiState.pendingPlayerMessage != null) {
            Button(
                onClick = { storyViewModel.sendPendingPlayerMessage() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(text = uiState.pendingPlayerMessage?.text ?: "")
            }
        }

    }
}

@Composable
fun TypingIndicator(
    text: String,
    senderImagePath: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (senderImagePath != null) {
            Image(
                painter = rememberAsyncImagePainter("file:///android_asset/$senderImagePath"),
                contentDescription = "Typing avatar",
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.White, CircleShape)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = Color.LightGray,
            fontSize = 14.sp
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
                onClick = { storyViewModel.onChoiceSelected(choice) },
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
    showAvatar: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalArrangement = if (message.sender == "player") Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (message.sender != "player" && showAvatar) {
            Image(
                painter = rememberAsyncImagePainter("file:///android_asset/${message.senderImagePath}"),
                contentDescription = message.sender,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.dp,Color.Black,CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        else if (message.sender != "player") {
            Spacer(modifier = Modifier.width(46.dp))
        }
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 48.dp,
                        topEnd = 48.dp,
                        bottomStart = if (message.sender == "player") 48.dp else 0.dp,
                        bottomEnd = if (message.sender == "player") 0.dp else 48.dp
                    )
                )
                .background(
                    if (message.sender == "player") Color(0xFF6C63FF) else Color.White
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = message.text,
                color = Color.Black
            )
        }
        if (message.sender == "player" && showAvatar) {
            Spacer(modifier = Modifier.width(6.dp))
            Image(
                painter = rememberAsyncImagePainter("file:///android_asset/${message.senderImagePath}"),
                contentDescription = message.sender,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.dp,Color.Black,CircleShape)
            )
        }
        else if (message.sender == "player") {
            Spacer(modifier = Modifier.width(46.dp))
        }
    }
}


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
