package com.example.liferpg.ui.quests

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liferpg.R
import com.example.liferpg.core.time.TimeProvider
import com.example.liferpg.domain.model.Category
import com.example.liferpg.domain.model.DeadlineType
import com.example.liferpg.domain.model.Difficulty
import com.example.liferpg.domain.model.PenaltyType
import com.example.liferpg.domain.model.Quest
import com.example.liferpg.domain.model.QuestDraft
import com.example.liferpg.domain.model.QuestValidationException
import com.example.liferpg.domain.model.QuestViolation
import com.example.liferpg.domain.quest.QuestValidator
import com.example.liferpg.domain.repository.QuestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SubtaskInput(val title: String = "", val isDone: Boolean = false)

data class QuestEditUiState(
  val loading: Boolean = true,
  val saving: Boolean = false,
  val isNew: Boolean = true,
  val title: String = "",
  val description: String = "",
  val difficulty: Difficulty = Difficulty.Common,
  val category: Category = Category.Personal,
  val xpText: String = Difficulty.Common.xpBaseline.toString(),
  val goldText: String = Difficulty.Common.goldBaseline.toString(),
  val deadlineType: DeadlineType = DeadlineType.None,
  val dueAt: Long? = null,
  val durationText: String = "",
  val penaltyType: PenaltyType = PenaltyType.None,
  val penaltyAmountText: String = "0",
  val subtasks: List<SubtaskInput> = emptyList(),
  val violations: List<QuestViolation> = emptyList(),
  @StringRes val errorMessage: Int? = null,
  val saved: Boolean = false,
) {
  fun toDraft(): QuestDraft =
    QuestDraft(
      title = title,
      description = description,
      difficulty = difficulty,
      category = category,
      xpReward = xpText.toIntOrNull() ?: 0,
      goldReward = goldText.toIntOrNull() ?: 0,
      deadlineType = deadlineType,
      dueAt = if (deadlineType == DeadlineType.Date) dueAt else null,
      durationMinutes = if (deadlineType == DeadlineType.Timer) durationText.toIntOrNull() else null,
      reminderAt = null,
      penaltyType = penaltyType,
      penaltyAmount = penaltyAmountText.toIntOrNull() ?: 0,
      subtasks = subtasks.map { it.title }.filter { it.isNotBlank() },
    )
}

class QuestEditViewModel(
  private val questId: Long?,
  private val repository: QuestRepository,
  private val timeProvider: TimeProvider,
) : ViewModel() {
  private val _state = MutableStateFlow(QuestEditUiState(isNew = questId == null))
  val state: StateFlow<QuestEditUiState> = _state.asStateFlow()

  init {
    if (questId == null) {
      _state.update { it.copy(loading = false) }
    } else {
      viewModelScope.launch { load(questId) }
    }
  }

  private suspend fun load(id: Long) {
    val quest = repository.getQuest(id)
    if (quest == null) {
      _state.update { it.copy(loading = false, errorMessage = R.string.error_quest_not_found) }
      return
    }
    _state.update { it.copy(loading = false, isNew = false).fromQuest(quest) }
  }

  fun setTitle(value: String) = _state.update { it.copy(title = value) }

  fun setDescription(value: String) = _state.update { it.copy(description = value) }

  fun setDifficulty(value: Difficulty) =
    _state.update {
      it.copy(
        difficulty = value,
        xpText = value.xpBaseline.toString(),
        goldText = value.goldBaseline.toString(),
      )
    }

  fun setCategory(value: Category) = _state.update { it.copy(category = value) }

  fun setXp(value: String) = _state.update { it.copy(xpText = value.filter(Char::isDigit)) }

  fun setGold(value: String) = _state.update { it.copy(goldText = value.filter(Char::isDigit)) }

  fun setDeadlineType(value: DeadlineType) =
    _state.update {
      it.copy(
        deadlineType = value,
        dueAt = if (value == DeadlineType.Date) it.dueAt else null,
        durationText = if (value == DeadlineType.Timer) it.durationText else "",
      )
    }

  fun setDueAt(epochMillis: Long) = _state.update { it.copy(dueAt = epochMillis) }

  fun setDuration(value: String) =
    _state.update { it.copy(durationText = value.filter(Char::isDigit)) }

  fun setPenaltyType(value: PenaltyType) =
    _state.update { it.copy(penaltyType = value, penaltyAmountText = if (value == PenaltyType.None) "0" else it.penaltyAmountText) }

  fun setPenaltyAmount(value: String) =
    _state.update { it.copy(penaltyAmountText = value.filter(Char::isDigit)) }

  fun addSubtask() =
    _state.update {
      if (it.subtasks.size >= QuestValidator.MAX_SUBTASKS) it
      else it.copy(subtasks = it.subtasks + SubtaskInput())
    }

  fun setSubtaskTitle(index: Int, value: String) =
    _state.update { current ->
      if (index !in current.subtasks.indices) current
      else current.copy(subtasks = current.subtasks.toMutableList().also { it[index] = it[index].copy(title = value) })
    }

  fun removeSubtask(index: Int) =
    _state.update { current ->
      if (index !in current.subtasks.indices) current
      else current.copy(subtasks = current.subtasks.filterIndexed { i, _ -> i != index })
    }

  fun save() {
    val current = _state.value
    if (current.saving) return
    _state.update { it.copy(saving = true, violations = emptyList(), errorMessage = null) }

    viewModelScope.launch {
      val result =
        if (questId == null) {
          repository.create(current.toDraft()).map { }
        } else {
          repository.update(questId, current.toDraft())
        }

      result.fold(
        onSuccess = { _state.update { it.copy(saving = false, saved = true) } },
        onFailure = { error ->
          if (error is QuestValidationException) {
            _state.update { it.copy(saving = false, violations = error.violations) }
          } else {
            _state.update { it.copy(saving = false, errorMessage = R.string.error_generic) }
          }
        },
      )
    }
  }

  private fun QuestEditUiState.fromQuest(quest: Quest): QuestEditUiState =
    copy(
      title = quest.title,
      description = quest.description,
      difficulty = quest.difficulty,
      category = quest.category,
      xpText = quest.xpReward.toString(),
      goldText = quest.goldReward.toString(),
      deadlineType = quest.deadlineType,
      dueAt = quest.dueAt,
      durationText = quest.durationMinutes?.toString() ?: "",
      penaltyType = quest.penaltyType,
      penaltyAmountText = quest.penaltyAmount.toString(),
      subtasks = quest.subtasks.sortedBy { it.position }.map { SubtaskInput(it.title, it.isDone) },
    )
}
