package com.example.liferpg.domain.model

data class Subtask(
  val id: Long,
  val questId: Long,
  val title: String,
  val isDone: Boolean,
  val position: Int,
)

data class Quest(
  val id: Long,
  val title: String,
  val description: String,
  val difficulty: Difficulty,
  val category: Category,
  val xpReward: Int,
  val goldReward: Int,
  val deadlineType: DeadlineType,
  val dueAt: Long?,
  val durationMinutes: Int?,
  val timerStartedAt: Long?,
  val reminderAt: Long?,
  val penaltyType: PenaltyType,
  val penaltyAmount: Int,
  val status: QuestStatus,
  val source: QuestSource,
  val systemQuestId: String?,
  val createdAt: Long,
  val updatedAt: Long,
  val completedAt: Long?,
  val failedAt: Long?,
  val subtasks: List<Subtask> = emptyList(),
) {
  val isTimerRunning: Boolean
    get() = deadlineType == DeadlineType.Timer && timerStartedAt != null

  /** Absolute deadline in epoch millis, or null when the quest has no deadline. */
  fun deadlineAtMillis(): Long? =
    when (deadlineType) {
      DeadlineType.None -> null
      DeadlineType.Date -> dueAt
      DeadlineType.Timer ->
        timerStartedAt?.plus((durationMinutes ?: 0).toLong() * 60_000L)
    }

  fun isExpired(nowMillis: Long): Boolean {
    val deadline = deadlineAtMillis() ?: return false
    return status == QuestStatus.Active && nowMillis >= deadline
  }
}
