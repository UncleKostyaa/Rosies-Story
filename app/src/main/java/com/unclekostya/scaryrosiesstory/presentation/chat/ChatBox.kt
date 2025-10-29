package com.unclekostya.scaryrosiesstory.presentation.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unclekostya.scaryrosiesstory.data.local.entity.ChoiceEntity
import com.unclekostya.scaryrosiesstory.presentation.story.StoryViewModel
import kotlin.collections.forEach

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