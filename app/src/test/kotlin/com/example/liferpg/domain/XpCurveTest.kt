package com.example.liferpg.domain

import com.example.liferpg.domain.progression.XpCurve
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class XpCurveTest {
  @Test
  fun `level thresholds match the spec table`() {
    assertEquals(0L, XpCurve.cumulativeXpForLevel(1))
    assertEquals(50L, XpCurve.cumulativeXpForLevel(2))
    assertEquals(125L, XpCurve.cumulativeXpForLevel(3))
    assertEquals(225L, XpCurve.cumulativeXpForLevel(4))
    assertEquals(350L, XpCurve.cumulativeXpForLevel(5))
    assertEquals(1350L, XpCurve.cumulativeXpForLevel(10))
  }

  @Test
  fun `xp to next follows the linear curve`() {
    assertEquals(50L, XpCurve.xpToNext(1))
    assertEquals(75L, XpCurve.xpToNext(2))
    assertEquals(100L, XpCurve.xpToNext(3))
    assertEquals(2500L, XpCurve.xpToNext(100 - 1))
  }

  @Test
  fun `level derivation at boundaries`() {
    assertEquals(1, XpCurve.levelFor(0))
    assertEquals(1, XpCurve.levelFor(49))
    assertEquals(2, XpCurve.levelFor(50))
    assertEquals(2, XpCurve.levelFor(124))
    assertEquals(3, XpCurve.levelFor(125))
    assertEquals(10, XpCurve.levelFor(1350))
  }

  @Test
  fun `level cap holds and never overflows`() {
    val capThreshold = XpCurve.cumulativeXpForLevel(XpCurve.MAX_LEVEL)
    assertEquals(100, XpCurve.levelFor(capThreshold))
    assertEquals(100, XpCurve.levelFor(capThreshold + 1_000_000L))
    assertEquals(100, XpCurve.levelFor(Long.MAX_VALUE))
  }

  @Test
  fun `negative xp clamps to level one`() {
    assertEquals(1, XpCurve.levelFor(-500L))
  }

  @Test
  fun `progress inside a level`() {
    val progress = XpCurve.progress(60L)
    assertEquals(2, progress.level)
    assertEquals(10, progress.xpIntoLevel)
    assertEquals(75, progress.xpForNext)
    assertTrue(!progress.isMaxLevel)
    assertEquals(10f / 75f, progress.fraction, 0.0001f)
  }

  @Test
  fun `progress at the cap reports max level`() {
    val progress = XpCurve.progress(XpCurve.cumulativeXpForLevel(100) + 5_000)
    assertEquals(100, progress.level)
    assertTrue(progress.isMaxLevel)
    assertEquals(1f, progress.fraction, 0f)
  }
}
