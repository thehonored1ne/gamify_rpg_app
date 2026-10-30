package com.example.liferpg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.liferpg.ui.AppRoot
import com.example.liferpg.ui.theme.LifeRpgTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    enableEdgeToEdge()
    setContent { LifeRpgTheme { AppRoot() } }
  }
}
