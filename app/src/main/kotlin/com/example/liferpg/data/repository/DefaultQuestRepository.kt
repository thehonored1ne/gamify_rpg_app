package com.example.liferpg.data.repository

import androidx.room.withTransaction
import com.example.liferpg.core.time.TimeProvider
import com.example.liferpg.data.db.LifeRpgDatabase
import com.example.liferpg.data.db.QuestDao
import com.example.liferpg.data.db.QuestEventDao
import com.example.liferpg.data.db.QuestEventEntity
import com.example.liferpg.data.db.SubtaskDao
import com.example.liferpg.data.db.SubtaskEntity
import com.example.liferpg.data.db.toDomain
import com.example.liferpg.data.db.toEntity
import com.example.liferpg.data.db.toSubtaskEntities
import com.example.liferpg.domain.model.Completion
import com.example.liferpg.domain.model.DeadlineType
import com.example.liferpg.domain.model.PenaltyType
import com.example.liferpg.domain.model.Quest
import com.example.liferpg.domain.model.QuestDraft
import com.example.liferpg.domain.model.QuestEventType
import com.example.liferpg.domain.model.QuestStatus
import com.example.liferpg.domain.model.QuestValidationException
import com.example.liferpg.domain.penalty.PenaltyRules
import com.example.liferpg.domain.progression.XpCurve
import com.example.liferpg.domain.quest.QuestOrder
import com.example.liferpg.domain.quest.QuestValidator
import com.example.liferpg.domain.repository.QuestRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class DefaultQuestRepository(
  private val database: LifeRpgDatabase,
  private val questDao: QuestDao,
  private val subtaskDao: SubtaskDao,
  private val eventDao: QuestEventDao,
  private val timeProvider: TimeProvider,
) : QuestRepository {

  override fun observeQuests(): Flow<List<Quest>> =
    combine(questDao.observeAll(), subtaskDao.observeAll()) { quests, subtasks ->
      val byQuest = subtasks.groupBy { it.questId }
      QuestOrder.sorted(quests.map { it.toDomain(byQuest[it.id].orEmpty()) })
    }

  override fun observeQuest(id: Long): Flow<Quest?> =
    combine(questDao.observeById(id), subtaskDao.observeForQuest(id)) { entity, subtasks ->
      entity?.toDomain(subtasks)
    }

  override suspend fun getQuest(id: Long): Quest? {
    val entity = questDao.findById(id) ?: return null
    return entity.toDomain(subtaskDao.listForQuest(id))
  }

  override suspend fun create(draft: QuestDraft): Result<Long> = resultOf {
    val now = timeProvider.nowMillis()
    val clean = draft.normalized()
    val violations = QuestValidator.validate(clean, now)
    if (violations.isNotEmpty()) throw QuestValidationException(violations)

    database.withTransaction {
      val id = questDao.insert(clean.toEntity(now))
      if (clean.subtasks.isNotEmpty()) subtaskDao.insertAll(clean.toSubtaskEntities(id))
      id
    }
  }

  override suspend fun update(id: Long, draft: QuestDraft): Result<Unit> = resultOf {
    val now = timeProvider.nowMillis()
    val existing = questDao.findById(id) ?: throw NoSuchElementException("quest $id not found")
    check(existing.status == QuestStatus.Active.name) { "only active quests can be edited" }

    val clean = draft.normalized()
    val violations = QuestValidator.validate(clean, now)
    if (violations.isNotEmpty()) throw QuestValidationException(violations)

    database.withTransaction {
      questDao.update(
        existing.copy(
          title = clean.title,
          description = clean.description,
          difficulty = clean.difficulty.name,
          category = clean.category.name,
          xpReward = clean.xpReward,
          goldReward = clean.goldReward,
          deadlineType = clean.deadlineType.name,
          dueAt = clean.dueAt,
          durationMinutes = clean.durationMinutes,
          reminderAt = clean.reminderAt,
          penaltyType = clean.penaltyType.name,
          penaltyAmount = clean.penaltyAmount,
          updatedAt = now,
        )
      )
      reconcileSubtasks(id, clean.subtasks)
    }
  }

  override suspend fun delete(id: Long) {
    database.withTransaction {
      eventDao.detachQuest(id)
      questDao.deleteById(id)
    }
  }

  override suspend fun complete(id: Long): Result<Completion> = resultOf {
    database.withTransaction {
      val entity = questDao.findById(id) ?: throw NoSuchElementException("quest $id not found")
      check(entity.status == QuestStatus.Active.name) { "quest $id is not active" }

      val now = timeProvider.nowMillis()
      val xpBefore = eventDao.totalXp()

      eventDao.insert(
        QuestEventEntity(
          questId = id,
          questTitle = entity.title,
          type = QuestEventType.Completed.name,
          xpDelta = entity.xpReward,
          goldDelta = entity.goldReward,
          occurredAt = now,
        )
      )
      questDao.update(entity.copy(status = QuestStatus.Completed.name, completedAt = now, updatedAt = now))

      val xpAfter = xpBefore + entity.xpReward
      Completion(
        questId = id,
        xpAwarded = entity.xpReward,
        goldAwarded = entity.goldReward,
        levelBefore = XpCurve.levelFor(xpBefore),
        levelAfter = XpCurve.levelFor(xpAfter),
        completedAt = now,
      )
    }
  }

  override suspend fun undoComplete(id: Long): Result<Unit> = resultOf {
    database.withTransaction {
      val entity = questDao.findById(id) ?: throw NoSuchElementException("quest $id not found")
      check(entity.status == QuestStatus.Completed.name) { "quest $id is not completed" }

      val completedAt = entity.completedAt ?: throw IllegalStateException("quest $id has no completion time")
      val now = timeProvider.nowMillis()
      check(now - completedAt <= QuestRepository.UNDO_WINDOW_MS) { "undo window closed" }

      val completion =
        eventDao.latestCompletion(id) ?: throw IllegalStateException("no completion event for quest $id")
      eventDao.insert(
        QuestEventEntity(
          questId = id,
          questTitle = entity.title,
          type = QuestEventType.Undone.name,
          xpDelta = -completion.xpDelta,
          goldDelta = -completion.goldDelta,
          occurredAt = now,
          note = "undo",
        )
      )
      questDao.update(entity.copy(status = QuestStatus.Active.name, completedAt = null, updatedAt = now))
    }
  }

  override suspend fun cancel(id: Long) {
    val now = timeProvider.nowMillis()
    database.withTransaction {
      val entity = questDao.findById(id) ?: return@withTransaction
      if (entity.status != QuestStatus.Active.name) return@withTransaction
      questDao.update(entity.copy(status = QuestStatus.Cancelled.name, updatedAt = now))
    }
  }

  override suspend fun startTimer(id: Long) {
    val now = timeProvider.nowMillis()
    database.withTransaction {
      val entity = questDao.findById(id) ?: return@withTransaction
      if (entity.deadlineType != DeadlineType.Timer.name) return@withTransaction
      if (entity.timerStartedAt != null) return@withTransaction
      questDao.update(entity.copy(timerStartedAt = now, updatedAt = now))
    }
  }

  override suspend fun failExpired(): Int {
    val now = timeProvider.nowMillis()
    val expired = questDao.listExpiredByDate(now) + questDao.listExpiredTimers(now)
    var failed = 0

    for (candidate in expired) {
      database.withTransaction {
        val entity = questDao.findById(candidate.id) ?: return@withTransaction
        if (entity.status != QuestStatus.Active.name) return@withTransaction

        questDao.update(entity.copy(status = QuestStatus.Failed.name, failedAt = now, updatedAt = now))
        eventDao.insert(
          QuestEventEntity(
            questId = entity.id,
            questTitle = entity.title,
            type = QuestEventType.Failed.name,
            xpDelta = 0,
            goldDelta = 0,
            occurredAt = now,
          )
        )
        applyPenalty(entity, now)
        failed++
      }
    }
    return failed
  }

  private suspend fun reconcileSubtasks(questId: Long, titles: List<String>) {
    val existing = subtaskDao.listForQuest(questId)
    titles.forEachIndexed { index, title ->
      val previous = existing.getOrNull(index)
      if (previous != null) {
        subtaskDao.updateTitleAndPosition(previous.id, title, index)
      } else {
        subtaskDao.insertAll(
          listOf(SubtaskEntity(questId = questId, title = title, isDone = false, position = index))
        )
      }
    }
    if (existing.size > titles.size) {
      subtaskDao.deleteByIds(existing.drop(titles.size).map { it.id })
    }
  }

  private suspend fun applyPenalty(entity: com.example.liferpg.data.db.QuestEntity, now: Long) {
    when (PenaltyType.valueOf(entity.penaltyType)) {
      PenaltyType.None -> Unit
      PenaltyType.Gold -> {
        val deduction = PenaltyRules.goldDeduction(entity.penaltyAmount, eventDao.gold())
        if (deduction > 0L) {
          eventDao.insert(
            QuestEventEntity(
              questId = entity.id,
              questTitle = entity.title,
              type = QuestEventType.PenaltyApplied.name,
              xpDelta = 0,
              goldDelta = -deduction.toInt(),
              occurredAt = now,
            )
          )
        }
      }
      PenaltyType.Xp -> {
        val deduction = PenaltyRules.xpDeduction(entity.penaltyAmount, eventDao.totalXp())
        if (deduction > 0L) {
          eventDao.insert(
            QuestEventEntity(
              questId = entity.id,
              questTitle = entity.title,
              type = QuestEventType.PenaltyApplied.name,
              xpDelta = -deduction.toInt(),
              goldDelta = 0,
              occurredAt = now,
            )
          )
        }
      }
      // Streak and Mark have no ledger delta; the streak subsystem handles them.
      PenaltyType.Streak, PenaltyType.Mark -> Unit
    }
  }

  private suspend fun <T> resultOf(block: suspend () -> T): Result<T> =
    try {
      Result.success(block())
    } catch (cancellation: CancellationException) {
      throw cancellation
    } catch (error: Exception) {
      Result.failure(error)
    }
}
