package com.unclekostya.scaryrosiesstory.presentation.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.unclekostya.scaryrosiesstory.data.local.entity.MessageEntity


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
                        48.dp
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