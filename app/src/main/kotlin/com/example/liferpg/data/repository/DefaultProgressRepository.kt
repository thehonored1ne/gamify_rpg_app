package com.example.liferpg.data.repository

import com.example.liferpg.data.db.QuestEventDao
import com.example.liferpg.data.db.toDomain
import com.example.liferpg.domain.model.QuestEvent
import com.example.liferpg.domain.progression.XpCurve
import com.example.liferpg.domain.repository.Progress
import com.example.liferpg.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class DefaultProgressRepository(private val eventDao: QuestEventDao) : ProgressRepository {
  override fun observeProgress(): Flow<Progress> =
    combine(eventDao.observeTotalXp(), eventDao.observeGold()) { xp, gold ->
      Progress(totalXp = xp, gold = gold, level = XpCurve.levelFor(xp))
    }

  override suspend fun snapshot(): Progress {
    val xp = eventDao.totalXp()
    return Progress(totalXp = xp, gold = eventDao.gold(), level = XpCurve.levelFor(xp))
  }

  override fun observeRecentEvents(limit: Int): Flow<List<QuestEvent>> =
    eventDao.observeRecent(limit).map { events -> events.map { it.toDomain() } }
}
