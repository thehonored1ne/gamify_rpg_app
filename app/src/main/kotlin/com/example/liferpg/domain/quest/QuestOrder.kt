package com.example.liferpg.domain.quest

import com.example.liferpg.domain.model.Quest
import com.example.liferpg.domain.model.QuestStatus

/**
 * List ordering from `specs/003` R11: Active first by nearest deadline (no deadline last), then
 * Completed by most recent, then the remaining statuses.
 */
object QuestOrder {
  private fun statusRank(status: QuestStatus): Int =
    when (status) {
      QuestStatus.Active -> 0
      QuestStatus.Completed -> 1
      QuestStatus.Failed -> 2
      QuestStatus.Cancelled -> 3
    }

  private fun deadlineRank(quest: Quest): Long = quest.deadlineAtMillis() ?: Long.MAX_VALUE

  val comparator: Comparator<Quest> =
    Comparator { left, right ->
      val byStatus = statusRank(left.status).compareTo(statusRank(right.status))
      if (byStatus != 0) return@Comparator byStatus

      when (left.status) {
        QuestStatus.Active -> {
          val byDeadline = deadlineRank(left).compareTo(deadlineRank(right))
          if (byDeadline != 0) byDeadline else right.createdAt.compareTo(left.createdAt)
        }
        QuestStatus.Completed -> {
          val leftAt = left.completedAt ?: 0L
          val rightAt = right.completedAt ?: 0L
          rightAt.compareTo(leftAt)
        }
        else -> right.updatedAt.compareTo(left.updatedAt)
      }
    }

  fun sorted(quests: List<Quest>): List<Quest> = quests.sortedWith(comparator)
}
