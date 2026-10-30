package com.example.liferpg.domain.repository

import com.example.liferpg.domain.model.QuestEvent
import kotlinx.coroutines.flow.Flow

/** Derived progression values. Nothing here is stored; every value comes from the ledger. */
data class Progress(
  val totalXp: Long,
  val gold: Long,
  val level: Int,
)

interface ProgressRepository {
  fun observeProgress(): Flow<Progress>

  suspend fun snapshot(): Progress

  fun observeRecentEvents(limit: Int): Flow<List<QuestEvent>>
}
