---
status: draft
created: 2026-10-30
updated: 2026-10-30
---

# Settings

> Requirements only. Implementation notes go in `documentation/`.

## Rows

| Row | Type | Default | Notes |
|---|---|---|---|
| Notifications | Switch | off | requires `POST_NOTIFICATIONS` on Android 13+ |
| Reminder lead time | Choice (5/10/15/30 min) | 15 | disabled when notifications are off |
| Theme | Choice | Default System | owned themes only |
| Title | Choice | Novice | owned titles only |
| UI effects | Switch | off | requires level 5 and ownership |
| Reduce motion | Switch | follows system | |
| Export data | Action | — | SAF create document |
| Import data | Action | — | SAF open document, confirmation first |
| Reset data | Action | — | double confirmation |
| About | Screen/section | — | version, offline statement |

## Requirements
- R1: settings persist in DataStore and survive restart.
- R2: changing a theme or title applies immediately, without restarting the app.
- R3: enabling notifications requests the runtime permission; if denied, the switch stays off and a rationale is shown.
- R4: import shows a confirmation that current data will be replaced, then reports a summary (quests, events, achievements).
- R5: reset requires two confirmations and deletes the database and all DataStore keys.
- R6: after reset the app returns to first-run state with no quests and level 1.
- R7: About states: offline, no account, no analytics, data stored on device only.
- R8: the last export date is displayed; a nudge appears when it is older than 30 days.
- R9: no setting enables network access; none exists.

## Acceptance criteria
- [ ] theme change is visible immediately and after restart
- [ ] denied notification permission leaves the switch off with an explanation
- [ ] reset clears every table and preference
- [ ] import summary counts match the imported file
- [ ] About contains no claim about encryption
