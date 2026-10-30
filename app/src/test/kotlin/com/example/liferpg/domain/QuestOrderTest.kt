package com.example.liferpg.domain

import com.example.liferpg.domain.model.DeadlineType
import com.example.liferpg.domain.model.Difficulty
import com.example.liferpg.domain.model.Quest
import com.example.liferpg.domain.model.QuestStatus
import com.example.liferpg.domain.quest.QuestOrder
import org.junit.Assert.assertEquals
import org.junit.Test

class QuestOrderTest {
  private fun quest(
    id: Long,
    status: QuestStatus,
    dueAt: Long? = null,
    completedAt: Long? = null,
  ) =
    Quest(
      id = id,
      title = "quest $id",
      description = "",
      difficulty = Difficulty.Common,
      category = com.example.liferpg.domain.model.Category.Personal,
      xpReward = 5,
      goldReward = 2,
      deadlineType = if (dueAt == null) DeadlineType.None else DeadlineType.Date,
      dueAt = dueAt,
      durationMinutes = null,
      timerStartedAt = null,
      reminderAt = null,
      penaltyType = com.example.liferpg.domain.model.PenaltyType.None,
      penaltyAmount = 0,
      status = status,
      source = com.example.liferpg.domain.model.QuestSource.User,
      systemQuestId = null,
      createdAt = id,
      updatedAt = id,
      completedAt = completedAt,
      failedAt = null,
    )

  @Test
  fun `active quests come first ordered by deadline`() {
    val late = quest(1, QuestStatus.Active, dueAt = 5_000)
    val early = quest(2, QuestStatus.Active, dueAt = 1_000)
    val none = quest(3, QuestStatus.Active)
    val completed = quest(4, QuestStatus.Completed, completedAt = 9_000)

    val ordered = QuestOrder.sorted(listOf(late, completed, none, early))
    assertEquals(listOf(2L, 1L, 3L, 4L), ordered.map { it.id })
  }

  @Test
  fun `completed quests sort by most recent first`() {
    val older = quest(1, QuestStatus.Completed, completedAt = 1_000)
    val newer = quest(2, QuestStatus.Completed, completedAt = 5_000)

    val ordered = QuestOrder.sorted(listOf(older, newer))
    assertEquals(listOf(2L, 1L), ordered.map { it.id })
  }
}
