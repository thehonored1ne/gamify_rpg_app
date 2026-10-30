package com.example.liferpg.domain.system

import com.example.liferpg.domain.model.Category
import com.example.liferpg.domain.model.Difficulty
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class LibraryQuest(
  val id: String,
  val title: String,
  val description: String,
  val category: Category,
  val rarity: Difficulty,
  val tags: List<String> = emptyList(),
)

data class GeneratedOffer(
  val rarity: Difficulty,
  val libraryQuestId: String,
)

/**
 * Deterministic daily offer selection from `specs/006-system-quests.md`.
 *
 * Pure function: the same `(date, level, library, history)` always yields the same offers, so a
 * restart shows exactly the same cards.
 */
object OfferGenerator {
  private val EXCLUSION_WINDOWS = intArrayOf(7, 3, 1, 0)

  fun unlockedRarities(level: Int): List<Difficulty> =
    when {
      level < 2 -> emptyList()
      level < 5 -> listOf(Difficulty.Common, Difficulty.Uncommon)
      else -> listOf(Difficulty.Common, Difficulty.Uncommon, Difficulty.Rare)
    }

  fun generate(
    date: LocalDate,
    level: Int,
    library: List<LibraryQuest>,
    offeredOn: Map<String, LocalDate> = emptyMap(),
  ): List<GeneratedOffer> =
    unlockedRarities(level).mapNotNull { rarity ->
      val byRarity = library.filter { it.rarity == rarity }
      if (byRarity.isEmpty()) return@mapNotNull null

      val candidate =
        EXCLUSION_WINDOWS
          .asSequence()
          .map { window ->
            byRarity.filter { quest ->
              val lastOffered = offeredOn[quest.id]
              lastOffered == null || ChronoUnit.DAYS.between(lastOffered, date) >= window
            }
          }
          .firstOrNull { it.isNotEmpty() }
          ?.minByOrNull { score(date, rarity, it.id) }
          ?: return@mapNotNull null

      GeneratedOffer(rarity = rarity, libraryQuestId = candidate.id)
    }

  /** Stable FNV-1a hash so selection does not depend on JVM internals. */
  fun score(date: LocalDate, rarity: Difficulty, libraryQuestId: String): Long {
    val input = "$date|${rarity.name}|$libraryQuestId"
    var hash = 0xCBF29CE484222325uL.toLong() // FNV-1a 64-bit offset basis
    input.forEach { char ->
      hash = hash xor char.code.toLong()
      hash *= 0x100000001b3L
    }
    return hash
  }
}
