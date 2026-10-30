package com.example.liferpg.ui.quests

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.liferpg.R
import com.example.liferpg.di.AppContainer
import com.example.liferpg.ui.components.ProgressHeader
import com.example.liferpg.ui.components.QuestCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestsScreen(
  container: AppContainer,
  onQuestClick: (Long) -> Unit,
  onCreateQuest: () -> Unit,
) {
  val viewModel: QuestsViewModel =
    viewModel(
      factory =
        viewModelFactory {
          initializer {
            QuestsViewModel(
              questRepository = container.questRepository,
              progressRepository = container.progressRepository,
            )
          }
        }
    )
  val state by viewModel.state.collectAsStateWithLifecycle()

  Scaffold(
    topBar = { TopAppBar(title = { Text(stringResource(R.string.quests_title)) }) },
    floatingActionButton = {
      ExtendedFloatingActionButton(
        onClick = onCreateQuest,
        icon = { Icon(Icons.Default.Add, contentDescription = null) },
        text = { Text(stringResource(R.string.action_create_quest)) },
      )
    },
  ) { innerPadding ->
    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
      when (val current = state) {
        QuestsUiState.Loading ->
          CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        is QuestsUiState.Error ->
          MessageContent(
            title = stringResource(current.messageRes),
            actionLabel = null,
            onAction = {},
            modifier = Modifier.align(Alignment.Center),
          )
        is QuestsUiState.Success ->
          if (current.quests.isEmpty()) {
            MessageContent(
              title = stringResource(R.string.quests_empty_title),
              body = stringResource(R.string.quests_empty_body),
              actionLabel = stringResource(R.string.action_create_quest),
              onAction = onCreateQuest,
              modifier = Modifier.align(Alignment.Center),
            )
          } else {
            LazyColumn(contentPadding = PaddingValues(bottom = 96.dp)) {
              item { ProgressHeader(current.progress) }
              items(current.quests, key = { it.id }) { quest ->
                QuestCard(
                  quest = quest,
                  onClick = { onQuestClick(quest.id) },
                  modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                )
              }
            }
          }
      }
    }
  }
}

@Composable
private fun MessageContent(
  title: String,
  body: String? = null,
  actionLabel: String? = null,
  onAction: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier.padding(32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.titleMedium,
      textAlign = TextAlign.Center,
    )
    body?.let {
      Text(
        text = it,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
      )
    }
    actionLabel?.let { TextButton(onClick = onAction) { Text(it) } }
  }
}
