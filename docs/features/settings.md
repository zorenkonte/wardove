# Settings

Access Settings from the navigation drawer. It has three sub-screens.

## Appearance

### Theme

Choose how the app handles light/dark mode:

| Option | Behavior |
|--------|----------|
| System default | Follows the device system theme |
| Light | Always light |
| Dark | Always dark |

### Dynamic color (Material You)

When enabled (and supported by the device — Android 12+), the app's color scheme adapts to your device wallpaper. Toggle it off for the fixed Wardove color palette.

## App Lock

Require **biometric authentication** (fingerprint or face) to open the app.

- The toggle is disabled with "No biometric hardware available" if no biometric is enrolled on the device.
- Enabling the lock requires a successful biometric confirmation first.
- The lock activates when the app has been in the background for more than **1 second**. Switching apps briefly and returning quickly does not trigger a re-auth.

## About

Shows:

- **Version** — current `versionName (versionCode)` (e.g. `2.0.25 (125)`).
- **GitHub** — link to the source repository.
- **Check for updates** — queries GitHub Releases and shows available updates. If a newer version exists, you can download and install the APK directly in-app.
- **Feedback / Issues** — opens the GitHub Issues page.
