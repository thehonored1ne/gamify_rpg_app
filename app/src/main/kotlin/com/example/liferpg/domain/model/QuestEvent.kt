package com.example.liferpg.domain.model

/** One append-only ledger row. Balances are always derived from these. */
data class QuestEvent(
  val id: Long,
  val questId: Long?,
  val questTitle: String?,
  val type: QuestEventType,
  val xpDelta: Int,
  val goldDelta: Int,
  val occurredAt: Long,
  val note: String? = null,
)

/** Result of a completion, used by the UI to show rewards and a level-up banner. */
data class Completion(
  val questId: Long,
  val xpAwarded: Int,
  val goldAwarded: Int,
  val levelBefore: Int,
  val levelAfter: Int,
  val completedAt: Long,
) {
  val leveledUp: Boolean
    get() = levelAfter > levelBefore
}
