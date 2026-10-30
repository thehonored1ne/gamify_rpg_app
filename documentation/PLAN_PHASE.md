# Implementation Plan — Life RPG

Status: active
Updated: 2026-10-30
Source of truth for requirements: `specs/001`–`016`

## Phase tracker

| Phase | Scope | Status | Gate |
|---|---|---|---|
| 0 | Setup: package rename, strip demo, add Room/DataStore/WorkManager | done | build + tests green, no `INTERNET` permission |
| 1 | Domain core: XP, level, ledger, penalties, streaks, offers | not started | unit tests cover `004`–`007` acceptance criteria |
| 2 | Persistence + quest CRUD screens | not started | quest survives process death |
| 3 | Core loop: complete, undo, fail, dashboard | not started | full loop works offline, rewards/penalties applied once |
| 4 | Shop, achievements, streak UI, settings basics | not started | balances equal ledger sum |
| 5 | JSON export and replace-mode import | not started | round-trip restores identical balances |
| 6 | System quests + 30-quest content library | not started | determinism and 7-day exclusion pass |
| 7 | Polish: animation, accessibility, performance | not started | `013` accessibility checklist passes |

- [x] Phase 0 — Setup
- [ ] Phase 1 — Domain core
- [ ] Phase 2 — Persistence and quest CRUD
- [ ] Phase 3 — Core loop
- [ ] Phase 4 — Progression surfaces
- [ ] Phase 5 — Backup
- [ ] Phase 6 — System quests
- [ ] Phase 7 — Polish

## Decisions

| Decision | Choice |
|---|---|
| Date/time API | `java.time` with core library desugaring (minSdk 24) |
| Gold sources | quests, system quests, achievement unlocks; never real money |
| Reminders | in v0.1 |
| Background work | `WorkManager` for reminders and expiry; no exact alarms |
| Timezone | `LocalDate.now(ZoneId.systemDefault())` at each event |
| Persistence | Room + DataStore; ledger-derived balances |
| DI | manual `AppContainer` |
| Package | `com.example.liferpg` |

## Phase 0 — Setup

- [ done ] Git init and baseline commit (owner: user)
- [x] Rename package to `com.example.liferpg` (namespace, applicationId, source dirs, imports)
- [x] Delete demo: `MainScreen`, `MainScreenViewModel`, `MainScreenUiState`, `DataRepository`, `DefaultDataRepository`, their tests
- [x] Remove Coil dependencies and the `INTERNET` permission
- [x] Add Room (runtime, ktx, compiler via KSP), DataStore, WorkManager, desugaring
- [x] Enable KSP plugin and Room schema export to `app/schemas`
- [x] Keep Navigation3, theme, version catalog, Gradle setup

Gate:
- `.\gradlew assembleDebug` and `.\gradlew test` pass — verified 2026-10-30
- manifest declares no `INTERNET` permission — verified with `aapt2 dump permissions`
- `app/schemas` is configured; the first schema file appears when the first `@Database` is added in Phase 2
- app installs and launches on device `15187705C5010595` — verified

## Phase 1 — Domain core

- [ ] `TimeProvider` interface and system implementation
- [ ] XP curve and level derivation (`004`)
- [ ] Gold ledger math and balance rules (`004`)
- [ ] Penalty caps and XP floor (`005`)
- [ ] Streak calculation with grace day and weekly streak (`007`)
- [ ] Deterministic offer generator (`006`)

Gate:
- unit tests assert every acceptance criterion in `004`, `005`, `006`, `007`
- `domain` package has no Android imports

## Phase 2 — Persistence and quest CRUD

- [ ] Room entities and DAOs exactly as `011`
- [ ] Repositories: quest, progress, shop, achievement, streak, system quest
- [ ] `AppContainer` wiring and ViewModel factories
- [ ] Screens: quest list, quest detail, quest create/edit
- [ ] Validation: title length, reward caps, deadline type rules, reminder ordering

Gate:
- create a quest, kill the app, reopen: quest and subtasks persist
- invalid rewards and missing deadline fields are rejected with a message
- deleting a quest leaves its events intact

## Phase 3 — Core loop

- [ ] Complete, undo (10s, in-memory), cancel, fail use cases in single transactions
- [ ] `WorkManager` job for deadline and timer expiry
- [ ] Dashboard: XP bar, level badge, gold chip, active quests, streak
- [ ] Level-up detection and state update

Gate:
- full loop works in airplane mode
- double-tap completion grants rewards once
- an expired quest applies its penalty once across restarts
- gold balance equals the ledger sum after any sequence

## Phase 4 — Progression surfaces

- [ ] Shop: static catalog, purchase, equip, level gates (`008`)
- [ ] Achievements: five from `007`, derived progress, unlock events
- [ ] Streak UI with grace indication
- [ ] Settings: theme, title, reduce motion, reset, about (`014`)

Gate:
- purchase and equip persist across restart
- achievement progress recalculates from the ledger
- reset clears every table and preference
- balance never goes negative

## Phase 5 — Backup

- [ ] Export JSON v1 via SAF with the schema in `012`
- [ ] Import with full validation, replace mode, single transaction
- [ ] Last export date and 30-day nudge in Settings
- [ ] Uninstall warning copy

Gate:
- export then import into a clean install restores identical balances
- malformed or newer-version files import nothing and report the first error
- failed import leaves existing data readable

## Phase 6 — System quests

- [ ] `assets/system_quests.json` with the 30 entries from `016`
- [ ] Offer generation wired to persistence and the 7-day exclusion
- [ ] Dashboard offer panel, accept/decline, locked state below level 2
- [ ] Rare offers gated at level 5

Gate:
- same date yields identical offers after restart
- no library quest repeats within 7 days
- accepting creates exactly one quest; declining creates none
- offers expire at local midnight

## Phase 7 — Polish

- [ ] Level-up overlay, XP bar animation, reduce-motion support
- [ ] Accessibility: TalkBack labels, 200% text, grayscale rarity check, 48dp targets
- [ ] Performance pass on a mid-range device
- [ ] Final `documentation/ARCHITECTURE.md` update to reflect the shipped app

Gate:
- `013` accessibility checklist fully passes
- no frame drops above 16ms during the level-up animation on the target device

## Working rules

- One phase per commit; update `documentation/CHANGELOG.md` under Unreleased at each phase.
- A phase is done only when its gate passes; do not start the next phase early.
- If implementation diverges from a spec, change the spec in the same commit.
- Run `.\gradlew test` before handing off any phase; run `installDebug` for UI phases.

## Risks

| Risk | Mitigation |
|---|---|
| Scope creep into chains, stats, effects | v0.1 boundaries in `015`; phases gate each other |
| Notification permission denied on Android 13+ | switch stays off, rationale shown; expiry still runs on app open |
| Timezone travel producing two "days" | accepted and documented in `012` R23 |
| Room migration mistakes | schema export, migrations required, no destructive fallback |
| Gamification decay after 3–4 weeks | content library expansion is a separate, post-v0.1 task |
