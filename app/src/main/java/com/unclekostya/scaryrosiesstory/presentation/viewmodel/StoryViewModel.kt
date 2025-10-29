package com.unclekostya.scaryrosiesstory.presentation.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unclekostya.scaryrosiesstory.data.local.entity.ChoiceEntity
import com.unclekostya.scaryrosiesstory.data.local.entity.MessageEntity
import com.unclekostya.scaryrosiesstory.data.local.entity.UserProgressEntity
import com.unclekostya.scaryrosiesstory.data.repository.StoryRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ChatUiState(
    val messages: List<MessageEntity> = emptyList(),
    val choices: List<ChoiceEntity> = emptyList(),
    val currentStoryId: Int? = null,
    val isLoading: Boolean = false,
    val storyTitle: String? = null,
    val typingMessage: String? = null,
    val pendingPlayerMessage: MessageEntity? = null,
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

                val storyTitle = repository.getAllStories()
                    .firstOrNull { it.id == storyId }
                    ?.title ?: "Chat"

                val progress = repository.getUserProgress(storyId)
                val start = when (val localId = progress?.currentMessageDbId) {
                    null -> repository.getMessageByLocalId(storyId, 1)
                    else -> repository.getMessageByLocalId(storyId, localId)
                } ?: return@launch

                _uiState.value = ChatUiState(
                    currentStoryId = storyId,
                    storyTitle = storyTitle,
                    isLoading = false
                )

                advanceStoryFrom(storyId, start)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }

    fun sendPendingPlayerMessage() {
        viewModelScope.launch {
            val s = _uiState.value
            val msg = s.pendingPlayerMessage ?: return@launch
            val id = s.currentStoryId ?: return@launch
            val updated = s.messages + msg

            _uiState.value = s.copy(
                messages = updated,
                pendingPlayerMessage = null,
                choices = emptyList()
            )

            repository.saveProgress(UserProgressEntity(id, msg.localId, null))

            msg.nextMessageLocalId?.let { nextId ->
                repository.getMessageByLocalId(id, nextId)?.let { nextMsg ->
                    advanceStoryFrom(id, nextMsg, updated.toMutableList())
                }
            }
        }
    }

    fun onChoiceSelected(choice: ChoiceEntity) {
        viewModelScope.launch {
            val s = _uiState.value
            val id = s.currentStoryId ?: return@launch

            repository.saveProgress(UserProgressEntity(id, choice.nextMessageLocalId, choice.id))

            choice.nextMessageLocalId?.let { nextId ->
                repository.getMessageByLocalId(id, nextId)?.let { nextMsg ->
                    advanceStoryFrom(id, nextMsg)
                }
            }
        }
    }

    private suspend fun advanceStoryFrom(
        storyId: Int,
        startMessage: MessageEntity,
        shownMessages: MutableList<MessageEntity> = _uiState.value.messages.toMutableList()
    ) {
        var current = startMessage
        var choices: List<ChoiceEntity> = emptyList()
        var pendingPlayer: MessageEntity? = null

        while (true) {
            if (current.sender != "player") {
                simulateTyping(current, shownMessages)

                val possibleChoices = repository.getChoicesForMessageLocalId(storyId, current.localId)
                if (possibleChoices.isNotEmpty()) {
                    choices = possibleChoices
                    break
                }

            } else {
                pendingPlayer = current
                break
            }

            val nextLocalId = current.nextMessageLocalId ?: break
            current = repository.getMessageByLocalId(storyId, nextLocalId) ?: break
        }

        _uiState.value = _uiState.value.copy(
            messages = shownMessages,
            choices = choices,
            pendingPlayerMessage = pendingPlayer,
            typingMessage = null,
            isLoading = false
        )
    }

    private suspend fun simulateTyping(
        message: MessageEntity,
        shown: MutableList<MessageEntity>
    ) {
        _uiState.value = _uiState.value.copy(
            messages = shown,
            typingMessage = "${message.sender} печатает"
        )
        repeat(3) {
            delay(400)
            val dots = ".".repeat((it % 3) + 1)
            _uiState.value = _uiState.value.copy(
                typingMessage = "${message.sender} печатает$dots"
            )
        }
        delay(200)
        shown += message
        _uiState.value = _uiState.value.copy(messages = shown, typingMessage = null)
    }

    fun resetStory(storyId: Int) {
        viewModelScope.launch {
            repository.deleteUserProgress(storyId)
            _uiState.value = ChatUiState()
        }
    }
}
