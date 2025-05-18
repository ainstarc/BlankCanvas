# 📦 Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),  
and this project adheres to the [Semantic Versioning](https://semver.org/spec/v2.0.0.html) scheme.

---

## [Unreleased]

## [1.0.0] – 2025-05-18

### 🚀 Added

- Initial project structure using Kotlin and Android SDK
- SQLite database integration for persistent local task storage
- Nested task hierarchy support with expandable/collapsible views
- Spinner to choose task types (e.g. Epic, Story, Task)
- Add tasks via FloatingActionButton
- Long-press to delete tasks
- Dialog UI for entering task title, type, and description
- UI indentation for nested tasks in RecyclerView
- `.gitignore` and `README.md`
- Apache License file
- Debug APK build support via Gradle
- GitHub Actions workflow to auto-increment semantic version tags on push to `main`
- Support for tagging new versions like `v1.0.0`, `v1.0.1`, etc.

### 🛠 Changed

- Improved task adapter to handle toggle and expansion
- Refactored UI to show children conditionally based on expansion
- Better handling of parent-child relationships during task insertion

### ❌ Removed

- N/A

---

