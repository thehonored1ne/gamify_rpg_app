package com.example.liferpg.ui.quests

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liferpg.R
import com.example.liferpg.domain.model.Quest
import com.example.liferpg.domain.repository.Progress
import com.example.liferpg.domain.repository.ProgressRepository
import com.example.liferpg.domain.repository.QuestRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

sealed interface QuestsUiState {
  data object Loading : QuestsUiState

  data class Success(val quests: List<Quest>, val progress: Progress) : QuestsUiState

  data class Error(@StringRes val messageRes: Int) : QuestsUiState
}

class QuestsViewModel(
  questRepository: QuestRepository,
  progressRepository: ProgressRepository,
) : ViewModel() {
  val state: StateFlow<QuestsUiState> =
    combine(questRepository.observeQuests(), progressRepository.observeProgress()) { quests, progress ->
        QuestsUiState.Success(quests, progress) as QuestsUiState
      }
      .catch { emit(QuestsUiState.Error(R.string.error_generic)) }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = QuestsUiState.Loading,
      )
}
