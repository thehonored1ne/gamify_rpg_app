---
status: draft
created: 2026-10-30
updated: 2026-10-30
---

# Achievements and Streaks

> Requirements only. Implementation notes go in `documentation/`.

## Achievements
- R1: achievement definitions are static content; only the unlock timestamp is stored.
- R2: progress is derived from the event ledger, not stored as a counter.
- R3: an achievement unlocks at most once and never relocks.
- R4: unlocking awards gold only; no XP.

Initial set:

| Id | Name | Criteria | Gold |
|---|---|---|---|
| `first_quest` | First Blood | complete 1 quest | 5 |
| `ten_quests` | Getting Started | complete 10 quests | 10 |
| `hundred_quests` | Consistent | complete 100 quests | 25 |
| `level_5` | Awakened | reach level 5 | 15 |
| `level_10` | Rising | reach level 10 | 30 |
| `streak_7` | One Week | 7-day streak | 15 |
| `streak_30` | One Month | 30-day streak | 50 |
| `rare_10` | Rare Hunter | complete 10 Rare quests | 20 |
| `perfect_day` | Perfect Day | complete every active quest on a day with at least 3 | 10 |
| `system_10` | System Ally | complete 10 system quests | 15 |

## Daily streak
- R5: the streak counts consecutive local calendar days with at least one completion event.
- R6: the day boundary is local midnight from the device timezone; DST transitions use the calendar date, not elapsed hours.
- R7: one automatic grace day per rolling 7 days: a missed day consumes the grace and keeps the streak.
- R8: a second missed day without grace resets the streak to 0.
- R9: a streak penalty on a failed quest resets the streak immediately and consumes no grace.
- R10: grace usage is stored per date so it cannot be reused after a restart.

## Weekly streak
- R11: a week counts when at least 5 distinct days contain a completion.
- R12: the weekly streak counts consecutive qualifying weeks; it resets on the first non-qualifying week.

## Presentation
- R13: locked achievements show their criteria and current progress.
- R14: streak loss is shown neutrally ("streak reset"), never with shaming copy.
- R15: streak state is visible on the dashboard.

## Acceptance criteria
- [ ] completing the first quest unlocks `first_quest` exactly once
- [ ] achievement progress recalculates from the ledger after import
- [ ] a 6-day streak survives one missed day with grace available
- [ ] a second missed day resets the streak
- [ ] a streak penalty resets immediately and does not consume grace
- [ ] weekly streak requires 5 distinct active days
