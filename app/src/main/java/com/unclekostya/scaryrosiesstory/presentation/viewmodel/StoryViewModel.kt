package com.unclekostya.scaryrosiesstory.presentation.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unclekostya.scaryrosiesstory.data.local.entity.ChoiceEntity
import com.unclekostya.scaryrosiesstory.data.local.entity.MessageEntity
import com.unclekostya.scaryrosiesstory.data.local.entity.UserProgressEntity
import com.unclekostya.scaryrosiesstory.data.repository.StoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ChatUiState(
    val messages: List<MessageEntity> = emptyList(),
    val choices: List<ChoiceEntity> = emptyList(),
    val currentStoryId: Int? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class StoryViewModel(
    private val repository: StoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState

    fun loadStory(storyId: Int) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                val progress = repository.getUserProgress(storyId)
                val startMessageId = progress?.currentMessageId ?: 1
                val message = repository.getMessage(storyId, startMessageId)
                val choices = repository.getChoicesForMessage(startMessageId)

                _uiState.value = ChatUiState(
                    messages = listOf(message),
                    choices = choices,
                    currentStoryId = storyId,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }

    fun onChoiceSelected(choice: ChoiceEntity) {
        viewModelScope.launch {
            val storyId = _uiState.value.currentStoryId ?: return@launch
            try {
                repository.saveProgress(
                    UserProgressEntity(
                        storyId = storyId,
                        currentMessageId = choice.nextMessageId,
                        lastChoiceId = choice.Id
                    )
                )

                val nextMessage = repository.getMessage(storyId, choice.nextMessageId)
                val newChoices = repository.getChoicesForMessage(choice.nextMessageId)

                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages + nextMessage,
                    choices = newChoices
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
        }
    }

    fun resetStory() {
        _uiState.value = ChatUiState()
    }
}
