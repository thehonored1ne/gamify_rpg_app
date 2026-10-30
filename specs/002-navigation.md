---
status: draft
created: 2026-10-30
updated: 2026-10-30
---

# Navigation Requirements

> Requirements only. Implementation notes go in `documentation/`.

## Screens and route keys

| Screen | Key | Arguments |
|---|---|---|
| Dashboard | `Dashboard` | none |
| Quest list | `Quests` | `filter: QuestFilter?` |
| Quest detail | `QuestDetail` | `questId: Long` |
| Quest create/edit | `QuestEdit` | `questId: Long?` (null = create) |
| Shop | `Shop` | none |
| Achievements | `Achievements` | none |
| Stats | `Stats` | none |
| Settings | `Settings` | none |

## Requirements
- R1: bottom navigation with four tabs — Dashboard, Quests, Shop, More.
- R2: More groups Achievements, Stats, Settings.
- R3: each tab keeps its own back stack; switching tabs preserves the other tabs' state.
- R4: detail and edit screens are pushed on top of the current tab.
- R5: back from detail returns to the list at the same scroll position.
- R6: every key is a `@Serializable` `NavKey`; ids are typed `Long`, never raw strings.
- R7: no deep links in v0.1. Adding them later must not change the key types.
- R8: after process death, a screen reloads its data from the database by id; nothing depends on in-memory objects.
- R9: a missing entity (deleted quest) pops back to the list instead of showing an empty detail screen.
- R10: unsaved edits in `QuestEdit` prompt a discard confirmation on back.

## Acceptance criteria
- [ ] back from `QuestDetail` returns to the list with scroll preserved
- [ ] rotating on `QuestDetail` keeps the same quest open
- [ ] deleting the open quest does not crash and returns to the list
- [ ] tab switching preserves each tab's stack
