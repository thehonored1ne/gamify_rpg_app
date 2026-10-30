package com.example.liferpg.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.liferpg.R
import com.example.liferpg.domain.model.DeadlineType
import com.example.liferpg.domain.model.Difficulty
import com.example.liferpg.domain.model.Quest
import com.example.liferpg.domain.model.QuestStatus
import com.example.liferpg.ui.format.formatDateTime

@Composable
fun QuestCard(quest: Quest, onClick: () -> Unit, modifier: Modifier = Modifier) {
  Card(
    onClick = onClick,
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
  ) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = quest.title,
          style = MaterialTheme.typography.titleMedium,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.weight(1f),
        )
        DifficultyBadge(quest.difficulty)
      }
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = stringResource(R.string.quest_reward_line, quest.xpReward, quest.goldReward),
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.weight(1f))
        Text(
          text = deadlineLabel(quest),
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
      if (quest.status != QuestStatus.Active) {
        Text(
          text = statusLabel(quest.status),
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.primary,
        )
      }
    }
  }
}

@Composable
fun DifficultyBadge(difficulty: Difficulty, modifier: Modifier = Modifier) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(4.dp),
    color = MaterialTheme.colorScheme.surface,
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
  ) {
    Text(
      text = difficulty.name,
      style = MaterialTheme.typography.labelSmall,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
    )
  }
}

@Composable
fun deadlineLabel(quest: Quest): String =
  when (quest.deadlineType) {
    DeadlineType.None -> stringResource(R.string.quest_no_deadline)
    DeadlineType.Date ->
      quest.dueAt?.let { stringResource(R.string.quest_deadline_line, formatDateTime(it)) }
        ?: stringResource(R.string.quest_no_deadline)
    DeadlineType.Timer ->
      if (quest.timerStartedAt == null) {
        stringResource(R.string.quest_timer_not_started)
      } else {
        stringResource(R.string.quest_timer_line, quest.durationMinutes ?: 0)
      }
  }

@Composable
fun statusLabel(status: QuestStatus): String =
  when (status) {
    QuestStatus.Active -> stringResource(R.string.quest_status_active)
    QuestStatus.Completed -> stringResource(R.string.quest_status_completed)
    QuestStatus.Failed -> stringResource(R.string.quest_status_failed)
    QuestStatus.Cancelled -> stringResource(R.string.quest_status_cancelled)
  }
