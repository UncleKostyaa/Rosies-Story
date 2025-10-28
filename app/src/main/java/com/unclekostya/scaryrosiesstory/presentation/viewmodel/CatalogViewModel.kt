package com.unclekostya.scaryrosiesstory.presentation.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.unclekostya.scaryrosiesstory.data.local.entity.StoryEntity
import com.unclekostya.scaryrosiesstory.data.repository.StoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CatalogViewModel(
    private val repository: StoryRepository
) : ViewModel() {

    val story = mutableStateOf("Loading stories...")

    val stories = mutableStateOf<List<StoryEntity>>(emptyList())

    fun getAll() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO){
                    stories.value = repository.getAllStories()
                }
            } catch (e: Exception) {
                story.value = "Error: ${e.message}"
            }
        }
    }
}