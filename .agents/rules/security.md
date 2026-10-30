---
description: Security requirements - credentials, memory leaks, vulnerability checks
globs: ["**/*"]
alwaysApply: true
---

- must always keep security first before anything else.
- must avoid memory leaks.
- dont put sensitive credentials in backend or frontend, put it inside .env file
- always check for security vulnerabilities.
- no secrets, tokens, or pii in source, logs, or error messages; .env and local.properties stay gitignored.
- https only; no cleartext traffic in release builds.
- android:exported="false" unless a component must be public; validate all intent extras and deep-link arguments.
- request the minimum permissions and handle denial.
- enable r8/minify for release; never ship debug keys.
