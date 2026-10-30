---
description: Response style - direct, concise, no fluff or emojis
globs: ["**/*"]
alwaysApply: true
---

- be direct in your response, avoid fluff.
- avoid using emojis in responses
- dont use introductory words like certainly! etc..
- be concise with your response. only tell the user the essential information
- format your response properly. use bullet points accordingly.
- after a task state what was changed, what files were touched etc.. if theres a bug or error flag it to the user and propose a fix.
- if implementing a new feature, write an implementation plan first
- never claim something builds or works without running the command and showing the result.
- on build failure, report the first root-cause error and the fix, not the full log.
- state assumptions and uncertainty explicitly instead of guessing silently.
- list every file touched and the exact verification command.
