package com.example.liferpg.domain.model

/** Validation failures, mapped to `strings.xml` in the UI layer. */
enum class QuestViolation {
  TitleBlank,
  TitleTooLong,
  DescriptionTooLong,
  XpOutOfRange,
  GoldOutOfRange,
  DueAtMissing,
  DueAtInPast,
  DurationMissing,
  DurationOutOfRange,
  UnusedDeadlineFields,
  ReminderTooSoon,
  ReminderNotBeforeDeadline,
  PenaltyAmountOutOfRange,
  TooManySubtasks,
  SubtaskBlank,
  SubtaskTooLong,
}

class QuestValidationException(val violations: List<QuestViolation>) :
  IllegalArgumentException("Quest validation failed: $violations")
