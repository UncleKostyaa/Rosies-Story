package com.unclekostya.scaryrosiesstory.presentation.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.TypeConverter
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
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

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromMessageList(messages: List<MessageEntity>?): String {
        return gson.toJson(messages)
    }

    @TypeConverter
    fun toMessageList(data: String?): List<MessageEntity> {
        if (data.isNullOrEmpty()) return emptyList()
        val type = object : TypeToken<List<MessageEntity>>() {}.type
        return gson.fromJson(data, type)
    }
}

class StoryViewModel(
    private val repository: StoryRepository
) : ViewModel() {

    private val gson = Gson()
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
                val shownMessages = progress?.let {
                    gson.fromJson(it.seenMessagesJson, Array<MessageEntity>::class.java).toMutableList()
                } ?: mutableListOf()

                val lastMessage = shownMessages.lastOrNull()
                    ?: repository.getMessageByLocalId(storyId, 1)

                _uiState.value = ChatUiState(
                    currentStoryId = storyId,
                    storyTitle = storyTitle,
                    messages = shownMessages,
                    isLoading = false
                )

                if (lastMessage != null)
                    advanceStoryFrom(storyId, lastMessage, shownMessages)

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

            saveProgress(id, updated, null)

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

            val updatedMessages = s.messages.toMutableList()
            saveProgress(id, updatedMessages, choice.id)

            choice.nextMessageLocalId?.let { nextId ->
                repository.getMessageByLocalId(id, nextId)?.let { nextMsg ->
                    advanceStoryFrom(id, nextMsg, updatedMessages)
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

        saveProgress(storyId, shownMessages, null)
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

    private suspend fun saveProgress(storyId: Int, messages: List<MessageEntity>, choiceId: Int?) {
        val json = gson.toJson(messages)
        val progress = UserProgressEntity(storyId, json, choiceId)
        repository.saveProgress(progress)
    }

    fun resetStory(storyId: Int) {
        viewModelScope.launch {
            repository.deleteUserProgress(storyId)
            _uiState.value = ChatUiState()
        }
    }
}