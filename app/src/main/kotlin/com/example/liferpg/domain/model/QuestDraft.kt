package com.example.liferpg.domain.model

/**
 * Everything the user can set when creating or editing a quest.
 *
 * Rewards are validated against the difficulty baselines; the UI may suggest the baseline but the
 * draft always carries the final values.
 */
data class QuestDraft(
  val title: String = "",
  val description: String = "",
  val difficulty: Difficulty = Difficulty.Common,
  val category: Category = Category.Personal,
  val xpReward: Int = Difficulty.Common.xpBaseline,
  val goldReward: Int = Difficulty.Common.goldBaseline,
  val deadlineType: DeadlineType = DeadlineType.None,
  val dueAt: Long? = null,
  val durationMinutes: Int? = null,
  val reminderAt: Long? = null,
  val penaltyType: PenaltyType = PenaltyType.None,
  val penaltyAmount: Int = 0,
  val subtasks: List<String> = emptyList(),
) {
  fun normalized(): QuestDraft =
    copy(
      title = title.trim(),
      description = description.trim(),
      subtasks = subtasks.map { it.trim() },
    )
}
