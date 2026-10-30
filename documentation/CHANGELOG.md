# Changelog

All notable changes to this project are documented in this file.

Format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).
Versioning follows [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Phase 1 domain core (pure Kotlin, no Android imports):
  - `TimeProvider` / `SystemTimeProvider`; `XpCurve` with the `50 + 25(n−1)` curve and level cap 100.
  - `RewardRules` for the 50%–200% reward bounds; `PenaltyRules` with the gold-zero clamp and XP level floor.
  - `StreakCalculator` for daily streaks, the one-per-seven-days grace day, and weekly streaks.
  - `QuestValidator` and `QuestOrder`; deterministic `OfferGenerator` using an FNV-1a seed of `date|rarity|id`.
  - 49 JVM unit tests covering `004`–`007`.
- Phase 2 persistence and quest CRUD:
  - Room `LifeRpgDatabase` v1 with `quests`, `subtasks`, `quest_events`, `system_offers`, `unlocked_achievements`, `owned_items`, `streak_grace`; schema exported to `app/schemas`.
  - `DefaultQuestRepository` (create, update, delete, complete, undo, cancel, start timer, fail expired, penalties) and `DefaultProgressRepository`.
  - `AppContainer` manual DI and `LifeRpgApplication`; ViewModel factories per screen.
  - Screens: quest list with XP/gold header, quest detail, create/edit form with difficulty and category chips, reward fields, deadline types, penalty, and subtask rows.
  - `QuestViolation` → `strings.xml` mapping so validation errors render as readable messages.
- `material-icons-core` for the add, back, edit, and delete icons.
- Phase 0 foundation: Room 2.8.5 (runtime, ktx, compiler via KSP), DataStore 1.3.0, WorkManager 2.12.0, kotlinx-serialization-json 1.12.0, `desugar_jdk_libs` 2.1.5.
- Test dependencies: Robolectric 4.17, Turbine 1.2.1, `room-testing`, `work-testing`.
- Room schema export configured to `app/schemas`; KSP and Room Gradle plugins applied.
- Core library desugaring enabled so `java.time` works on minSdk 24.
- `AppRoot` placeholder screen so the app still launches after the demo removal.
- Product requirements for the Life RPG app in `specs/`:
  - `001-architecture`, `002-navigation`, `003-quest-system`, `004-progression-economy`
  - `005-penalty-policy`, `006-system-quests`, `007-achievements-streaks`, `008-shop-cosmetics`
  - `011-data-layer`, `012-offline-cache`, `013-ui-design-system`, `014-settings`
  - `015-mvp-scope`, `016-content-library`
- `documentation/ARCHITECTURE.md` now separates the implemented demo from the specified target.
- `documentation/SETUP.md` lists the planned package rename, Room, and DataStore work.
- `documentation/PLAN_PHASE.md` — phased implementation plan with per-phase gates.

### Changed

- Package renamed `com.example.myfastapp` → `com.example.liferpg` (namespace, applicationId, source tree, theme, resources).
- App label is now "Life RPG System"; root Gradle project renamed to `LifeRPG`.
- Compose theme renamed `MyFastAppTheme` → `LifeRpgTheme`.
- `android:allowBackup` set to `false`.
- Penalty policy restricted to in-app effects only: gold, XP, streak, failed mark. Body, health, money, and social consequences are explicitly denied.
- Currency defined as a single in-app currency (gold) earned in-app only; no real-money purchases.
- Gold may also be awarded by achievement unlocks (`004` R6).
- XP penalty floor defined as the cumulative threshold of the current level (`005` R8).
- Quest expiry is evaluated by `WorkManager` and on app open, up to 15 minutes late (`003` R7).
- Day boundary rule added to `012`: local date at each event; timezone travel caveat documented.
- Undo window documented as in-memory and lost on process death (`003` R6).

### Removed

- Demo UI and data: `MainScreen`, `MainScreenViewModel`, `MainScreenUiState`, `DataRepository`, `DefaultDataRepository`, `MainNavigation`, `NavigationKeys`, `MainScreenViewModelTest`, `MainScreenTest`.
- Coil dependencies (`io.coil-kt:coil`, `io.coil-kt:coil-compose`) and their catalog entries.
- `INTERNET` permission from `AndroidManifest.xml`.
- `res/xml/backup_rules.xml` and `res/xml/data_extraction_rules.xml` (backup disabled).
- `specs/013-theming.md`, replaced by `specs/013-ui-design-system.md`.

## [1.0.0] - 2026-10-30

### Added

- Compose app scaffold with a single `Main` destination rendered through Navigation3 (`NavDisplay`).
- `MainScreen` with a centered top app bar, a greeting list, and an OK button.
- `DataRepository` interface plus `DefaultDataRepository`, emitting a static list through a `Flow`.
- `MainScreenViewModel` exposing `MainScreenUiState` (`Loading` / `Success` / `Error`) as a `StateFlow` via `stateIn`.
- Coil 2.5.0 (`io.coil-kt:coil`, `io.coil-kt:coil-compose`) for image loading.
- `AsyncImage` demo in `MainScreen`, loading a remote image into a 200dp rounded crop.
- `INTERNET` permission in `AndroidManifest.xml`.
- Version catalog (`gradle/libs.versions.toml`) holding every dependency.
- Unit test `MainScreenViewModelTest` and instrumented test `MainScreenTest`.
- Agent rules in `.agents/rules/`: `follow`, `response`, `security`, `testing`, `workflow`.
- Spec scaffolds in `specs/` (`000`–`014`).

### Changed

- `compileSdk` set to 36 (was 37) because AGP 9.0.1 ships no `android-37` platform target.
- App module now applies its plugins (`alias(...)`) instead of declaring them with `apply false`.

### Fixed

- Build failure caused by `apply false` in the app module's `plugins` block, which left the `android { }` extension unresolved.
- Unresolved `libs.androidx.material3` corrected to `libs.androidx.compose.material3`.
- Coil catalog alias renamed `coil` to `coil-core` to avoid the alias-prefix conflict with `coil-compose`.
