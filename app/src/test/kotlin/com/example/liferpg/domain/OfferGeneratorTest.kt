package com.example.liferpg.domain

import com.example.liferpg.domain.model.Category
import com.example.liferpg.domain.model.Difficulty
import com.example.liferpg.domain.system.LibraryQuest
import com.example.liferpg.domain.system.OfferGenerator
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OfferGeneratorTest {
  private val date = LocalDate.of(2026, 10, 30)

  private fun quest(id: String, rarity: Difficulty) =
    LibraryQuest(
      id = id,
      title = id,
      description = "",
      category = Category.Personal,
      rarity = rarity,
    )

  private val library =
    listOf(
      quest("sys_c1", Difficulty.Common),
      quest("sys_c2", Difficulty.Common),
      quest("sys_c3", Difficulty.Common),
      quest("sys_u1", Difficulty.Uncommon),
      quest("sys_u2", Difficulty.Uncommon),
      quest("sys_r1", Difficulty.Rare),
      quest("sys_r2", Difficulty.Rare),
    )

  @Test
  fun `offers are locked below level two`() {
    assertTrue(OfferGenerator.generate(date, level = 1, library = library).isEmpty())
  }

  @Test
  fun `level two unlocks common and uncommon only`() {
    val rarities = OfferGenerator.generate(date, level = 2, library = library).map { it.rarity }
    assertEquals(listOf(Difficulty.Common, Difficulty.Uncommon), rarities)
  }

  @Test
  fun `level five adds rare`() {
    val rarities = OfferGenerator.generate(date, level = 5, library = library).map { it.rarity }
    assertEquals(listOf(Difficulty.Common, Difficulty.Uncommon, Difficulty.Rare), rarities)
  }

  @Test
  fun `selection is deterministic for the same inputs`() {
    val first = OfferGenerator.generate(date, 5, library)
    val second = OfferGenerator.generate(date, 5, library)
    assertEquals(first, second)
  }

  @Test
  fun `the same quest scores differently on different dates`() {
    assertNotEquals(
      OfferGenerator.score(date, Difficulty.Common, "sys_c1"),
      OfferGenerator.score(date.plusDays(1), Difficulty.Common, "sys_c1"),
    )
  }

  @Test
  fun `recently offered quests are skipped`() {
    val first = OfferGenerator.generate(date, 5, library)
    val commonId = first.first { it.rarity == Difficulty.Common }.libraryQuestId
    val history = mapOf(commonId to date.minusDays(1))
    val next = OfferGenerator.generate(date, 5, library, history)
    val nextCommon = next.first { it.rarity == Difficulty.Common }.libraryQuestId
    assertNotEquals(commonId, nextCommon)
  }

  @Test
  fun `exclusion window shrinks when candidates run out`() {
    val single = listOf(quest("sys_only", Difficulty.Common))
    val history = mapOf("sys_only" to date.minusDays(2))
    val offers = OfferGenerator.generate(date, 2, single, history)
    assertEquals("sys_only", offers.single().libraryQuestId)
  }

  @Test
  fun `missing rarity is skipped without failing`() {
    val onlyCommon = listOf(quest("sys_c1", Difficulty.Common))
    val offers = OfferGenerator.generate(date, 5, onlyCommon)
    assertEquals(1, offers.size)
    assertTrue(offers.all { it.rarity == Difficulty.Common })
  }

  @Test
  fun `score is stable across calls`() {
    assertEquals(
      OfferGenerator.score(date, Difficulty.Common, "sys_c1"),
      OfferGenerator.score(date, Difficulty.Common, "sys_c1"),
    )
  }
}
