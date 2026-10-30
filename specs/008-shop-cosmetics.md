---
status: draft
created: 2026-10-30
updated: 2026-10-30
---

# Shop and Cosmetics

> Requirements only. Implementation notes go in `documentation/`.

## Rules
- R1: all prices are in gold earned from quests. No real-money purchases, no billing library.
- R2: items are cosmetic only. Nothing in the shop affects XP, gold earn rate, or quest limits.
- R3: the catalog is static content; the database stores ownership and the equipped selection.
- R4: purchases are one-time and permanent; no consumables in v0.1.
- R5: no refunds; the purchase confirmation states this.
- R6: items cannot be bought twice; owned items show as Owned.
- R7: level gates are enforced at purchase and at equip time.

## Catalog

| Id | Type | Name | Price | Gate |
|---|---|---|---|---|
| `theme_system` | Theme | Default System | 0 (owned) | — |
| `theme_shadow` | Theme | Shadow | 60 | — |
| `theme_frost` | Theme | Frost | 120 | — |
| `title_novice` | Title | Novice | 0 (owned) | — |
| `title_hunter` | Title | Hunter | 25 | 2 |
| `title_monarch` | Title | Monarch | 50 | 5 |
| `effect_glow` | UI effect | Accent Glow | 150 | 5 |

## Behaviour
- R8: equipping a theme changes color tokens app-wide immediately, without restart.
- R9: exactly one theme and one title are equipped at a time; effects may be toggled on or off.
- R10: the equipped title is shown on the dashboard next to the level.
- R11: a purchase and its ledger event are written in one transaction.
- R12: if the balance is insufficient, the button is disabled and shows the missing amount.

## Acceptance criteria
- [ ] buying an item deducts exactly its price
- [ ] buying twice is impossible
- [ ] equipping a theme applies immediately and survives restart
- [ ] an item above the level gate cannot be bought or equipped
- [ ] no code path performs a real-money transaction
