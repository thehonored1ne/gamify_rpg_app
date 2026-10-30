package com.example.liferpg.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Root of the app UI.
 *
 * Placeholder only: it gives the app a valid launch target after the template demo was removed.
 * The dashboard and the navigation shell replace it once the domain layer exists.
 */
@Composable
fun AppRoot() {
  Scaffold { innerPadding ->
    Column(
      modifier =
        Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 24.dp),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
        text = "Life RPG System",
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Center,
      )
      Text(
        text = "Offline. No accounts, no network.",
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 8.dp),
      )
    }
  }
}
