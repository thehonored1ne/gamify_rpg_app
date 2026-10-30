package com.example.liferpg.data.db

import com.example.liferpg.domain.model.Category
import com.example.liferpg.domain.model.DeadlineType
import com.example.liferpg.domain.model.Difficulty
import com.example.liferpg.domain.model.PenaltyType
import com.example.liferpg.domain.model.Quest
import com.example.liferpg.domain.model.QuestDraft
import com.example.liferpg.domain.model.QuestEvent
import com.example.liferpg.domain.model.QuestEventType
import com.example.liferpg.domain.model.QuestSource
import com.example.liferpg.domain.model.QuestStatus
import com.example.liferpg.domain.model.Subtask

fun QuestEntity.toDomain(subtasks: List<SubtaskEntity> = emptyList()): Quest =
  Quest(
    id = id,
    title = title,
    description = description,
    difficulty = Difficulty.valueOf(difficulty),
    category = Category.valueOf(category),
    xpReward = xpReward,
    goldReward = goldReward,
    deadlineType = DeadlineType.valueOf(deadlineType),
    dueAt = dueAt,
    durationMinutes = durationMinutes,
    timerStartedAt = timerStartedAt,
    reminderAt = reminderAt,
    penaltyType = PenaltyType.valueOf(penaltyType),
    penaltyAmount = penaltyAmount,
    status = QuestStatus.valueOf(status),
    source = QuestSource.valueOf(source),
    systemQuestId = systemQuestId,
    createdAt = createdAt,
    updatedAt = updatedAt,
    completedAt = completedAt,
    failedAt = failedAt,
    subtasks = subtasks.map { it.toDomain() },
  )

fun SubtaskEntity.toDomain(): Subtask =
  Subtask(id = id, questId = questId, title = title, isDone = isDone, position = position)

fun QuestEventEntity.toDomain(): QuestEvent =
  QuestEvent(
    id = id,
    questId = questId,
    questTitle = questTitle,
    type = QuestEventType.valueOf(type),
    xpDelta = xpDelta,
    goldDelta = goldDelta,
    occurredAt = occurredAt,
    note = note,
  )

fun QuestDraft.toEntity(nowMillis: Long): QuestEntity =
  QuestEntity(
    title = title.trim(),
    description = description.trim(),
    difficulty = difficulty.name,
    category = category.name,
    xpReward = xpReward,
    goldReward = goldReward,
    deadlineType = deadlineType.name,
    dueAt = dueAt,
    durationMinutes = durationMinutes,
    timerStartedAt = null,
    reminderAt = reminderAt,
    penaltyType = penaltyType.name,
    penaltyAmount = penaltyAmount,
    status = QuestStatus.Active.name,
    source = QuestSource.User.name,
    systemQuestId = null,
    createdAt = nowMillis,
    updatedAt = nowMillis,
    completedAt = null,
    failedAt = null,
  )

fun QuestDraft.toSubtaskEntities(questId: Long): List<SubtaskEntity> =
  subtasks.mapIndexed { index, title ->
    SubtaskEntity(questId = questId, title = title.trim(), isDone = false, position = index)
  }
