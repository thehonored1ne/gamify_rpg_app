package com.example.liferpg.domain.progression

import com.example.liferpg.domain.model.Difficulty

/**
 * Reward bounds for a quest, derived from its difficulty.
 *
 * Allowed range is `ceil(0.5 * baseline)` to `2 * baseline` for XP, and `0` to `2 * baseline` for
 * gold. XP never drops below 1.
 */
object RewardRules {
  fun minXp(difficulty: Difficulty): Int = maxOf(1, ceilHalf(difficulty.xpBaseline))

  fun maxXp(difficulty: Difficulty): Int = 2 * difficulty.xpBaseline

  fun minGold(difficulty: Difficulty): Int = 0

  fun maxGold(difficulty: Difficulty): Int = 2 * difficulty.goldBaseline

  fun isValidXp(difficulty: Difficulty, xp: Int): Boolean = xp in minXp(difficulty)..maxXp(difficulty)

  fun isValidGold(difficulty: Difficulty, gold: Int): Boolean = gold in minGold(difficulty)..maxGold(difficulty)

  fun clampXp(difficulty: Difficulty, xp: Int): Int = xp.coerceIn(minXp(difficulty), maxXp(difficulty))

  fun clampGold(difficulty: Difficulty, gold: Int): Int = gold.coerceIn(minGold(difficulty), maxGold(difficulty))

  private fun ceilHalf(value: Int): Int = (value + 1) / 2
}
