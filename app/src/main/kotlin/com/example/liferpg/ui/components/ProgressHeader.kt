package com.example.liferpg.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.liferpg.R
import com.example.liferpg.domain.progression.XpCurve
import com.example.liferpg.domain.repository.Progress

@Composable
fun ProgressHeader(progress: Progress, modifier: Modifier = Modifier) {
  val levelProgress = XpCurve.progress(progress.totalXp)
  Column(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(
        text = stringResource(R.string.level_label, levelProgress.level),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
      )
      Spacer(Modifier.weight(1f))
      GoldChip(progress.gold)
    }
    Spacer(Modifier.height(8.dp))
    LinearProgressIndicator(
      progress = { levelProgress.fraction },
      modifier = Modifier.fillMaxWidth().height(8.dp),
      trackColor = MaterialTheme.colorScheme.surfaceVariant,
    )
    Spacer(Modifier.height(4.dp))
    Text(
      text =
        if (levelProgress.isMaxLevel) {
          stringResource(R.string.xp_progress_max)
        } else {
          stringResource(R.string.xp_progress_label, levelProgress.xpIntoLevel, levelProgress.xpForNext)
        },
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@Composable
fun GoldChip(gold: Long, modifier: Modifier = Modifier) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(4.dp),
    color = MaterialTheme.colorScheme.surfaceVariant,
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(4.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Surface(
        modifier = Modifier.width(8.dp).height(8.dp),
        shape = RoundedCornerShape(2.dp),
        color = MaterialTheme.colorScheme.tertiary,
      ) {}
      Text(
        text = stringResource(R.string.gold_label, gold),
        style = MaterialTheme.typography.labelMedium,
      )
    }
  }
}
