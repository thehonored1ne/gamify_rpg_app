package com.example.liferpg.domain.penalty

import com.example.liferpg.domain.model.PenaltyType
import com.example.liferpg.domain.progression.XpCurve

/**
 * Penalty rules from `specs/005-penalty-policy.md`.
 *
 * Penalties are in-app only, optional, and applied at most once. They never remove owned items and
 * never demote the user below the current level's XP floor.
 */
object PenaltyRules {
  /** Maximum gold penalty for a quest: half of its gold reward, rounded down. */
  fun maxGold(questGoldReward: Int): Int = questGoldReward / 2

  /** Maximum XP penalty for a quest: half of its XP reward, rounded down. */
  fun maxXp(questXpReward: Int): Int = questXpReward / 2

  fun isValid(
    type: PenaltyType,
    amount: Int,
    questXpReward: Int,
    questGoldReward: Int,
  ): Boolean =
    when (type) {
      PenaltyType.None, PenaltyType.Streak, PenaltyType.Mark -> amount == 0
      PenaltyType.Gold -> amount in 0..maxGold(questGoldReward)
      PenaltyType.Xp -> amount in 0..maxXp(questXpReward)
    }

  /** Gold actually removed, clamped so the balance never goes negative. */
  fun goldDeduction(amount: Int, goldBalance: Long): Long {
    if (amount <= 0 || goldBalance <= 0L) return 0L
    return minOf(amount.toLong(), goldBalance)
  }

  /**
   * XP actually removed, clamped so total XP never falls below the floor of the current level.
   * A penalty can slow progress but never demotes.
   */
  fun xpDeduction(amount: Int, totalXp: Long): Long {
    if (amount <= 0 || totalXp <= 0L) return 0L
    val floor = XpCurve.levelFloor(totalXp)
    val removable = (totalXp - floor).coerceAtLeast(0L)
    return minOf(amount.toLong(), removable)
  }

  /** Streak penalties reset to zero immediately and consume no grace day. */
  fun resetsStreak(type: PenaltyType): Boolean = type == PenaltyType.Streak

  fun incrementsFailedCount(type: PenaltyType): Boolean = type == PenaltyType.Mark
}
