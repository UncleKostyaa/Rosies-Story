package com.unclekostya.scaryrosiesstory.presentation.catalog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.unclekostya.scaryrosiesstory.presentation.story.StoryViewModel
import com.unclekostya.scaryrosiesstory.presentation.viewmodel.CatalogViewModel


@Composable
fun CatalogScreen(
    modifier: Modifier = Modifier,
    storyViewModel: StoryViewModel,
    catalogViewModel: CatalogViewModel,
    navController: NavHostController

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
            Text(text = it.title)
            Spacer(modifier = modifier.padding(10.dp))
            Text(text = it.description)
        }

    }
}