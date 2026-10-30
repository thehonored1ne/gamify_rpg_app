package com.example.liferpg.domain.quest

import com.example.liferpg.domain.model.DeadlineType
import com.example.liferpg.domain.model.QuestDraft
import com.example.liferpg.domain.model.QuestViolation
import com.example.liferpg.domain.model.QuestViolation.DueAtInPast
import com.example.liferpg.domain.model.QuestViolation.DueAtMissing
import com.example.liferpg.domain.model.QuestViolation.DurationMissing
import com.example.liferpg.domain.model.QuestViolation.DurationOutOfRange
import com.example.liferpg.domain.model.QuestViolation.GoldOutOfRange
import com.example.liferpg.domain.model.QuestViolation.PenaltyAmountOutOfRange
import com.example.liferpg.domain.model.QuestViolation.ReminderNotBeforeDeadline
import com.example.liferpg.domain.model.QuestViolation.ReminderTooSoon
import com.example.liferpg.domain.model.QuestViolation.TooManySubtasks
import com.example.liferpg.domain.model.QuestViolation.XpOutOfRange
import com.example.liferpg.domain.model.QuestViolation.DescriptionTooLong
import com.example.liferpg.domain.model.QuestViolation.SubtaskBlank
import com.example.liferpg.domain.model.QuestViolation.SubtaskTooLong
import com.example.liferpg.domain.model.QuestViolation.TitleBlank
import com.example.liferpg.domain.model.QuestViolation.TitleTooLong
import com.example.liferpg.domain.model.QuestViolation.UnusedDeadlineFields
import com.example.liferpg.domain.penalty.PenaltyRules
import com.example.liferpg.domain.progression.RewardRules

/** Field limits from `specs/003-quest-system.md`. */
object QuestValidator {
  const val MAX_TITLE_LENGTH = 80
  const val MAX_DESCRIPTION_LENGTH = 500
  const val MAX_SUBTASKS = 20
  const val MAX_SUBTASK_LENGTH = 80
  const val MIN_DURATION_MINUTES = 1
  const val MAX_DURATION_MINUTES = 10_080
  const val MIN_REMINDER_LEAD_MS = 60_000L

  fun validate(draft: QuestDraft, nowMillis: Long): List<QuestViolation> {
    val clean = draft.normalized()
    val violations = mutableListOf<QuestViolation>()

    when {
      clean.title.isBlank() -> violations += TitleBlank
      clean.title.length > MAX_TITLE_LENGTH -> violations += TitleTooLong
    }

    if (clean.description.length > MAX_DESCRIPTION_LENGTH) violations += DescriptionTooLong

    if (!RewardRules.isValidXp(clean.difficulty, clean.xpReward)) violations += XpOutOfRange
    if (!RewardRules.isValidGold(clean.difficulty, clean.goldReward)) violations += GoldOutOfRange

    when (clean.deadlineType) {
      DeadlineType.None -> {
        if (clean.dueAt != null || clean.durationMinutes != null) violations += UnusedDeadlineFields
      }
      DeadlineType.Date -> {
        val dueAt = clean.dueAt
        if (dueAt == null) {
          violations += DueAtMissing
        } else if (dueAt <= nowMillis) {
          violations += DueAtInPast
        }
        if (clean.durationMinutes != null) violations += UnusedDeadlineFields
      }
      DeadlineType.Timer -> {
        val duration = clean.durationMinutes
        if (duration == null) {
          violations += DurationMissing
        } else if (duration !in MIN_DURATION_MINUTES..MAX_DURATION_MINUTES) {
          violations += DurationOutOfRange
        }
        if (clean.dueAt != null) violations += UnusedDeadlineFields
      }
    }

    clean.reminderAt?.let { reminder ->
      if (reminder < nowMillis + MIN_REMINDER_LEAD_MS) violations += ReminderTooSoon
      if (clean.deadlineType == DeadlineType.Date && clean.dueAt != null && reminder >= clean.dueAt) {
        violations += ReminderNotBeforeDeadline
      }
    }

    if (!PenaltyRules.isValid(clean.penaltyType, clean.penaltyAmount, clean.xpReward, clean.goldReward)) {
      violations += PenaltyAmountOutOfRange
    }

    if (clean.subtasks.size > MAX_SUBTASKS) violations += TooManySubtasks
    clean.subtasks.forEach { subtask ->
      when {
        subtask.isBlank() -> violations += SubtaskBlank
        subtask.length > MAX_SUBTASK_LENGTH -> violations += SubtaskTooLong
      }
    }

    return violations.distinct()
  }

  fun isValid(draft: QuestDraft, nowMillis: Long): Boolean = validate(draft, nowMillis).isEmpty()
}
