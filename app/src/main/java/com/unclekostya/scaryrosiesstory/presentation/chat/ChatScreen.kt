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
