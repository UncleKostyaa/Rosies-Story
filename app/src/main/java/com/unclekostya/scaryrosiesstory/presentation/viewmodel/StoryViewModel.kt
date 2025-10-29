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
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

                val progress = repository.getUserProgress(storyId)
                val startMessage: MessageEntity? = when (val dbId = progress?.currentMessageDbId) {
                    null -> repository.getMessageByLocalId(storyId, 1)
                    else -> repository.getMessageByDbId(storyId, dbId)
                }

                val messages = mutableListOf<MessageEntity>()
                var choices: List<ChoiceEntity> = emptyList()

                var current: MessageEntity? = startMessage
                while (current != null) {
                    messages += current
                    choices = repository.getChoicesForMessageDb(current.id)
                    if (choices.isNotEmpty()) break
                    current = current.nextMessageDbId?.let { nextDbId ->
                        repository.getMessageByDbId(storyId, nextDbId)
                    }
                }

                _uiState.value = ChatUiState(
                    messages = messages,
                    choices = choices,
                    currentStoryId = storyId,
                    isLoading = false,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Unknown error"
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
                        currentMessageDbId = choice.nextMessageDbId,
                        lastChoiceDbId = choice.id
                    )
                )

                val nextMessage = choice.nextMessageDbId?.let {
                    repository.getMessageByDbId(storyId, it)
                }

                val newChoices = nextMessage?.let { repository.getChoicesForMessageDb(it.id) } ?: emptyList()

                _uiState.value = _uiState.value.copy(
                    messages = if (nextMessage != null) _uiState.value.messages + nextMessage else _uiState.value.messages,
                    choices = newChoices
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
        }
    }

    fun resetStory(storyId: Int) {
        viewModelScope.launch {
            try {
                repository.deleteUserProgress(storyId)
                _uiState.value = ChatUiState()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
        }
    }
}
