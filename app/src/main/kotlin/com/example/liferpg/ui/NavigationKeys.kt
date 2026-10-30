package com.example.liferpg.ui

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object Quests : NavKey

@Serializable data class QuestDetail(val questId: Long) : NavKey

@Serializable data class QuestEdit(val questId: Long? = null) : NavKey
