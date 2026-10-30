package com.example.liferpg.domain.progression

/**
 * XP curve from `specs/004-progression-economy.md`.
 *
 * `xpToNext(n) = 50 + 25 * (n - 1)`, level cap 100. Level is always derived from total XP; nothing
 * stores it.
 */
object XpCurve {
  const val MAX_LEVEL: Int = 100

  /** XP needed to go from [level] to `level + 1`. Undefined at the cap, where it returns 0. */
  fun xpToNext(level: Int): Long {
    require(level >= 1) { "level must be >= 1, was $level" }
    if (level >= MAX_LEVEL) return 0
    return 50L + 25L * (level - 1)
  }

  /** Total XP needed to reach [level]. Level 1 starts at 0. */
  fun cumulativeXpForLevel(level: Int): Long {
    require(level in 1..MAX_LEVEL) { "level must be in 1..$MAX_LEVEL, was $level" }
    if (level == 1) return 0
    val n = (level - 1).toLong()
    return 50L * n + 25L * n * (n - 1) / 2
  }

  fun levelFor(totalXp: Long): Int {
    if (totalXp <= 0L) return 1
    var level = 1
    while (level < MAX_LEVEL && totalXp >= cumulativeXpForLevel(level + 1)) {
      level++
    }
    return level
  }

  /** XP floor of the level that [totalXp] falls in; XP penalties can never go below it. */
  fun levelFloor(totalXp: Long): Long = cumulativeXpForLevel(levelFor(totalXp))

  fun progress(totalXp: Long): LevelProgress {
    val level = levelFor(totalXp)
    if (level >= MAX_LEVEL) {
      return LevelProgress(level = MAX_LEVEL, xpIntoLevel = 0, xpForNext = 0, isMaxLevel = true)
    }
    val floor = cumulativeXpForLevel(level)
    return LevelProgress(
      level = level,
      xpIntoLevel = (totalXp - floor).toInt(),
      xpForNext = xpToNext(level).toInt(),
      isMaxLevel = false,
    )
  }
}

data class LevelProgress(
  val level: Int,
  val xpIntoLevel: Int,
  val xpForNext: Int,
  val isMaxLevel: Boolean,
) {
  val fraction: Float
    get() = if (isMaxLevel || xpForNext <= 0) 1f else (xpIntoLevel.toFloat() / xpForNext).coerceIn(0f, 1f)
}
