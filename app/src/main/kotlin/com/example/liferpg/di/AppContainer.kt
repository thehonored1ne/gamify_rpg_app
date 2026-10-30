package com.example.liferpg.di

import android.content.Context
import androidx.room.Room
import com.example.liferpg.core.time.SystemTimeProvider
import com.example.liferpg.core.time.TimeProvider
import com.example.liferpg.data.db.LifeRpgDatabase
import com.example.liferpg.data.repository.DefaultProgressRepository
import com.example.liferpg.data.repository.DefaultQuestRepository
import com.example.liferpg.domain.repository.ProgressRepository
import com.example.liferpg.domain.repository.QuestRepository

/**
 * Manual dependency container, created once in [com.example.liferpg.LifeRpgApplication].
 *
 * No DI framework until this exceeds roughly twenty entries.
 */
class AppContainer(context: Context) {
  val timeProvider: TimeProvider = SystemTimeProvider()

  private val database: LifeRpgDatabase =
    Room.databaseBuilder(context.applicationContext, LifeRpgDatabase::class.java, LifeRpgDatabase.NAME)
      .build()

  val questRepository: QuestRepository =
    DefaultQuestRepository(
      database = database,
      questDao = database.questDao(),
      subtaskDao = database.subtaskDao(),
      eventDao = database.questEventDao(),
      timeProvider = timeProvider,
    )

  val progressRepository: ProgressRepository = DefaultProgressRepository(database.questEventDao())
}
