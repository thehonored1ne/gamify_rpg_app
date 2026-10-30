package com.example.liferpg.domain

import com.example.liferpg.domain.model.PenaltyType
import com.example.liferpg.domain.penalty.PenaltyRules
import com.example.liferpg.domain.progression.XpCurve
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PenaltyRulesTest {
  @Test
  fun `caps are half the reward`() {
    assertEquals(1, PenaltyRules.maxGold(2))
    assertEquals(2, PenaltyRules.maxGold(5))
    assertEquals(1, PenaltyRules.maxXp(3))
    assertEquals(40, PenaltyRules.maxXp(80))
  }

  @Test
  fun `amounts above the cap are rejected`() {
    assertTrue(PenaltyRules.isValid(PenaltyType.Gold, 2, questXpReward = 10, questGoldReward = 5))
    assertFalse(PenaltyRules.isValid(PenaltyType.Gold, 3, questXpReward = 10, questGoldReward = 5))
    assertFalse(PenaltyRules.isValid(PenaltyType.Xp, 6, questXpReward = 10, questGoldReward = 5))
    assertTrue(PenaltyRules.isValid(PenaltyType.Xp, 5, questXpReward = 10, questGoldReward = 5))
  }

  @Test
  fun `non numeric penalties carry no amount`() {
    assertTrue(PenaltyRules.isValid(PenaltyType.None, 0, 10, 5))
    assertTrue(PenaltyRules.isValid(PenaltyType.Mark, 0, 10, 5))
    assertTrue(PenaltyRules.isValid(PenaltyType.Streak, 0, 10, 5))
    assertFalse(PenaltyRules.isValid(PenaltyType.Mark, 1, 10, 5))
  }

  @Test
  fun `gold deduction never makes the balance negative`() {
    assertEquals(5L, PenaltyRules.goldDeduction(5, 10))
    assertEquals(3L, PenaltyRules.goldDeduction(5, 3))
    assertEquals(0L, PenaltyRules.goldDeduction(5, 0))
  }

  @Test
  fun `xp deduction never drops below the current level floor`() {
    val floor = XpCurve.cumulativeXpForLevel(2)
    assertEquals(50L, floor)
    assertEquals(10L, PenaltyRules.xpDeduction(40, 60))
    assertEquals(0L, PenaltyRules.xpDeduction(40, 50))
    assertEquals(10L, PenaltyRules.xpDeduction(40, 10))
  }

  @Test
  fun `streak penalty resets without consuming grace`() {
    assertTrue(PenaltyRules.resetsStreak(PenaltyType.Streak))
    assertFalse(PenaltyRules.resetsStreak(PenaltyType.Gold))
    assertTrue(PenaltyRules.incrementsFailedCount(PenaltyType.Mark))
  }
}
