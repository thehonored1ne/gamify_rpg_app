package com.example.liferpg.domain

import com.example.liferpg.domain.model.Difficulty
import com.example.liferpg.domain.progression.RewardRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RewardRulesTest {
  @Test
  fun `baselines match the spec`() {
    assertEquals(5, Difficulty.Common.xpBaseline)
    assertEquals(2, Difficulty.Common.goldBaseline)
    assertEquals(80, Difficulty.Legendary.xpBaseline)
    assertEquals(40, Difficulty.Legendary.goldBaseline)
  }

  @Test
  fun `xp range is ceil half to double`() {
    assertEquals(3, RewardRules.minXp(Difficulty.Common))
    assertEquals(10, RewardRules.maxXp(Difficulty.Common))
    assertEquals(5, RewardRules.minXp(Difficulty.Uncommon))
    assertEquals(20, RewardRules.maxXp(Difficulty.Uncommon))
    assertEquals(40, RewardRules.minXp(Difficulty.Legendary))
    assertEquals(160, RewardRules.maxXp(Difficulty.Legendary))
  }

  @Test
  fun `gold range starts at zero`() {
    assertEquals(0, RewardRules.minGold(Difficulty.Rare))
    assertEquals(20, RewardRules.maxGold(Difficulty.Rare))
    assertTrue(RewardRules.isValidGold(Difficulty.Rare, 0))
    assertFalse(RewardRules.isValidGold(Difficulty.Rare, 21))
  }

  @Test
  fun `xp bounds are enforced`() {
    assertFalse(RewardRules.isValidXp(Difficulty.Common, 2))
    assertTrue(RewardRules.isValidXp(Difficulty.Common, 3))
    assertTrue(RewardRules.isValidXp(Difficulty.Common, 10))
    assertFalse(RewardRules.isValidXp(Difficulty.Common, 11))
    assertFalse(RewardRules.isValidXp(Difficulty.Common, 0))
  }

  @Test
  fun `clamping stays inside the allowed range`() {
    assertEquals(3, RewardRules.clampXp(Difficulty.Common, -20))
    assertEquals(10, RewardRules.clampXp(Difficulty.Common, 999))
    assertEquals(0, RewardRules.clampGold(Difficulty.Common, -1))
    assertEquals(4, RewardRules.clampGold(Difficulty.Common, 4))
    assertEquals(4, RewardRules.clampGold(Difficulty.Common, 5))
  }
}
