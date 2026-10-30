package com.example.liferpg.ui.quests

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.liferpg.R
import com.example.liferpg.di.AppContainer
import com.example.liferpg.domain.model.Category
import com.example.liferpg.domain.model.DeadlineType
import com.example.liferpg.domain.model.Difficulty
import com.example.liferpg.domain.model.PenaltyType
import com.example.liferpg.domain.progression.RewardRules
import com.example.liferpg.domain.quest.QuestValidator
import com.example.liferpg.ui.format.datePickerMillisToLocalEndOfDay
import com.example.liferpg.ui.format.formatDate
import com.example.liferpg.ui.messageRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestEditScreen(
  container: AppContainer,
  questId: Long?,
  onDone: () -> Unit,
) {
  val viewModel: QuestEditViewModel =
    viewModel(
      key = "quest-edit-${questId ?: "new"}",
      factory =
        viewModelFactory {
          initializer {
            QuestEditViewModel(
              questId = questId,
              repository = container.questRepository,
              timeProvider = container.timeProvider,
            )
          }
        }
    )
  val state by viewModel.state.collectAsStateWithLifecycle()
  val snackbarHostState = remember { SnackbarHostState() }
  val genericError = stringResource(R.string.error_generic)
  val notFoundError = stringResource(R.string.error_quest_not_found)
  var showDatePicker by remember { mutableStateOf(false) }

  LaunchedEffect(state.saved) { if (state.saved) onDone() }

  LaunchedEffect(state.errorMessage) {
    state.errorMessage?.let { message ->
      snackbarHostState.showSnackbar(if (message == R.string.error_quest_not_found) notFoundError else genericError)
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            if (state.isNew) {
              stringResource(R.string.action_create_quest)
            } else {
              stringResource(R.string.action_edit)
            }
          )
        },
        navigationIcon = {
          IconButton(onClick = onDone) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
          }
        },
      )
    },
    snackbarHost = { SnackbarHost(snackbarHostState) },
  ) { innerPadding ->
    if (state.loading) {
      Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
      }
      return@Scaffold
    }

    LazyColumn(
      modifier = Modifier.fillMaxSize().padding(innerPadding),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      item {
        OutlinedTextField(
          value = state.title,
          onValueChange = viewModel::setTitle,
          label = { Text(stringResource(R.string.quest_title_label)) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
        )
      }

      item {
        OutlinedTextField(
          value = state.description,
          onValueChange = viewModel::setDescription,
          label = { Text(stringResource(R.string.quest_description_label)) },
          minLines = 3,
          modifier = Modifier.fillMaxWidth(),
        )
      }

      item {
        SectionLabel(stringResource(R.string.quest_difficulty_label))
        ChipRow(
          options = Difficulty.entries,
          selected = state.difficulty,
          label = { it.name },
          onSelect = viewModel::setDifficulty,
        )
      }

      item {
        SectionLabel(stringResource(R.string.quest_category_label))
        ChipRow(
          options = Category.entries,
          selected = state.category,
          label = { it.name },
          onSelect = viewModel::setCategory,
        )
      }

      item {
        SectionLabel(stringResource(R.string.quest_rewards_label))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
          OutlinedTextField(
            value = state.xpText,
            onValueChange = viewModel::setXp,
            label = { Text(stringResource(R.string.quest_xp_label)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f),
          )
          OutlinedTextField(
            value = state.goldText,
            onValueChange = viewModel::setGold,
            label = { Text(stringResource(R.string.quest_gold_label)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f),
          )
        }
        Text(
          text =
            stringResource(
              R.string.quest_reward_hint,
              RewardRules.minXp(state.difficulty),
              RewardRules.maxXp(state.difficulty),
              RewardRules.maxGold(state.difficulty),
            ),
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(top = 4.dp),
        )
      }

      item {
        SectionLabel(stringResource(R.string.quest_deadline_label))
        ChipRow(
          options = DeadlineType.entries,
          selected = state.deadlineType,
          label = {
            when (it) {
              DeadlineType.None -> stringResource(R.string.quest_deadline_none)
              DeadlineType.Date -> stringResource(R.string.quest_deadline_date)
              DeadlineType.Timer -> stringResource(R.string.quest_deadline_timer)
            }
          },
          onSelect = viewModel::setDeadlineType,
        )
      }

      if (state.deadlineType == DeadlineType.Date) {
        item {
          OutlinedButton(
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth(),
          ) {
            Text(
              text =
                state.dueAt?.let { stringResource(R.string.quest_due_date_label) + ": " + formatDate(it) }
                  ?: stringResource(R.string.quest_due_date_unset)
            )
          }
        }
      }

      if (state.deadlineType == DeadlineType.Timer) {
        item {
          OutlinedTextField(
            value = state.durationText,
            onValueChange = viewModel::setDuration,
            label = { Text(stringResource(R.string.quest_duration_label)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
          )
        }
      }

      item {
        SectionLabel(stringResource(R.string.quest_subtasks_label))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          state.subtasks.forEachIndexed { index, subtask ->
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              OutlinedTextField(
                value = subtask.title,
                onValueChange = { viewModel.setSubtaskTitle(index, it) },
                label = { Text(stringResource(R.string.quest_subtask_label, index + 1)) },
                singleLine = true,
                modifier = Modifier.weight(1f),
              )
              IconButton(onClick = { viewModel.removeSubtask(index) }) {
                Icon(
                  Icons.Default.Delete,
                  contentDescription = stringResource(R.string.cd_remove_subtask),
                )
              }
            }
          }
          OutlinedButton(
            onClick = viewModel::addSubtask,
            enabled = state.subtasks.size < QuestValidator.MAX_SUBTASKS,
            modifier = Modifier.fillMaxWidth(),
          ) {
            Text(stringResource(R.string.action_add_subtask))
          }
        }
      }

      item {
        SectionLabel(stringResource(R.string.quest_penalty_label))
        ChipRow(
          options = PenaltyType.entries,
          selected = state.penaltyType,
          label = {
            when (it) {
              PenaltyType.None -> stringResource(R.string.quest_penalty_none)
              PenaltyType.Gold -> stringResource(R.string.quest_penalty_gold)
              PenaltyType.Xp -> stringResource(R.string.quest_penalty_xp)
              PenaltyType.Mark -> stringResource(R.string.quest_penalty_mark)
              PenaltyType.Streak -> stringResource(R.string.quest_penalty_streak)
            }
          },
          onSelect = viewModel::setPenaltyType,
        )
        if (state.penaltyType == PenaltyType.Gold || state.penaltyType == PenaltyType.Xp) {
          OutlinedTextField(
            value = state.penaltyAmountText,
            onValueChange = viewModel::setPenaltyAmount,
            label = { Text(stringResource(R.string.quest_penalty_amount_label)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
          )
        }
      }

      if (state.violations.isNotEmpty()) {
        item {
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            state.violations.forEach { violation ->
              Text(
                text = stringResource(violation.messageRes()),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
              )
            }
          }
        }
      }

      item {
        Button(
          onClick = viewModel::save,
          enabled = !state.saving,
          modifier = Modifier.fillMaxWidth(),
        ) {
          Text(stringResource(R.string.action_save))
        }
      }
    }
  }

  if (showDatePicker) {
    val pickerState = rememberDatePickerState(initialSelectedDateMillis = state.dueAt)
    DatePickerDialog(
      onDismissRequest = { showDatePicker = false },
      confirmButton = {
        TextButton(
          onClick = {
            pickerState.selectedDateMillis?.let { viewModel.setDueAt(datePickerMillisToLocalEndOfDay(it)) }
            showDatePicker = false
          }
        ) {
          Text(stringResource(R.string.action_save))
        }
      },
      dismissButton = {
        TextButton(onClick = { showDatePicker = false }) {
          Text(stringResource(R.string.action_cancel))
        }
      },
    ) {
      DatePicker(state = pickerState)
    }
  }
}

@Composable
private fun SectionLabel(text: String) {
  Text(
    text = text,
    style = MaterialTheme.typography.titleSmall,
    color = MaterialTheme.colorScheme.onSurfaceVariant,
    modifier = Modifier.padding(bottom = 8.dp),
  )
}

@Composable
private fun <T> ChipRow(
  options: List<T>,
  selected: T,
  label: @Composable (T) -> String,
  onSelect: (T) -> Unit,
) {
  Row(
    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    options.forEach { option ->
      FilterChip(
        selected = option == selected,
        onClick = { onSelect(option) },
        label = { Text(label(option)) },
      )
    }
  }
}
