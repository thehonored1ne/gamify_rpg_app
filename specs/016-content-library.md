---
status: draft
created: 2026-10-30
updated: 2026-10-30
---

# System Quest Content Library

> Requirements only. Implementation notes go in `documentation/`.

## Format
- R1: the library is a bundled JSON asset: `app/src/main/assets/system_quests.json`.
- R2: `id` is stable, snake_case, prefixed `sys_`, and never reused after a rename.
- R3: `version` increments on any content change; the app accepts version >= 1.
- R4: `category` uses the quest category enum; `rarity` uses Common, Uncommon, Rare.
- R5: `tags` are free-form, lowercase, used for future filtering only.

```json
{
  "version": 1,
  "quests": [
    {
      "id": "sys_drink_water",
      "title": "Drink a glass of water",
      "description": "One glass, any size.",
      "category": "Health",
      "rarity": "Common",
      "tags": ["health", "quick"]
    }
  ]
}
```

## Content rules
- R6: universally achievable — no equipment, location, money, ability, or other people required.
- R7: no body, weight, diet, or appearance framing.
- R8: no task longer than 25 minutes.
- R9: no duplicate titles within the library.
- R10: at least 12 Common, 10 Uncommon, and 8 Rare entries before v0.1.1 ships.

## Initial 30

### Common (12)

| Id | Title | Category |
|---|---|---|
| `sys_drink_water` | Drink a glass of water | Health |
| `sys_stretch_1m` | Stretch for one minute | Health |
| `sys_breathe_5` | Take five deep breaths | Health |
| `sys_read_page` | Read one page | Study |
| `sys_gratitude` | Write down one thing you are grateful for | Personal |
| `sys_clear_surface` | Clear one small surface | Chores |
| `sys_item_home` | Put one item back in its place | Chores |
| `sys_learn_fact` | Learn one new fact | Study |
| `sys_walk_2m` | Stand up and walk for two minutes | Health |
| `sys_tidy_desk` | Tidy your desk for three minutes | Work |
| `sys_delete_files` | Delete five unused files | Work |
| `sys_plan_tomorrow` | Plan tomorrow's top task | Personal |

### Uncommon (10)

| Id | Title | Category |
|---|---|---|
| `sys_complete_user_quest` | Complete any one user-created quest | Personal |
| `sys_create_quest` | Create one new quest | Personal |
| `sys_read_5_pages` | Read five pages | Study |
| `sys_focus_10m` | Do a ten-minute focused work block | Work |
| `sys_organize_shelf` | Organize one drawer or shelf | Chores |
| `sys_walk_10m` | Walk for ten minutes | Health |
| `sys_journal_3` | Write a three-sentence journal entry | Personal |
| `sys_review_quests` | Review and clean your quest list | Personal |
| `sys_practice_10m` | Practice a skill for ten minutes | Creative |
| `sys_prepare_tomorrow` | Prepare one thing for tomorrow | Personal |

### Rare (8)

| Id | Title | Category |
|---|---|---|
| `sys_two_user_quests` | Complete two user-created quests today | Personal |
| `sys_focus_25m` | Do a 25-minute focused session | Work |
| `sys_finish_section` | Finish a chapter or a section | Study |
| `sys_clean_room` | Clean one full room | Chores |
| `sys_move_20m` | Walk or move for twenty minutes | Health |
| `sys_creative_piece` | Finish a small creative piece | Creative |
| `sys_all_subtasks` | Complete all subtasks of one quest | Personal |
| `sys_weekly_reflection` | Write five sentences on what worked this week | Personal |

## Acceptance criteria
- [ ] the asset parses and every entry passes the content rules
- [ ] ids are unique and stable
- [ ] no body or diet framing anywhere in the library
- [ ] counts meet R10
