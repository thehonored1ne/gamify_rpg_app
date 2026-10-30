package com.example.liferpg.domain.streak

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

/**
 * Daily and weekly streak math from `specs/007-achievements-streaks.md`.
 *
 * Input is always a set of local dates that contain at least one completion event, plus the dates
 * that consumed a grace day. Nothing here stores a counter.
 */
object StreakCalculator {
  const val GRACE_WINDOW_DAYS: Long = 6
  const val WEEKLY_MINIMUM_DAYS: Int = 5

  /**
   * Consecutive days ending today (or yesterday, while today is still open) with a completion.
   *
   * Grace days keep the chain alive without adding to the count.
   */
  fun currentStreak(
    completedDays: Set<LocalDate>,
    graceDays: Set<LocalDate> = emptySet(),
    today: LocalDate,
  ): Int {
    var day = today
    if (day !in completedDays && day !in graceDays) {
      day = day.minusDays(1)
    }

    var streak = 0
    while (true) {
      when {
        day in completedDays -> {
          streak++
          day = day.minusDays(1)
        }
        day in graceDays -> day = day.minusDays(1)
        else -> return streak
      }
    }
  }

  /** A grace day may be consumed at most once per rolling seven days. */
  fun graceAvailable(graceDays: Set<LocalDate>, date: LocalDate): Boolean {
    val windowStart = date.minusDays(GRACE_WINDOW_DAYS)
    return graceDays.none { it in windowStart..date }
  }

  /**
   * Result of evaluating a missed day: either the grace absorbs it, or the streak resets.
   */
  fun onMissedDay(
    completedDays: Set<LocalDate>,
    graceDays: Set<LocalDate>,
    missedDay: LocalDate,
  ): MissedDayOutcome {
    if (missedDay in completedDays) return MissedDayOutcome.StillActive
    return if (graceAvailable(graceDays, missedDay)) {
      MissedDayOutcome.GraceConsumed
    } else {
      MissedDayOutcome.StreakReset
    }
  }

  /**
   * Consecutive weeks with at least [minimumDays] distinct completion days. The current week is
   * still open, so a partial week does not break the chain.
   */
  fun weeklyStreak(
    completedDays: Set<LocalDate>,
    today: LocalDate,
    minimumDays: Int = WEEKLY_MINIMUM_DAYS,
  ): Int {
    if (completedDays.isEmpty()) return 0
    val perWeek = completedDays.groupingBy(::weekStart).eachCount()
    var week = weekStart(today)
    if ((perWeek[week] ?: 0) < minimumDays) week = week.minusWeeks(1)

    var streak = 0
    while ((perWeek[week] ?: 0) >= minimumDays) {
      streak++
      week = week.minusWeeks(1)
    }
    return streak
  }

  fun weekStart(date: LocalDate): LocalDate =
    date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
}

enum class MissedDayOutcome {
  StillActive,
  GraceConsumed,
  StreakReset,
}
