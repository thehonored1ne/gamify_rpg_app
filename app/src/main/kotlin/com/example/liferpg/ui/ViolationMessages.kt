package com.example.liferpg.ui

import androidx.annotation.StringRes
import com.example.liferpg.R
import com.example.liferpg.domain.model.QuestViolation

@StringRes
fun QuestViolation.messageRes(): Int =
  when (this) {
    QuestViolation.TitleBlank -> R.string.violation_title_blank
    QuestViolation.TitleTooLong -> R.string.violation_title_long
    QuestViolation.DescriptionTooLong -> R.string.violation_description_long
    QuestViolation.XpOutOfRange -> R.string.violation_xp_range
    QuestViolation.GoldOutOfRange -> R.string.violation_gold_range
    QuestViolation.DueAtMissing -> R.string.violation_due_missing
    QuestViolation.DueAtInPast -> R.string.violation_due_past
    QuestViolation.DurationMissing -> R.string.violation_duration_missing
    QuestViolation.DurationOutOfRange -> R.string.violation_duration_range
    QuestViolation.UnusedDeadlineFields -> R.string.violation_unused_deadline
    QuestViolation.ReminderTooSoon -> R.string.violation_reminder_soon
    QuestViolation.ReminderNotBeforeDeadline -> R.string.violation_reminder_deadline
    QuestViolation.PenaltyAmountOutOfRange -> R.string.violation_penalty_range
    QuestViolation.TooManySubtasks -> R.string.violation_too_many_subtasks
    QuestViolation.SubtaskBlank -> R.string.violation_subtask_blank
    QuestViolation.SubtaskTooLong -> R.string.violation_subtask_long
  }
