# Privacy Policy

**Effective date: June 20, 2026**

Wardove is a free, open-source personal wardrobe tracker for Android. This policy explains what data the app handles and how.

## Data storage

All data you enter — clothing items, wear logs, laundry cycles, and item photos — is stored **entirely on your device** in the app's private sandbox:

- **Room database** — item and activity records in the app's internal storage.
- **Item photos** — image files saved to the app's private `filesDir` directory.
- **Preferences** — theme mode, dynamic color, and app-lock state stored via DataStore.

None of this data ever leaves your device. There is no server, no cloud sync, and no backup outside of Android's standard system backup (which you control in system settings). **All data is permanently deleted when you uninstall the app.**

## Permissions

| Permission | Purpose |
|---|---|
| `CAMERA` | Capturing photos to attach to clothing items. Photos are stored locally only. |
| `READ_MEDIA_IMAGES` / `READ_EXTERNAL_STORAGE` | Selecting existing photos from your gallery to attach to items. |
| `INTERNET` | Checking GitHub Releases for app updates, and loading these documentation pages. |
| `REQUEST_INSTALL_PACKAGES` | Installing downloaded app updates directly (sideload APK). |
| `USE_BIOMETRIC` / `USE_FINGERPRINT` | App-lock authentication (optional, enabled by you). |

## Network activity

Wardove makes **two types of outbound network requests**, and nothing else:

1. **Update check** — when you open the Updates screen, the app fetches release metadata from `https://api.github.com/repos/zorenkonte/wardove/releases`. This request sends your device's IP address to GitHub's servers. See [GitHub's Privacy Statement](https://docs.github.com/en/site-policy/privacy-policies/github-general-privacy-statement) for how they handle that.

2. **Documentation pages** — tapping Privacy Policy, Terms of Service, or any other docs link opens a page on `https://zorenkonte.github.io/wardove/` inside the app (Chrome Custom Tabs). This sends your device's IP address to GitHub Pages' servers, subject to GitHub's privacy policy linked above.

No analytics, no advertising SDKs, no crash reporting, and no telemetry of any kind are included in the app.

## Third-party libraries

Wardove uses open-source libraries (Jetpack Compose, Room, Hilt, Coil, etc.). You can view the full list and their licenses in the app under **Settings → About → Open Source Licenses**. These libraries process data locally on your device only; none of them transmit data externally.

## Children's privacy

Wardove does not knowingly collect data from or about children. It collects no personal data whatsoever.

## Changes to this policy

If this policy changes materially, the effective date above will be updated and the change will appear in the [Changelog](/changelog). Wardove is open-source; all changes to this file are visible in the [GitHub repository](https://github.com/zorenkonte/wardove).

## Contact

Questions or concerns? [Open an issue on GitHub](https://github.com/zorenkonte/wardove/issues).
