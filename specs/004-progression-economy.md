---
status: draft
created: 2026-10-30
updated: 2026-10-30
---

# Progression and Economy

> Requirements only. Implementation notes go in `documentation/`.

## XP curve
- R1: XP required to advance from level `n` to `n + 1` is `xpToNext(n) = 50 + 25 * (n - 1)`.
- R2: level is derived from total XP, never stored as a separate writable field.
- R3: level cap is 100. At the cap, XP still accumulates but no further levels are granted.

Cumulative thresholds:

| Level | Total XP |
|---|---|
| 1 | 0 |
| 2 | 50 |
| 3 | 125 |
| 4 | 225 |
| 5 | 350 |
| 10 | 1350 |

## Difficulty baselines

| Difficulty | XP | Gold |
|---|---|---|
| Common | 5 | 2 |
| Uncommon | 10 | 5 |
| Rare | 20 | 10 |
| Epic | 40 | 20 |
| Legendary | 80 | 40 |

- R4: user overrides are allowed from 50% (rounded up) to 200% of the baseline, integer values only.
- R5: XP reward minimum is 1; gold reward minimum is 0.

## Gold
- R6: gold is earned from quest completion, system quest completion, and achievement unlocks. It is never purchasable with real money.
- R7: gold balance is the sum of all ledger deltas and is never negative.
- R8: a purchase is rejected when the balance is insufficient; the UI shows the shortfall.

## Unlock gates

| Feature | Level |
|---|---|
| System quests (Common, Uncommon) | 2 |
| System quests (Rare) | 5 |
| Shop: titles | 1 |
| Shop: themes | 1 |
| Shop: UI effects | 5 |
| Quest chains | not level-gated (v0.3) |

## Ledger rules
- R9: XP and gold are stored as append-only events; balances are derived, not written directly.
- R10: every balance-changing action is a single Room transaction that also writes its event.
- R11: penalties subtract but clamp at 0; they never remove owned items.

## Acceptance criteria
- [ ] `levelFor(0) == 1`, `levelFor(49) == 1`, `levelFor(50) == 2`
- [ ] level 100 is reachable and does not overflow
- [ ] gold balance equals the sum of event deltas after any sequence of actions
- [ ] a purchase with insufficient gold leaves the balance unchanged
- [ ] no code path can produce a negative balance
