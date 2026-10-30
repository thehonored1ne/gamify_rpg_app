package com.example.liferpg.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestDao {
  @Insert suspend fun insert(entity: QuestEntity): Long

  @Update suspend fun update(entity: QuestEntity)

  @Query("DELETE FROM quests WHERE id = :id") suspend fun deleteById(id: Long)

  @Query("SELECT * FROM quests WHERE id = :id") suspend fun findById(id: Long): QuestEntity?

  @Query("SELECT * FROM quests WHERE id = :id") fun observeById(id: Long): Flow<QuestEntity?>

  @Query("SELECT * FROM quests") fun observeAll(): Flow<List<QuestEntity>>

  @Query("SELECT * FROM quests WHERE status = :status") suspend fun listByStatus(status: String): List<QuestEntity>

  @Query(
    "SELECT * FROM quests WHERE status = 'Active' AND deadlineType = 'Date' AND dueAt IS NOT NULL AND dueAt <= :now"
  )
  suspend fun listExpiredByDate(now: Long): List<QuestEntity>

  @Query(
    """
    SELECT * FROM quests
    WHERE status = 'Active'
      AND deadlineType = 'Timer'
      AND timerStartedAt IS NOT NULL
      AND durationMinutes IS NOT NULL
      AND timerStartedAt + durationMinutes * 60000 <= :now
    """
  )
  suspend fun listExpiredTimers(now: Long): List<QuestEntity>
}

@Dao
interface SubtaskDao {
  @Insert suspend fun insertAll(items: List<SubtaskEntity>)

  @Query("DELETE FROM subtasks WHERE questId = :questId") suspend fun deleteForQuest(questId: Long)

  @Query("SELECT * FROM subtasks WHERE questId = :questId ORDER BY position")
  suspend fun listForQuest(questId: Long): List<SubtaskEntity>

  @Query("SELECT * FROM subtasks ORDER BY questId, position") fun observeAll(): Flow<List<SubtaskEntity>>

  @Query("SELECT * FROM subtasks WHERE questId = :questId ORDER BY position")
  fun observeForQuest(questId: Long): Flow<List<SubtaskEntity>>

  @Query("UPDATE subtasks SET isDone = :done WHERE id = :id") suspend fun setDone(id: Long, done: Boolean)

  @Query("UPDATE subtasks SET title = :title, position = :position WHERE id = :id")
  suspend fun updateTitleAndPosition(id: Long, title: String, position: Int)

  @Query("DELETE FROM subtasks WHERE id IN (:ids)") suspend fun deleteByIds(ids: List<Long>)
}

@Dao
interface QuestEventDao {
  @Insert suspend fun insert(event: QuestEventEntity): Long

  @Query("SELECT COALESCE(SUM(xpDelta), 0) FROM quest_events") fun observeTotalXp(): Flow<Long>

  @Query("SELECT COALESCE(SUM(goldDelta), 0) FROM quest_events") fun observeGold(): Flow<Long>

  @Query("SELECT COALESCE(SUM(xpDelta), 0) FROM quest_events") suspend fun totalXp(): Long

  @Query("SELECT COALESCE(SUM(goldDelta), 0) FROM quest_events") suspend fun gold(): Long

  @Query(
    "SELECT * FROM quest_events WHERE questId = :questId AND type = 'Completed' ORDER BY occurredAt DESC, id DESC LIMIT 1"
  )
  suspend fun latestCompletion(questId: Long): QuestEventEntity?

  @Query("SELECT * FROM quest_events ORDER BY occurredAt DESC, id DESC LIMIT :limit")
  fun observeRecent(limit: Int): Flow<List<QuestEventEntity>>

  @Query("UPDATE quest_events SET questId = NULL WHERE questId = :questId")
  suspend fun detachQuest(questId: Long)
}
