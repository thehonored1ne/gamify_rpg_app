---
status: draft
created: 2026-10-30
updated: 2026-10-30
---

# System Quests

> Requirements only. Implementation notes go in `documentation/`.

## Unlock gates
- R1: Common and Uncommon system quests unlock at level 2.
- R2: Rare system quests unlock at level 5.
- R3: below level 2 the dashboard shows a locked panel explaining the unlock, with current progress.

## Daily offers
- R4: each local calendar day generates one Common offer, one Uncommon offer, and — from level 5 — one Rare offer.
- R5: offers are generated at first app open of the day, not by a background job.
- R6: offers are persisted, so closing and reopening the app shows the same offers.
- R7: offers expire at local midnight; unaccepted offers are discarded.
- R8: a quest from the library is not offered again within 7 days.

## Generation
- R9: selection is deterministic for a given `(date, level, history)` tuple.
- R10: the seed is the ISO local date (`yyyy-MM-dd`), hashed with the rarity and the library id.
- R11: candidates are filtered by rarity, by the level gate, and by the 7-day exclusion list.
- R12: if fewer candidates remain than offers needed, the exclusion window shrinks to 3 days, then 1 day, then 0.
- R13: the generator is a pure function in `domain` and unit-tested with a fixed clock.

## Offer lifecycle
- R14: states are Offered, Accepted, Declined, Expired.
- R15: accepting creates an Active quest with the library's title, description, category, difficulty, and fixed rewards.
- R16: declining is final for that day; no reroll.
- R17: accepted quests follow normal quest rules, including deadlines (none) and penalties (none).

## Rewards

| Rarity | XP | Gold |
|---|---|---|
| Common | 5 | 2 |
| Uncommon | 10 | 5 |
| Rare | 20 | 10 |

## Library
- R18: the library ships as a bundled JSON asset, not a database table.
- R19: each entry declares `id`, `title`, `description`, `category`, `rarity`, and `tags`.
- R20: entries must be universally achievable: no equipment, location, ability, money, or social exposure required.

## Acceptance criteria
- [ ] the same date produces the same offers after a restart
- [ ] no library quest repeats within 7 days
- [ ] offers below the level gate never appear
- [ ] accepting an offer creates exactly one quest
- [ ] declining does not create a quest
- [ ] offers expire at local midnight
