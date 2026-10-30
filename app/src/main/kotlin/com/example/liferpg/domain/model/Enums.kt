package com.example.liferpg.domain.model

/** Quest difficulty. Baselines come from `specs/004-progression-economy.md`. */
enum class Difficulty(val xpBaseline: Int, val goldBaseline: Int) {
  Common(5, 2),
  Uncommon(10, 5),
  Rare(20, 10),
  Epic(40, 20),
  Legendary(80, 40),
}

enum class Category {
  Health,
  Study,
  Work,
  Personal,
  Creative,
  Social,
  Chores,
  Other,
}

enum class DeadlineType {
  None,
  Date,
  Timer,
}

enum class PenaltyType {
  None,
  Gold,
  Xp,
  Streak,
  Mark,
}

enum class QuestStatus {
  Active,
  Completed,
  Failed,
  Cancelled,
}

enum class QuestSource {
  User,
  System,
}

enum class QuestEventType {
  Completed,
  Undone,
  Failed,
  PenaltyApplied,
  Purchased,
  LevelUp,
  AchievementUnlocked,
  OfferAccepted,
}

enum class OfferStatus {
  Offered,
  Accepted,
  Declined,
  Expired,
}
