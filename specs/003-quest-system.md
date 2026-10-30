---
status: draft
created: 2026-10-30
updated: 2026-10-30
---

# Quest System

> Requirements only. Implementation notes go in `documentation/`.

## Quest fields

| Field | Type | Required | Rules |
|---|---|---|---|
| `title` | String | yes | 1–80 characters, trimmed, non-blank |
| `description` | String | no | up to 500 characters |
| `difficulty` | Enum | yes | Common, Uncommon, Rare, Epic, Legendary |
| `category` | Enum | yes | Health, Study, Work, Personal, Creative, Social, Chores, Other |
| `xpReward` | Int | yes | ceil(0.5 × baseline)..(2 × tier baseline), see `004` |
| `goldReward` | Int | yes | 0..(2 × tier baseline), see `004` |
| `deadlineType` | Enum | yes | None, Date, Timer |
| `dueAt` | Long? | Date only | epoch millis, must be in the future at creation |
| `durationMinutes` | Int? | Timer only | 1..10080 (7 days) |
| `timerStartedAt` | Long? | Timer only | epoch millis, null until started |
| `reminderAt` | Long? | no | must be before `dueAt`, at least 1 minute after creation |
| `penaltyType` | Enum | yes | None, Gold, Xp, Streak, Mark |
| `penaltyAmount` | Int | conditional | 0 when type is None, otherwise within caps in `005` |
| `subtasks` | List | no | 0–20 items, each 1–80 characters |
| `status` | Enum | yes | Active, Completed, Failed, Cancelled |

## Requirements
- R1: rewards are auto-suggested from difficulty; the user may override within the caps.
- R2: a quest with `deadlineType = Date` requires `dueAt`; `Timer` requires `durationMinutes`; `None` requires neither.
- R3: the timer starts when the user taps Start, not at creation; `timerStartedAt` is stored so the countdown survives process death.
- R4: subtasks are independent checkboxes; completing all subtasks does not complete the quest.
- R5: completing a quest grants XP and gold exactly once, in a single transaction.
- R6: completion is undoable for 10 seconds while the app is running; the undo window is in-memory and does not survive process death. Undo reverses the XP and gold deltas and returns the quest to Active.
- R7: a quest that passes its deadline while Active becomes Failed, and its penalty is applied exactly once. Expiry is evaluated by a `WorkManager` job and on app open, so detection can be up to 15 minutes late.
- R8: cancelling a quest applies no penalty and grants nothing.
- R9: editing a completed quest is not allowed; editing an active quest re-validates the reward caps.
- R10: deleting a quest keeps its history events; the events keep the quest title as a snapshot.
- R11: list ordering: Active first by nearest deadline (no-deadline last), then Completed by most recent.

## Acceptance criteria
- [ ] creating each deadline type persists and restores correctly after process death
- [ ] completing twice (double tap) grants rewards once
- [ ] undo within 10 seconds restores XP, gold, and status
- [ ] an expired quest applies its penalty once, even across app restarts
- [ ] reward overrides outside the caps are rejected with a visible message
