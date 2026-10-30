package com.example.liferpg.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Single database. Schema is exported to `app/schemas` and committed; every version bump ships a
 * migration, never a destructive fallback.
 */
@Database(
  entities = [
    QuestEntity::class,
    SubtaskEntity::class,
    QuestEventEntity::class,
    SystemOfferEntity::class,
    UnlockedAchievementEntity::class,
    OwnedItemEntity::class,
    StreakGraceEntity::class,
  ],
  version = 1,
  exportSchema = true,
)
abstract class LifeRpgDatabase : RoomDatabase() {
  abstract fun questDao(): QuestDao

  abstract fun subtaskDao(): SubtaskDao

  abstract fun questEventDao(): QuestEventDao

  companion object {
    const val NAME: String = "liferpg.db"
  }
}
