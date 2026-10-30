---
status: draft
created: 2026-10-30
updated: 2026-10-30
---

# UI Design System

> Requirements only. Implementation notes go in `documentation/`.

## Direction
Manhwa "system window": near-black surfaces, thin borders, tabular numerals, restrained glow. Clean and adult, never cartoonish.

## Tokens

| Token | Value |
|---|---|
| `background` | `#0B0D10` |
| `surface` | `#12151A` |
| `surfaceVariant` | `#181C23` |
| `outline` | `#2A2F3A` |
| `onBackground` | `#E6E8EB` |
| `onSurfaceVariant` | `#9AA0A6` |
| `accent` | `#6C8CFF` |
| `gold` | `#F5C451` |
| `danger` | `#E5534B` |

Rarity colors: Common `#9AA0A6`, Uncommon `#4CAF50`, Rare `#42A5F5`, Epic `#AB47BC`, Legendary `#FFB300`.

## Rules
- R1: dark theme is the default; a light theme is optional and must keep the same contrast ratios.
- R2: rarity is never communicated by color alone — always pair the color with the rarity label and a distinct shape or icon.
- R3: borders are 1dp `outline`; elevation is avoided in favor of borders.
- R4: corner radii come from a fixed set: 4dp, 8dp, 12dp.
- R5: spacing uses 4/8/12/16/24/32dp only.
- R6: numerals in XP, gold, and level displays use tabular figures so values do not shift.
- R7: no decorative fonts; Material 3 type scale with adjusted letter spacing for headings.

## Motion
- R8: state changes 150ms; XP bar fill 250ms; level-up overlay 400ms.
- R9: a "reduce motion" setting disables the level-up overlay and bar animation.
- R10: no looping or idle animations.

## Components
- XP bar with level badge and numeric `current / next`.
- Gold chip with icon and tabular amount.
- Quest card: title, rarity chip, reward row, deadline, status.
- System window panel: bordered container used for daily offers and level-up.
- Empty state: one line of copy plus one action.
- Snackbar for undo, with a 10-second window.

## States
- R11: every list screen defines loading, empty, error, and content states.
- R12: empty states explain the next action; they never blame the user.
- R13: errors offer a retry action and keep the previous content visible when possible.

## Accessibility
- R14: contrast at least 4.5:1 for body text and 3:1 for large text.
- R15: touch targets at least 48dp.
- R16: text scales to 200% without clipping or overlap.
- R17: the XP bar exposes progress semantics (`progressBarRangeInfo`) and a spoken label.
- R18: all icon-only buttons have content descriptions.

## Acceptance criteria
- [ ] rarity is distinguishable in grayscale
- [ ] every screen has all four states implemented
- [ ] reduce motion removes the level-up overlay
- [ ] TalkBack announces level, XP progress, and gold on the dashboard
- [ ] no hardcoded user-facing strings outside `strings.xml`
