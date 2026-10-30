package com.example.liferpg.ui.quests

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.liferpg.R
import com.example.liferpg.di.AppContainer
import com.example.liferpg.domain.model.DeadlineType
import com.example.liferpg.domain.model.Quest
import com.example.liferpg.domain.model.QuestStatus
import com.example.liferpg.ui.components.DifficultyBadge
import com.example.liferpg.ui.components.deadlineLabel
import com.example.liferpg.ui.components.statusLabel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestDetailScreen(
  container: AppContainer,
  questId: Long,
  onBack: () -> Unit,
  onEdit: () -> Unit,
) {
  val viewModel: QuestDetailViewModel =
    viewModel(
      key = "quest-detail-$questId",
      factory =
        viewModelFactory {
          initializer { QuestDetailViewModel(questId, container.questRepository) }
        }
    )
  val state by viewModel.state.collectAsStateWithLifecycle()
  val snackbarHostState = remember { SnackbarHostState() }
  val context = LocalContext.current
  var showDeleteDialog by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    viewModel.messages.collect { message ->
      when (message) {
        is QuestDetailMessage.Completed ->
          snackbarHostState.showSnackbar(
            context.getString(R.string.message_quest_completed, message.xp, message.gold)
          )
        QuestDetailMessage.Cancelled ->
          snackbarHostState.showSnackbar(context.getString(R.string.message_quest_cancelled))
        QuestDetailMessage.Deleted -> onBack()
        QuestDetailMessage.Failed ->
          snackbarHostState.showSnackbar(context.getString(R.string.error_generic))
      }
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(stringResource(R.string.quest_detail_title)) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
          }
        },
        actions = {
          val quest = (state as? QuestDetailUiState.Success)?.quest
          if (quest?.status == QuestStatus.Active) {
            IconButton(onClick = onEdit) {
              Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.action_edit))
            }
          }
        },
      )
    },
    snackbarHost = { SnackbarHost(snackbarHostState) },
  ) { innerPadding ->
    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
      when (val current = state) {
        QuestDetailUiState.Loading ->
          CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        QuestDetailUiState.NotFound ->
          Column(
            modifier = Modifier.align(Alignment.Center).padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Text(
              text = stringResource(R.string.error_quest_not_found),
              style = MaterialTheme.typography.titleMedium,
            )
            TextButton(onClick = onBack) { Text(stringResource(R.string.action_back)) }
          }
        is QuestDetailUiState.Error ->
          Text(
            text = stringResource(current.messageRes),
            modifier = Modifier.align(Alignment.Center).padding(32.dp),
          )
        is QuestDetailUiState.Success ->
          QuestDetailContent(
            quest = current.quest,
            onComplete = viewModel::complete,
            onCancel = viewModel::cancel,
            onStartTimer = viewModel::startTimer,
            onDelete = { showDeleteDialog = true },
          )
      }
    }
  }

  if (showDeleteDialog) {
    AlertDialog(
      onDismissRequest = { showDeleteDialog = false },
      title = { Text(stringResource(R.string.quest_delete_confirm_title)) },
      text = { Text(stringResource(R.string.quest_delete_confirm_body)) },
      confirmButton = {
        TextButton(
          onClick = {
            showDeleteDialog = false
            viewModel.delete()
          }
        ) {
          Text(stringResource(R.string.action_delete))
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteDialog = false }) {
          Text(stringResource(R.string.action_cancel))
        }
      },
    )
  }
}

@Composable
private fun QuestDetailContent(
  quest: Quest,
  onComplete: () -> Unit,
  onCancel: () -> Unit,
  onStartTimer: () -> Unit,
  onDelete: () -> Unit,
) {
  Column(
    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Text(text = quest.title, style = MaterialTheme.typography.headlineSmall)

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
      DifficultyBadge(quest.difficulty)
      Text(text = quest.category.name, style = MaterialTheme.typography.labelMedium)
      Text(text = statusLabel(quest.status), style = MaterialTheme.typography.labelMedium)
    }

    Text(
      text = stringResource(R.string.quest_reward_line, quest.xpReward, quest.goldReward),
      style = MaterialTheme.typography.bodyLarge,
    )
    Text(
      text = deadlineLabel(quest),
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    if (quest.description.isNotBlank()) {
      HorizontalDivider()
      Text(text = quest.description, style = MaterialTheme.typography.bodyMedium)
    }

    if (quest.subtasks.isNotEmpty()) {
      HorizontalDivider()
      quest.subtasks.forEach { subtask ->
        Text(
          text = if (subtask.isDone) "✓ ${subtask.title}" else "• ${subtask.title}",
          style = MaterialTheme.typography.bodyMedium,
        )
      }
    }

    HorizontalDivider()

    if (quest.status == QuestStatus.Active) {
      Button(onClick = onComplete, modifier = Modifier.fillMaxWidth()) {
        Text(stringResource(R.string.action_complete))
      }
      if (quest.deadlineType == DeadlineType.Timer && quest.timerStartedAt == null) {
        OutlinedButton(onClick = onStartTimer, modifier = Modifier.fillMaxWidth()) {
          Text(stringResource(R.string.action_start))
        }
      }
      TextButton(onClick = onCancel, modifier = Modifier.fillMaxWidth()) {
        Text(stringResource(R.string.action_cancel))
      }
    }

    TextButton(onClick = onDelete, modifier = Modifier.fillMaxWidth()) {
      Text(stringResource(R.string.action_delete), color = MaterialTheme.colorScheme.error)
    }
  }
}
