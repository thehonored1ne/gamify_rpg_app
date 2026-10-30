---
description: Testing expectations - what to test and how to verify
globs: ["**/*"]
alwaysApply: true
---

- unit-test viewmodels and repositories on the jvm; no android framework deps in unit tests.
- run `.\gradlew test` before handing off any change.
- for ui changes, run `.\gradlew installDebug` and verify on the device (serial 15187705C5010595).
- test behavior, not implementation details; one assertion focus per test.
- do not delete or skip a failing test to make the build pass.
