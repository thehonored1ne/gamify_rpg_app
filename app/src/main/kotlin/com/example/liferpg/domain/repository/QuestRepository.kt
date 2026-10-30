package com.example.liferpg.domain.repository

import com.example.liferpg.domain.model.Completion
import com.example.liferpg.domain.model.Quest
import com.example.liferpg.domain.model.QuestDraft
import kotlinx.coroutines.flow.Flow

/**
 * Quest persistence boundary. Implementations own the transaction that keeps quest state and the
 * ledger consistent.
 */
interface QuestRepository {
  fun observeQuests(): Flow<List<Quest>>

  fun observeQuest(id: Long): Flow<Quest?>

  suspend fun getQuest(id: Long): Quest?

  suspend fun create(draft: QuestDraft): Result<Long>

  suspend fun update(id: Long, draft: QuestDraft): Result<Unit>

  suspend fun delete(id: Long)

  /** Completes an active quest and writes the ledger row in the same transaction. */
  suspend fun complete(id: Long): Result<Completion>

  /** Reverses the most recent completion while the undo window is open. */
  suspend fun undoComplete(id: Long): Result<Unit>

  suspend fun cancel(id: Long)

  suspend fun startTimer(id: Long)

  /** Marks every expired active quest as failed and applies its penalty once. Returns the count. */
  suspend fun failExpired(): Int

  companion object {
    /** Undo stays available for ten seconds after completion, in memory only. */
    const val UNDO_WINDOW_MS: Long = 10_000L
  }
}
