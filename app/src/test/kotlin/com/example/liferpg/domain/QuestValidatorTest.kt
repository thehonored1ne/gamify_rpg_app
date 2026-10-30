package com.example.liferpg.domain

import com.example.liferpg.domain.model.DeadlineType
import com.example.liferpg.domain.model.Difficulty
import com.example.liferpg.domain.model.PenaltyType
import com.example.liferpg.domain.model.QuestDraft
import com.example.liferpg.domain.model.QuestViolation
import com.example.liferpg.domain.quest.QuestValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuestValidatorTest {
  private val now = 1_800_000_000_000L
  private val hour = 60L * 60L * 1_000L

  private fun validDraft() =
    QuestDraft(
      title = "Write the report",
      description = "Two sections",
      difficulty = Difficulty.Uncommon,
      xpReward = 10,
      goldReward = 5,
    )

  @Test
  fun `a plain quest is valid`() {
    assertTrue(QuestValidator.isValid(validDraft(), now))
  }

  @Test
  fun `title rules`() {
    assertTrue(QuestViolation.TitleBlank in QuestValidator.validate(validDraft().copy(title = "   "), now))
    assertTrue(
      QuestViolation.TitleTooLong in
        QuestValidator.validate(validDraft().copy(title = "x".repeat(81)), now),
    )
    assertTrue(QuestValidator.isValid(validDraft().copy(title = "x".repeat(80)), now))
  }

  @Test
  fun `rewards outside the caps are rejected`() {
    assertTrue(
      QuestViolation.XpOutOfRange in
        QuestValidator.validate(validDraft().copy(xpReward = 21), now),
    )
    assertTrue(
      QuestViolation.GoldOutOfRange in
        QuestValidator.validate(validDraft().copy(goldReward = 11), now),
    )
  }

  @Test
  fun `date deadline must be in the future`() {
    assertTrue(
      QuestViolation.DueAtMissing in
        QuestValidator.validate(validDraft().copy(deadlineType = DeadlineType.Date), now),
    )
    assertTrue(
      QuestViolation.DueAtInPast in
        QuestValidator.validate(
          validDraft().copy(deadlineType = DeadlineType.Date, dueAt = now - hour),
          now,
        ),
    )
    assertTrue(
      QuestValidator.isValid(
        validDraft().copy(deadlineType = DeadlineType.Date, dueAt = now + 24 * hour),
        now,
      ),
    )
  }

  @Test
  fun `timer duration range is enforced`() {
    assertTrue(
      QuestViolation.DurationMissing in
        QuestValidator.validate(validDraft().copy(deadlineType = DeadlineType.Timer), now),
    )
    assertTrue(
      QuestViolation.DurationOutOfRange in
        QuestValidator.validate(
          validDraft().copy(deadlineType = DeadlineType.Timer, durationMinutes = 0),
          now,
        ),
    )
    assertTrue(
      QuestViolation.DurationOutOfRange in
        QuestValidator.validate(
          validDraft().copy(deadlineType = DeadlineType.Timer, durationMinutes = 10_081),
          now,
        ),
    )
    assertTrue(
      QuestValidator.isValid(
        validDraft().copy(deadlineType = DeadlineType.Timer, durationMinutes = 10_080),
        now,
      ),
    )
  }

  @Test
  fun `unused deadline fields are rejected`() {
    assertTrue(
      QuestViolation.UnusedDeadlineFields in
        QuestValidator.validate(validDraft().copy(dueAt = now + hour), now),
    )
  }

  @Test
  fun `reminder must come before the deadline and after one minute`() {
    val base = validDraft().copy(deadlineType = DeadlineType.Date, dueAt = now + 5 * hour)
    assertTrue(
      QuestViolation.ReminderTooSoon in
        QuestValidator.validate(base.copy(reminderAt = now + 30_000L), now),
    )
    assertTrue(
      QuestViolation.ReminderNotBeforeDeadline in
        QuestValidator.validate(base.copy(reminderAt = base.dueAt), now),
    )
    assertTrue(QuestValidator.isValid(base.copy(reminderAt = now + 2 * hour), now))
  }

  @Test
  fun `penalty above the cap is rejected`() {
    assertTrue(
      QuestViolation.PenaltyAmountOutOfRange in
        QuestValidator.validate(
          validDraft().copy(penaltyType = PenaltyType.Gold, penaltyAmount = 3),
          now,
        ),
    )
    assertTrue(
      QuestValidator.isValid(
        validDraft().copy(penaltyType = PenaltyType.Gold, penaltyAmount = 2),
        now,
      ),
    )
  }

  @Test
  fun `subtasks are limited and non blank`() {
    val tooMany = (1..21).map { "subtask $it" }
    assertTrue(
      QuestViolation.TooManySubtasks in
        QuestValidator.validate(validDraft().copy(subtasks = tooMany), now),
    )
    assertTrue(
      QuestViolation.SubtaskBlank in
        QuestValidator.validate(validDraft().copy(subtasks = listOf("ok", " ")), now),
    )
    assertTrue(
      QuestViolation.SubtaskTooLong in
        QuestValidator.validate(validDraft().copy(subtasks = listOf("x".repeat(81))), now),
    )
  }

  @Test
  fun `violations are not duplicated`() {
    val violations =
      QuestValidator.validate(validDraft().copy(title = "", subtasks = listOf("", "")), now)
    assertEquals(violations.size, violations.distinct().size)
  }
}
