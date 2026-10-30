---
status: draft
created: 2026-10-30
updated: 2026-10-30
---

# Data Layer

> Requirements only. Implementation notes go in `documentation/`.

## Storage
- R1: Room (SQLite) for structured data; DataStore Preferences for settings.
- R2: schema export is enabled; the exported schema is committed.
- R3: no `fallbackToDestructiveMigration` in release builds. Every version bump ships a migration.
- R4: all writes that change balances are single transactions.

## Entities

### `quests`
| Column | Type | Notes |
|---|---|---|
| `id` | Long PK | auto |
| `title` | String | snapshot, kept after delete |
| `description` | String | |
| `difficulty` | String | enum name |
| `category` | String | enum name |
| `xpReward` | Int | |
| `goldReward` | Int | |
| `deadlineType` | String | None, Date, Timer |
| `dueAt` | Long? | |
| `durationMinutes` | Int? | |
| `timerStartedAt` | Long? | |
| `reminderAt` | Long? | |
| `penaltyType` | String | None, Gold, Xp, Streak, Mark |
| `penaltyAmount` | Int | |
| `status` | String | Active, Completed, Failed, Cancelled |
| `source` | String | User, System |
| `systemQuestId` | String? | library id |
| `createdAt` / `updatedAt` | Long | |
| `completedAt` / `failedAt` | Long? | |

### `subtasks`
`id` PK, `questId` FK (CASCADE), `title`, `isDone` Bool, `position` Int.

### `quest_events` (append-only ledger)
| Column | Type | Notes |
|---|---|---|
| `id` | Long PK | |
| `questId` | Long? | null after quest deletion |
| `questTitle` | String? | snapshot |
| `type` | String | Completed, Undone, Failed, PenaltyApplied, Purchased, LevelUp, AchievementUnlocked, OfferAccepted |
| `xpDelta` | Int | signed |
| `goldDelta` | Int | signed |
| `occurredAt` | Long | |
| `note` | String? | |

### `system_offers`
`id` PK, `date` String (`yyyy-MM-dd`), `libraryQuestId`, `rarity`, `status` (Offered, Accepted, Declined, Expired), `createdAt`. Unique index on `(date, rarity)`.

### `unlocked_achievements`
`achievementId` String PK, `unlockedAt` Long.

### `owned_items`
`itemId` String PK, `purchasedAt` Long.

### `streak_grace`
`date` String PK — one row per consumed grace day.

## Indices
- `quests(status, dueAt)`
- `quest_events(occurredAt)`, `quest_events(questId)`
- `system_offers(date)`

## Repositories
- R5: `QuestRepository` — CRUD, complete, undo, fail, list with filters.
- R6: `ProgressRepository` — total XP, gold balance, level, ledger queries.
- R7: `SystemQuestRepository` — today's offers, accept, decline.
- R8: `AchievementRepository` — derived progress, unlocks.
- R9: `ShopRepository` — catalog merge, purchase, equip.
- R10: `StreakRepository` — daily and weekly streaks, grace usage.

## Derived values
- R11: total XP = `SUM(xpDelta)`; gold = `SUM(goldDelta)`; level = `levelFor(totalXp)`.
- R12: no table stores a balance or level. Only the ledger and quest state are written.

## DataStore keys
`notificationsEnabled`, `reminderLeadMinutes`, `themeId`, `titleId`, `effectEnabled`, `reduceMotion`, `lastExportAt`, `firstRunCompleted`.

## Acceptance criteria
- [ ] a fresh install creates the schema with no destructive fallback
- [ ] completing a quest writes the status change and the ledger row atomically
- [ ] balances match the ledger sum after import
- [ ] deleting a quest leaves its events intact
- [ ] schema export exists and matches the entities above
