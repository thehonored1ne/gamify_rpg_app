---
description: General coding standards - simplicity, naming, organization, separation of concerns
globs: ["**/*"]
alwaysApply: true
---

## General
- avoid over-engineering and over-abstraction; if the code can be simpler, make it simpler.
- use proper convention when it comes to naming, be consistent.
- always organize things up, always group related files together.
- when writing code, you must follow the latest conventions and rules.
- always write clean, maintainable, scalable code.
- when writing code you must not bloat a function or class. if it only needs one or a few responsibilities, keep it that way and create another.
- follow the dry principle. before writing code always check if it already exists.
- separate of concern, must avoid putting business logic inside ui code same as the other.
- before handing the result to the user, run the test suite; see testing.md.
- no need to put code in the response unless requested.

## Kotlin / Compose
- prefer val over var, immutable data classes, sealed interfaces for ui state, no !!.
- composables stay stateless and state is hoisted; modifier: Modifier = Modifier is the last parameter with a default.
- use rememberSaveable for transient ui state; no side effects during composition.
- ui text lives in res/values/strings.xml; no hardcoded user-facing strings.

## Build
- add dependencies only via gradle/libs.versions.toml; never hardcode coordinates or versions in build.gradle.kts.
- make the smallest targeted edit; never rewrite a whole file to change a few lines.
- don't add a dependency if the stdlib or compose already covers it.
