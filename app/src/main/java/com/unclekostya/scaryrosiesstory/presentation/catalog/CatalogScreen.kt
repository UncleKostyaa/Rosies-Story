package com.unclekostya.scaryrosiesstory.presentation.catalog

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.room.util.TableInfo
import coil.compose.rememberAsyncImagePainter
import com.unclekostya.scaryrosiesstory.data.local.entity.StoryEntity
import com.unclekostya.scaryrosiesstory.presentation.story.StoryViewModel
import com.unclekostya.scaryrosiesstory.presentation.viewmodel.CatalogViewModel
import java.io.File


@Composable
fun CatalogScreen(
    modifier: Modifier = Modifier,
    storyViewModel: StoryViewModel,
    catalogViewModel: CatalogViewModel,
    navController: NavHostController,
    currentRoute: String?

) {
    Column(
        modifier = modifier
            .padding(10.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        LaunchedEffect(Unit) {
            catalogViewModel.getAll()

        }

        val storyList by catalogViewModel.stories

        storyList.forEach { it ->
            StoryCard(
                id = it.id,
                title = it.title,
                description = it.description,
                coverPath = it.coverPath,
                navController = navController,
                currentRoute = currentRoute,
                storyViewModel = storyViewModel
            )
            Spacer(modifier = modifier.padding(2.dp))
        }

    }
}

@Composable
fun StoryCard(
    id: Int,
    title: String,
    description: String,
    coverPath: String,
    navController: NavHostController,
    currentRoute: String?,
    storyViewModel: StoryViewModel,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.LightGray
        ),
        modifier = modifier
            .padding(6.dp),
        onClick = {
            storyViewModel.loadStory(id)
            navController.navigate("chat/$id")
        }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Row {
                val context = LocalContext.current
                val assetManager = context.assets
                val imgBitmap = try {
                    val inputStream = assetManager.open("covers/${File(coverPath).name}")
                    BitmapFactory.decodeStream(inputStream)
                } catch (e: Exception) {
                    null
                }
                imgBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = title,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                Column(modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f)
                ) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(
                    onClick = { storyViewModel.resetStory(id) }
                ) {
                    Text("Delete progress")
                }
            }
        }
    }
}