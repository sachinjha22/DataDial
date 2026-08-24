# DataDial

DataDial is a lightweight, single-screen Android app for measuring mobile and Wi-Fi
internet data usage over a specific time period you choose. Pick a start and end
date/time, tap **Calculate Usage**, and see exactly how much data was downloaded and
uploaded — split by mobile vs. Wi-Fi — for that window.

Everything is read directly from Android's own network-statistics APIs, on-device.
The app has no backend, no analytics SDK, and no `INTERNET` permission at all, so
your usage data can never leave your phone.

## Features

- **Custom time range** — pick any Start and End date/time via native Material 3
  date and time pickers.
- **Full breakdown** — total data usage, download vs. upload, and mobile vs. Wi-Fi,
  all for the exact window you selected.
- **Device-wide totals** — aggregates usage across all apps on the device (not just
  DataDial itself), using `NetworkStatsManager.querySummaryForDevice`.
- **On-device only** — no cloud sync, no accounts, no ads, no third-party SDKs.
- **Polished, animated UI** — a dark, "instrument panel" style single screen with:
  - an animated ring/donut chart showing the mobile vs. Wi-Fi split,
  - an animated download vs. upload bar,
  - a dashed empty-state placeholder before the first calculation,
  - a pulsing skeleton-loading state while a calculation is running,
  - a permission banner that deep-links straight to the system's Usage Access
    settings when required,
  - a splash screen and system status/navigation bars that match the app's theme.

## How it works

Android tracks data usage per network transport (mobile, Wi-Fi) at the OS level.
DataDial reads that data through
[`NetworkStatsManager`](https://developer.android.com/reference/android/app/usage/NetworkStatsManager),
querying a device-wide summary bucket for `TYPE_MOBILE` and `TYPE_WIFI` over the
millisecond time range you selected, and reports the `rxBytes` (download) and
`txBytes` (upload) from each.

Reading these statistics requires the special **Usage Access** permission
(`PACKAGE_USAGE_STATS`). This is not a normal runtime permission — Android requires
the user to grant it manually from **Settings → Apps → Special app access → Usage
access**. DataDial detects whether access has been granted (via `AppOpsManager`) and
shows an in-app banner with a button that opens that settings screen directly if it
hasn't.

## Privacy

- No `INTERNET` permission is declared anywhere in the app — it is structurally
  incapable of sending data to a server, because it has no way to open a network
  connection.
- No analytics, crash reporting, or third-party SDKs are included.
- No accounts, sign-in, or cloud sync.
- The only special permission requested is `PACKAGE_USAGE_STATS` (Usage Access),
  used solely to read on-device network statistics for the calculation you request.

## Tech stack

- **Language:** Kotlin
- **UI:** Jetpack Compose (Material 3)
- **Architecture:** MVI (Model-View-Intent) — unidirectional state flow via a single
  `UsageState`, sealed `UsageIntent`s, and a `Channel`-backed `UsageEffect` stream
  for one-off side effects (e.g. opening system settings)
- **Async:** Kotlin Coroutines + `StateFlow`
- **Min SDK:** 24 · **Target/Compile SDK:** 37

## Project structure

```
app/src/main/java/com/buddy/data/dial/
├── MainActivity.kt                  # Activity entry point, splash screen, edge-to-edge setup
├── datausage/
│   ├── data/
│   │   └── NetworkUsageRepository.kt   # NetworkStatsManager / AppOpsManager access
│   ├── model/
│   │   └── UsageResult.kt              # NetworkUsage / UsageResult data classes
│   ├── mvi/
│   │   ├── UsageContract.kt            # UsageState, UsageIntent, UsageEffect
│   │   └── UsageViewModel.kt           # MVI reducer / state holder
│   ├── ui/
│   │   ├── DataUsageScreen.kt          # Screen composition (stateful + stateless + previews)
│   │   ├── UsagePalette.kt             # Color palette
│   │   └── components/                 # Date/time picker, donut chart, stat cards,
│   │                                    # calculate button, permission banner, skeletons
│   └── util/
│       └── FormatUtils.kt              # Byte/date formatting helpers
└── ui/theme/                        # Base Material theme (DataDialTheme)
```

## Building

```bash
./gradlew :app:assembleDebug
```

Install to a connected device or emulator:

```bash
./gradlew :app:installDebug
```

## Using the app

1. Launch DataDial.
2. If prompted, tap **Grant Access** to open Usage Access settings, enable it for
   DataDial, then return to the app.
3. Tap the **Start** and **End** cards to pick the date/time range you want to
   measure.
4. Tap **Calculate Usage**.
5. Review the total, the mobile/Wi-Fi split, and the download/upload breakdown.
