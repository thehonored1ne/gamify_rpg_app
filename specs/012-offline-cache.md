---
status: draft
created: 2026-10-30
updated: 2026-10-30
---

# Offline and Backup

> Requirements only. Implementation notes go in `documentation/`.

## Offline
- R1: the app declares no `INTERNET` permission and ships no network client.
- R2: no accounts, no sync, no telemetry, no crash reporting service.
- R3: every feature works with the device in airplane mode.
- R4: `android:allowBackup` is `false`; the privacy claim must match the manifest.

## Export
- R5: export writes a single JSON file through the system file picker (SAF).
- R6: default file name `liferpg-backup-yyyyMMdd-HHmm.json`.
- R7: export is available from Settings and is never triggered automatically.

Schema:

```json
{
  "version": 1,
  "exportedAt": 1761782400000,
  "appVersion": "0.1.0",
  "data": {
    "quests": [],
    "subtasks": [],
    "events": [],
    "systemOffers": [],
    "achievements": [],
    "ownedItems": [],
    "graceDates": [],
    "settings": {}
  }
}
```

- R8: `version` is mandatory and monotonic. Unknown newer versions are rejected with a clear message.
- R9: export includes the ledger, so balances can be reconstructed exactly.

## Import
- R10: import reads a file through SAF and validates the entire document before writing anything.
- R11: validation covers version, required fields, enum values, foreign keys, and non-negative caps.
- R12: a failed validation leaves the database untouched and reports the first problem.
- R13: a successful import runs in one transaction.
- R14: default mode is Replace: the current data set is cleared and replaced.
- R15: Merge mode is optional in v0.2: quests by id keep the newer `updatedAt`, events are appended by id, achievements and owned items are unioned, settings keep current values.
- R16: after import, derived balances and achievements are recomputed from the ledger.

## Data safety
- R17: uninstalling or clearing app data deletes everything permanently; Settings states this.
- R18: Settings shows the date of the last export and nudges when it is older than 30 days.
- R19: export files are plain JSON containing personal task titles; the export dialog warns about this.
- R20: optional passphrase encryption is deferred to a later version and must not be advertised before it exists.

## Day boundary
- R21: "today" is `LocalDate.now(ZoneId.systemDefault())`, evaluated at each event; no day value is cached across sessions.
- R22: system offers, streaks, and the daily grace check all use this same definition.
- R23: traveling across timezones can make two consecutive local dates count as two days; this is accepted and not corrected.

## Acceptance criteria
- [ ] airplane mode: every screen and action works
- [ ] exported file re-imports into a clean install with identical balances
- [ ] a truncated or malformed file imports nothing and shows an error
- [ ] importing a newer schema version is rejected
- [ ] a failed import leaves existing data readable
- [ ] manifest has no `INTERNET` permission and `allowBackup="false"`
