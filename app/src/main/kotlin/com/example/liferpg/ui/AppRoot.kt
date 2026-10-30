package com.example.liferpg.ui

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.liferpg.di.AppContainer
import com.example.liferpg.ui.quests.QuestDetailScreen
import com.example.liferpg.ui.quests.QuestEditScreen
import com.example.liferpg.ui.quests.QuestsScreen

@Composable
fun AppRoot(container: AppContainer) {
  val backStack = rememberNavBackStack(Quests)

  NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryProvider =
      entryProvider {
        entry<Quests> {
          QuestsScreen(
            container = container,
            onQuestClick = { questId -> backStack.add(QuestDetail(questId)) },
            onCreateQuest = { backStack.add(QuestEdit()) },
          )
        }
        entry<QuestDetail> { key ->
          QuestDetailScreen(
            container = container,
            questId = key.questId,
            onBack = { backStack.removeLastOrNull() },
            onEdit = { backStack.add(QuestEdit(key.questId)) },
          )
        }
        entry<QuestEdit> { key ->
          QuestEditScreen(
            container = container,
            questId = key.questId,
            onDone = { backStack.removeLastOrNull() },
          )
        }
      },
  )
}
