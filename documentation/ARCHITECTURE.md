# Architecture

Current truth of the repository. Designs for work not yet built live in `specs/`.

## Status

| Area | State |
|---|---|
| Gradle foundation (Room, DataStore, WorkManager, serialization, desugaring) | Implemented |
| App shell (`MainActivity` → `AppRoot` → `NavDisplay`) | Implemented |
| Domain core (XP, levels, ledger, penalties, streaks, offers) | Implemented — Phase 1 |
| Persistence and quest CRUD (list, detail, create/edit) | Implemented — Phase 2 |
| Product requirements (`specs/003`–`016`) | Partially implemented |
| Target architecture (`specs/001`) | Implemented for `core`, `domain`, `data`, `ui/quests` |

## Current implementation

Single module `:app`, package `com.example.liferpg`, applicationId `com.example.liferpg`.

| Path | Responsibility |
|---|---|
| `com.example.liferpg` | `MainActivity`, `LifeRpgApplication` |
| `com.example.liferpg.di` | `AppContainer` — manual DI: database, repositories, `TimeProvider` |
| `com.example.liferpg.core.time` | `TimeProvider`, `SystemTimeProvider` |
| `com.example.liferpg.domain.model` | `Quest`, `QuestDraft`, `Subtask`, `QuestEvent`, `Completion`, enums, `QuestViolation` |
| `com.example.liferpg.domain.progression` | `XpCurve`, `RewardRules` |
| `com.example.liferpg.domain.penalty` | `PenaltyRules` |
| `com.example.liferpg.domain.streak` | `StreakCalculator` (daily, grace day, weekly) |
| `com.example.liferpg.domain.quest` | `QuestValidator`, `QuestOrder` |
| `com.example.liferpg.domain.system` | `OfferGenerator` (deterministic daily offers) |
| `com.example.liferpg.domain.repository` | `QuestRepository`, `ProgressRepository`, `Progress` |
| `com.example.liferpg.data.db` | Room entities, DAOs, `LifeRpgDatabase` v1, mappers |
| `com.example.liferpg.data.repository` | `DefaultQuestRepository`, `DefaultProgressRepository` |
| `com.example.liferpg.ui` | `AppRoot`, `NavigationKeys`, violation message mapping |
| `com.example.liferpg.ui.quests` | Quest list, detail, edit screens and ViewModels |
| `com.example.liferpg.ui.components` | `QuestCard`, `DifficultyBadge`, `ProgressHeader`, `GoldChip` |
| `com.example.liferpg.ui.format` | date/time formatting, date-picker conversion |
| `com.example.liferpg.ui.theme` | `LifeRpgTheme`, color schemes, typography |

Startup flow:

```
MainActivity.onCreate
  └─ enableEdgeToEdge()
  └─ LifeRpgApplication.container (AppContainer)
  └─ LifeRpgTheme { AppRoot(container) }
        └─ NavDisplay(backStack = [Quests])
```

Navigation3 routes: `Quests`, `QuestDetail(questId)`, `QuestEdit(questId?)`. Bottom tabs, dashboard, shop, achievements, and settings are Phase 3–4.

Build: Gradle 9.1.0, AGP 9.0.1, Kotlin 2.3.20, Compose BOM 2026.03.01, compileSdk/targetSdk 36, minSdk 24, JVM 17, configuration cache on.

Dependencies in use: Room 2.8.5 (KSP), kotlinx-coroutines, Navigation3 1.0.1, Material 3, `material-icons-core`. Present but unused until later phases: DataStore 1.3.0, WorkManager 2.12.0, kotlinx-serialization-json 1.12.0.

Manifest: no `INTERNET` permission, `allowBackup="false"`. WorkManager's manifest merge adds `WAKE_LOCK`, `ACCESS_NETWORK_STATE`, `RECEIVE_BOOT_COMPLETED`, and `FOREGROUND_SERVICE`; none of them grant network access, and the app opens no sockets.

## Target architecture (Life RPG)

Planned package layout, per `specs/001-architecture.md`:

| Package | Contents |
|---|---|
| `core/` | `TimeProvider`, result types, id generation, formatting |
| `domain/` | pure Kotlin: XP curve, level derivation, gold ledger, streak math, penalty rules, system quest generation |
| `data/` | Room entities and DAOs, repositories, DataStore |
| `ui/<feature>/` | dashboard, quests, shop, achievements, stats, settings |

Rules that govern the implementation:

- `domain` has no Android imports and is JVM-testable.
- UI depends on domain and repository interfaces; only ViewModels touch repositories.
- Balances and level are derived from an append-only `quest_events` ledger, never stored as writable fields.
- Room + DataStore; schema exported and committed; no destructive migration fallback.
- Manual `AppContainer` for DI; no framework.
- Offline only: no `INTERNET` permission, no analytics, no accounts.

Data flow:

```
UI event → ViewModel → repository (Room transaction + ledger row) → StateFlow<UiState> → UI
```

## Phase 1 — domain core

`domain/` is pure Kotlin. Decisions worth knowing:

- `XpCurve` derives the level from total XP; nothing stores a level. `cumulativeXpForLevel(n) = 50n + 25·n(n−1)/2`, cap 100.
- `RewardRules` enforces `ceil(0.5 × baseline)..2 × baseline` for XP and `0..2 × baseline` for gold.
- `PenaltyRules` clamps gold at 0 and XP at the current level's cumulative floor, so a penalty never demotes.
- `StreakCalculator` takes the set of days with completions plus consumed grace days; one grace day per rolling seven.
- `OfferGenerator` scores library entries with an FNV-1a hash of `date|rarity|id`, so selection is deterministic and independent of JVM hash internals.

## Phase 2 — persistence and quest CRUD

- `LifeRpgDatabase` v1 holds `quests`, `subtasks`, `quest_events`, `system_offers`, `unlocked_achievements`, `owned_items`, `streak_grace`. Schema exported to `app/schemas/.../1.json`.
- `DefaultQuestRepository` owns every transaction that changes quest state and the ledger: create, update, delete, complete, undo, cancel, start timer, fail expired.
- Undo is an `Undone` event with negated deltas, allowed for `UNDO_WINDOW_MS` (10 s) after completion. The window is evaluated from the stored `completedAt`, so a restart cannot reopen it.
- Deleting a quest detaches its events (`questId = NULL`) but keeps `questTitle`; history survives.
- Editing subtasks reconciles by position: existing rows keep `isDone`, extra rows are deleted, new rows are appended.
- Screens: quest list with progress header, quest detail, create/edit form with difficulty and category chips, reward fields, deadline type (none/date/timer), penalty, and subtask rows. Validation failures render as field-level messages from `QuestViolation`.

## Migration notes

Done in Phase 0:

- Package moved from `com.example.myfastapp` to `com.example.liferpg`; source tree and resources renamed.
- Demo removed: `MainScreen`, `MainScreenViewModel`, `MainScreenUiState`, `DataRepository`, `DefaultDataRepository`, `MainNavigation`, `NavigationKeys`, and both test classes.
- Coil dependencies and the `INTERNET` permission removed.
- `allowBackup="false"`; the backup/data-extraction rule XML files were deleted because nothing is backed up.
- Theme, Navigation3 dependency, version catalog, and Gradle setup carry over.

Still to come:

- Bottom navigation with Dashboard, Quests, Shop, More (`002`) — Phase 3–4.
- `WorkManager` expiry and reminders — Phase 3.
- DataStore-backed settings — Phase 4.

## Testing

- Domain unit tests: 49 tests across `XpCurve`, `RewardRules`, `PenaltyRules`, `StreakCalculator`, `QuestValidator`, `QuestOrder`, `OfferGenerator`. Run with `.\gradlew testDebugUnitTest`.
- No Room or Compose tests yet; repositories are verified on device. Room `room-testing`, Robolectric, and Turbine are already on the classpath for Phase 3+.
- Device verification (2026-10-30, `15187705C5010595`): quest and subtasks survive process death; invalid XP is rejected with a message; deleting a quest keeps its event with `questId = NULL`; UI balances match `SUM(xpDelta)` / `SUM(goldDelta)`.

## Known constraints

- `compileSdk` must stay 36: AGP 9.0.1 has no `android-37` target.
- App module plugins must be applied, not declared with `apply false`.
- Version catalog aliases must not be prefixes of one another.
- Room 2.8.5 requires KSP2; do not add kapt.
