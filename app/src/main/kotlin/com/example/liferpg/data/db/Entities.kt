package com.example.liferpg.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "quests",
  indices = [Index(value = ["status", "dueAt"])],
)
data class QuestEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  val description: String,
  val difficulty: String,
  val category: String,
  val xpReward: Int,
  val goldReward: Int,
  val deadlineType: String,
  val dueAt: Long?,
  val durationMinutes: Int?,
  val timerStartedAt: Long?,
  val reminderAt: Long?,
  val penaltyType: String,
  val penaltyAmount: Int,
  val status: String,
  val source: String,
  val systemQuestId: String?,
  val createdAt: Long,
  val updatedAt: Long,
  val completedAt: Long?,
  val failedAt: Long?,
)

@Entity(
  tableName = "subtasks",
  foreignKeys = [
    ForeignKey(
      entity = QuestEntity::class,
      parentColumns = ["id"],
      childColumns = ["questId"],
      onDelete = ForeignKey.CASCADE,
    )
  ],
  indices = [Index("questId")],
)
data class SubtaskEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val questId: Long,
  val title: String,
  val isDone: Boolean,
  val position: Int,
)

/**
 * Append-only ledger. `questId` is nulled when the quest is deleted; `questTitle` keeps the
 * snapshot so history stays readable.
 */
@Entity(
  tableName = "quest_events",
  indices = [Index("occurredAt"), Index("questId")],
)
data class QuestEventEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val questId: Long?,
  val questTitle: String?,
  val type: String,
  val xpDelta: Int,
  val goldDelta: Int,
  val occurredAt: Long,
  val note: String? = null,
)

@Entity(
  tableName = "system_offers",
  indices = [Index(value = ["date", "rarity"], unique = true)],
)
data class SystemOfferEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val date: String,
  val libraryQuestId: String,
  val rarity: String,
  val status: String,
  val createdAt: Long,
)

@Entity(tableName = "unlocked_achievements")
data class UnlockedAchievementEntity(
  @PrimaryKey val achievementId: String,
  val unlockedAt: Long,
)

@Entity(tableName = "owned_items")
data class OwnedItemEntity(
  @PrimaryKey val itemId: String,
  val purchasedAt: Long,
)

@Entity(tableName = "streak_grace")
data class StreakGraceEntity(
  @PrimaryKey val date: String,
)
