package com.example.liferpg.ui.quests

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liferpg.R
import com.example.liferpg.domain.model.Quest
import com.example.liferpg.domain.repository.QuestRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface QuestDetailUiState {
  data object Loading : QuestDetailUiState

  data class Success(val quest: Quest) : QuestDetailUiState

  data object NotFound : QuestDetailUiState

  data class Error(@StringRes val messageRes: Int) : QuestDetailUiState
}

sealed interface QuestDetailMessage {
  data class Completed(val xp: Int, val gold: Int) : QuestDetailMessage

  data object Cancelled : QuestDetailMessage

  data object Deleted : QuestDetailMessage

  data object Failed : QuestDetailMessage
}

class QuestDetailViewModel(
  private val questId: Long,
  private val repository: QuestRepository,
) : ViewModel() {
  private val _messages = Channel<QuestDetailMessage>(Channel.BUFFERED)
  val messages: Flow<QuestDetailMessage> = _messages.receiveAsFlow()

  val state: StateFlow<QuestDetailUiState> =
    repository
      .observeQuest(questId)
      .map { quest ->
        if (quest == null) QuestDetailUiState.NotFound else QuestDetailUiState.Success(quest)
      }
      .catch { emit(QuestDetailUiState.Error(R.string.error_generic)) }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = QuestDetailUiState.Loading,
      )

  fun complete() {
    viewModelScope.launch {
      repository.complete(questId).fold(
        onSuccess = { _messages.send(QuestDetailMessage.Completed(it.xpAwarded, it.goldAwarded)) },
        onFailure = { _messages.send(QuestDetailMessage.Failed) },
      )
    }
  }

  fun cancel() {
    viewModelScope.launch {
      repository.cancel(questId)
      _messages.send(QuestDetailMessage.Cancelled)
    }
  }

  fun delete() {
    viewModelScope.launch {
      repository.delete(questId)
      _messages.send(QuestDetailMessage.Deleted)
    }
  }

  fun startTimer() {
    viewModelScope.launch { repository.startTimer(questId) }
  }
}
