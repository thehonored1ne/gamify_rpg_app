---
status: draft
created: 2026-10-30
updated: 2026-10-30
---

# Penalty Policy

> Requirements only. Implementation notes go in `documentation/`.

## Purpose
Penalties add stakes without causing harm. Every penalty is optional, user-defined, in-app only, and reversible.

## Allowed penalty types

| Type | Effect | Cap |
|---|---|---|
| Gold | subtract gold | ≤ 50% of the quest's gold reward, floor 0 |
| Xp | subtract XP | ≤ 50% of the quest's XP reward, never below the current level's floor |
| Streak | reset the daily streak to 0 | at most once per day |
| Mark | increment the "failed quests" counter | informational only |

## Denied content
- R1: penalties must never reference the user's body, weight, food, health, sleep, medication, money, relationships, or social standing.
- R2: penalties must never require a real-world action, payment, self-denial, or public post.
- R3: any penalty text that implies self-harm or disordered behavior is rejected at input time.

## Rules
- R4: a quest has at most one penalty.
- R5: a penalty triggers only on deadline expiry, never on cancellation.
- R6: a penalty is applied exactly once per quest.
- R7: penalties never remove purchased items, titles, themes, or achievements.
- R8: XP subtraction cannot drop total XP below the cumulative threshold of the current level, so a penalty never demotes the user.
- R9: the penalty preview is shown at quest creation before saving.
- R10: penalties are disabled by default on new quests.

## Copy guidelines
- R11: describe the effect, not the user: "Lose 5 gold", not "You failed again".
- R12: no shaming language; no all-caps; no threats.
- R13: the failed state is presented as a system notification, not a moral judgement.

## Acceptance criteria
- [ ] the penalty selector cannot offer a body or health consequence
- [ ] a gold penalty never produces a negative balance
- [ ] cancelling a quest never applies a penalty
- [ ] a penalty is applied once even if the app restarts mid-expiry
- [ ] penalty amounts above the cap are rejected
