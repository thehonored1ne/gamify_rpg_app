---
status: draft
created: 2026-10-30
updated: 2026-10-30
---

# Architecture Requirements

> Requirements only. Implementation notes go in `documentation/`.

## Purpose
Define the layer boundaries and dependency rules for the Life RPG app.

## Package layout
- R1: single Gradle module `:app` for v0.1. Split into modules only if clean builds exceed 60 seconds.
- R2: package by feature, not by type:
  - `core/` — result types, `TimeProvider`, id generation, formatting
  - `domain/` — pure Kotlin: XP curve, level derivation, gold ledger, streak math, penalty rules, system quest generation
  - `data/` — Room entities, DAOs, repositories, DataStore
  - `ui/<feature>/` — dashboard, quests, shop, achievements, stats, settings
- R3: `domain` must not import `android.*` or `androidx.*`. It must run in JVM unit tests.

## Dependency direction
- R4: `ui` → `domain` and repository interfaces; `data` → `domain` models; `domain` → nothing.
- R5: composables never touch repositories; only ViewModels do.

## State
- R6: one ViewModel per screen, exposing `StateFlow<UiState>`.
- R7: every `UiState` is a sealed interface with at least `Loading`, `Success`, `Error`.
- R8: no mutable state inside composables; state is hoisted and passed down.

## Dependency injection
- R9: manual `AppContainer` created in `Application`, passed to ViewModel factories.
- R10: no DI framework until the container exceeds ~20 entries.

## Concurrency and time
- R11: Room and DataStore work on `Dispatchers.IO`; domain functions are pure and synchronous.
- R12: all "now" values come from a `TimeProvider` interface so tests can pin the date.

## Errors
- R13: repositories return `Result<T>` or throw typed exceptions; ViewModels map failures to `UiState.Error`.
- R14: no empty catch blocks; every caught error is either mapped or rethrown.
- R15: user-facing messages come from `strings.xml`.

## Constraints
- R16: offline only. No `INTERNET` permission, no analytics SDK, no remote config.
- R17: minSdk 24, compileSdk/targetSdk 36, JVM 17.
- R18: every dependency declared in `gradle/libs.versions.toml`.

## Acceptance criteria
- [ ] `domain` package compiles with no Android imports
- [ ] each screen has exactly one ViewModel and one `UiState`
- [ ] `.\gradlew test` runs domain tests without a device
- [ ] manifest declares no `INTERNET` permission
