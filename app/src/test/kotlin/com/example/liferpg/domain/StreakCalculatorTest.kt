package com.example.liferpg.domain

import com.example.liferpg.domain.streak.MissedDayOutcome
import com.example.liferpg.domain.streak.StreakCalculator
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StreakCalculatorTest {
  private val today = LocalDate.of(2026, 10, 30)

  @Test
  fun `empty history has no streak`() {
    assertEquals(0, StreakCalculator.currentStreak(emptySet(), emptySet(), today))
  }

  @Test
  fun `consecutive days count`() {
    val days = (0L..5L).map { today.minusDays(it) }.toSet()
    assertEquals(6, StreakCalculator.currentStreak(days, emptySet(), today))
  }

  @Test
  fun `today still open does not break the streak`() {
    val days = (1L..5L).map { today.minusDays(it) }.toSet()
    assertEquals(5, StreakCalculator.currentStreak(days, emptySet(), today))
  }

  @Test
  fun `one missed day is absorbed by grace`() {
    val completed = setOf(today, today.minusDays(2), today.minusDays(3))
    val grace = setOf(today.minusDays(1))
    assertEquals(3, StreakCalculator.currentStreak(completed, grace, today))
  }

  @Test
  fun `second missed day resets the streak`() {
    val completed = setOf(today, today.minusDays(3))
    val grace = setOf(today.minusDays(1))
    assertEquals(1, StreakCalculator.currentStreak(completed, grace, today))
  }

  @Test
  fun `grace is available once per rolling seven days`() {
    assertTrue(StreakCalculator.graceAvailable(emptySet(), today))
    assertFalse(StreakCalculator.graceAvailable(setOf(today.minusDays(3)), today))
    assertTrue(StreakCalculator.graceAvailable(setOf(today.minusDays(7)), today))
  }

  @Test
  fun `missed day outcome reports grace or reset`() {
    assertEquals(
      MissedDayOutcome.GraceConsumed,
      StreakCalculator.onMissedDay(emptySet(), emptySet(), today.minusDays(1)),
    )
    assertEquals(
      MissedDayOutcome.StreakReset,
      StreakCalculator.onMissedDay(emptySet(), setOf(today.minusDays(2)), today.minusDays(1)),
    )
    assertEquals(
      MissedDayOutcome.StillActive,
      StreakCalculator.onMissedDay(setOf(today.minusDays(1)), emptySet(), today.minusDays(1)),
    )
  }

  @Test
  fun `weekly streak needs five distinct days`() {
    val weekStart = LocalDate.of(2026, 10, 26)
    val fiveDays = (0L..4L).map { weekStart.plusDays(it) }
    assertEquals(1, StreakCalculator.weeklyStreak(fiveDays.toSet(), today))
    assertEquals(0, StreakCalculator.weeklyStreak(fiveDays.dropLast(1).toSet(), today))
  }

  @Test
  fun `weekly streak counts consecutive qualifying weeks`() {
    val thisWeek = LocalDate.of(2026, 10, 26)
    val lastWeek = thisWeek.minusWeeks(1)
    val days =
      (0L..4L).map { thisWeek.plusDays(it) } .toSet() +
        (0L..4L).map { lastWeek.plusDays(it) }.toSet()
    assertEquals(2, StreakCalculator.weeklyStreak(days, today))
  }

  @Test
  fun `partial current week keeps the previous streak alive`() {
    val thisWeek = LocalDate.of(2026, 10, 26)
    val lastWeek = thisWeek.minusWeeks(1)
    val days =
      setOf(thisWeek, thisWeek.plusDays(1)) +
        (0L..4L).map { lastWeek.plusDays(it) }.toSet()
    assertEquals(1, StreakCalculator.weeklyStreak(days, today))
  }
}
