# Changelog

All notable changes are documented here. Format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/). Wardove uses [semantic versioning](https://semver.org/) with the scheme `MAJOR.MINOR.BUILD` — `BUILD` is the CI run number.

## [Unreleased]

## [2.0.25] — 2026-06-20

### Fixed
- Back button behavior after navigation-related regressions
- Version numbering and signing configuration updated for stable in-app updates

### Changed
- Signing setup and version numbering refactored ([#6](https://github.com/zorenkonte/wardove/pull/6))

## [2.0.0] — 2026-06-18

### Added
- Splash screen on app launch
- New app launcher icon

### Fixed
- In-app update not applying: APK install now uses `ActivityResultLauncher` to detect result and reload version info
- APK file not cleaned up after install
- Biometric App Lock never prompting on app reopen (StateFlow race condition)
- App Lock toggle could be enabled without verifying biometrics first

## [1.0.23] — 2026-06-19

### Fixed
- Laundry history back navigation
- Empty state illustration added to Laundry History screen ([#5](https://github.com/zorenkonte/wardove/pull/5))

## [1.0.21] — 2026-06-18

_Internal build — no release notes._

## [1.0.20] — 2026-06-18

_Internal build — no release notes._

## [1.0.19] — 2026-06-18

_Internal build — no release notes._

## [1.0.18] — 2026-06-18

_Internal build — no release notes._

## [1.0.17] — 2026-06-18

_Internal build — no release notes._

## [1.0.16] — 2026-06-18

_Internal build — no release notes._

## [1.0.15] — 2026-06-18

_Internal build — no release notes._

## [1.0.14] — 2026-06-15

_Internal build — no release notes._

## [1.0.13] — 2026-06-15

### Added
- Biometric-only App Lock feature ([#4](https://github.com/zorenkonte/wardove/pull/4))

## [1.0.10] — 2026-06-14

### Added
- Auto-update system: checks GitHub Releases for new versions, downloads and installs APK in-app ([#3](https://github.com/zorenkonte/wardove/pull/3))
- Feedback / issues link in About settings

## [1.0.8] — 2026-06-14

_Internal build — no release notes._

## [1.0.7] — 2026-06-14

### Added
- Settings screen with sub-screens for Appearance, App Lock, and About
- Dark mode / light mode / system theme toggle
- Material You dynamic color toggle
- Navigation drawer replacing bottom navigation
- Laundry wear threshold configuration ([#2](https://github.com/zorenkonte/wardove/pull/2))

## [1.0.4] — 2026-06-13

### Added
- "Un-wear today" action on Item Detail — undo an accidental wear log for the current day ([#1](https://github.com/zorenkonte/wardove/pull/1))

_First public release._

[Unreleased]: https://github.com/zorenkonte/wardove/compare/v2.0.25...HEAD
[2.0.25]: https://github.com/zorenkonte/wardove/releases/tag/v2.0.25
[2.0.0]: https://github.com/zorenkonte/wardove/releases/tag/v2.0.0
[1.0.23]: https://github.com/zorenkonte/wardove/releases/tag/v1.0.23
[1.0.21]: https://github.com/zorenkonte/wardove/releases/tag/v1.0.21
[1.0.20]: https://github.com/zorenkonte/wardove/releases/tag/v1.0.20
[1.0.19]: https://github.com/zorenkonte/wardove/releases/tag/v1.0.19
[1.0.18]: https://github.com/zorenkonte/wardove/releases/tag/v1.0.18
[1.0.17]: https://github.com/zorenkonte/wardove/releases/tag/v1.0.17
[1.0.16]: https://github.com/zorenkonte/wardove/releases/tag/v1.0.16
[1.0.15]: https://github.com/zorenkonte/wardove/releases/tag/v1.0.15
[1.0.14]: https://github.com/zorenkonte/wardove/releases/tag/v1.0.14
[1.0.13]: https://github.com/zorenkonte/wardove/releases/tag/v1.0.13
[1.0.10]: https://github.com/zorenkonte/wardove/releases/tag/v1.0.10
[1.0.8]: https://github.com/zorenkonte/wardove/releases/tag/v1.0.8
[1.0.7]: https://github.com/zorenkonte/wardove/releases/tag/v1.0.7
[1.0.4]: https://github.com/zorenkonte/wardove/releases/tag/v1.0.4
