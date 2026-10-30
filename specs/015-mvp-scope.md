---
status: draft
created: 2026-10-30
updated: 2026-10-30
---

# MVP Scope and Release Gates

> Requirements only. Implementation notes go in `documentation/`.

## Principle
Ship the loop before the content. Each release must be usable on its own; nothing ships half-wired.

## v0.1 — Core loop (MVP)

In scope:
- Quest create, edit, delete: title, description, difficulty, category, XP, gold, deadline (None/Date/Timer), reminder.
- Complete, undo (10s), fail on expiry, cancel.
- Subtasks with checkboxes.
- XP curve, levels, gold balance, ledger events.
- Penalties: Gold, XP, Mark. (Streak penalty deferred.)
- Shop: one theme (`theme_shadow`), two titles (`title_novice`, `title_hunter`).
- Achievements: `first_quest`, `ten_quests`, `level_5`, `streak_7`, `perfect_day`.
- Daily streak with one grace day per 7.
- JSON export and replace-mode import.
- Settings: theme, title, reduce motion, export, import, reset, about.
- Dashboard, quest list, detail, edit, shop, achievements, settings screens.

Out of scope for v0.1:
- System daily quests, stats screen, quest chains, UI effects, merge import, reminders requiring exact alarms, widgets, sounds, localization beyond English.

Gate:
- [ ] a new user can create a quest, complete it, gain XP and gold, and level up without leaving the app
- [ ] export then import into a clean install restores identical balances
- [ ] no `INTERNET` permission in the manifest
- [ ] `.\gradlew test` passes; domain coverage of XP, gold, streak, penalty rules

## v0.1.1 — System quests

- Library of at least 30 quests (see `016`).
- Deterministic daily offers, 7-day exclusion, accept/decline, expiry.
- Level gates: Common/Uncommon at 2, Rare at 5.
- Dashboard panel for offers and for the locked state.

Gate:
- [ ] same date yields the same offers after restart
- [ ] no repeat within 7 days
- [ ] accepting creates exactly one quest

## v0.2 — Depth

- Stats screen derived from the ledger.
- Streak penalty type, remaining achievements.
- UI effect item, merge import.
- More titles and themes.

Gate:
- [ ] stats match the ledger for a seeded data set
- [ ] merge import does not duplicate events

## v0.3 — Chains

- Linear quest chains with cumulative rewards and a completion bonus.
- Chain achievement.
- Branching chains remain out of scope.

Gate:
- [ ] a chain cannot complete out of order
- [ ] chain bonus is granted once

## v0.4 — Polish

- Level-up overlay, XP bar animation, sound toggle (off by default).
- Accessibility pass: TalkBack, 200% text scale, grayscale rarity check.
- Performance pass on a mid-range device.

Gate:
- [ ] no frame drops above 16ms during the level-up animation on the target device
- [ ] accessibility checklist in `013` fully passes

## Non-goals (all releases)

- Cloud sync, accounts, social features, leaderboards.
- Real-money purchases or in-game currency bought with money.
- AI-generated quests.
- Analytics or crash reporting.
