# Changelog

All notable changes to this project are documented in this file.

Format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).
Versioning follows [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Product requirements for the Life RPG app in `specs/`:
  - `001-architecture`, `002-navigation`, `003-quest-system`, `004-progression-economy`
  - `005-penalty-policy`, `006-system-quests`, `007-achievements-streaks`, `008-shop-cosmetics`
  - `011-data-layer`, `012-offline-cache`, `013-ui-design-system`, `014-settings`
  - `015-mvp-scope`, `016-content-library`
- `documentation/ARCHITECTURE.md` now separates the implemented demo from the specified target.
- `documentation/SETUP.md` lists the planned package rename, Room, and DataStore work.
- `documentation/PLAN_PHASE.md` — phased implementation plan with per-phase gates.

### Changed

- Penalty policy restricted to in-app effects only: gold, XP, streak, failed mark. Body, health, money, and social consequences are explicitly denied.
- Currency defined as a single in-app currency (gold) earned in-app only; no real-money purchases.
- Gold may also be awarded by achievement unlocks (`004` R6).
- XP penalty floor defined as the cumulative threshold of the current level (`005` R8).
- Quest expiry is evaluated by `WorkManager` and on app open, up to 15 minutes late (`003` R7).
- Day boundary rule added to `012`: local date at each event; timezone travel caveat documented.
- Undo window documented as in-memory and lost on process death (`003` R6).

### Removed

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
