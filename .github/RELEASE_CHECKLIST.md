# Release Checklist

Go through this list before pushing a version tag.

## 1. Decide the version

Format: `vMAJOR.MINOR.PATCH`

| Bump | When |
| ---- | ---- |
| PATCH (`1.0.x`) | Bug fixes and small tweaks, no new features |
| MINOR (`1.x.0`) | New features |
| MAJOR (`x.0.0`) | Big redesign or product direction change |

- MINOR and PATCH must be 99 or less (versionCode = MAJOR * 10000 + MINOR * 100 + PATCH)
- The tag automatically set `versionCode` / `versionName` in `build.gradle.kts`.

## 2. Before tagging

- All changes for this version are merged into `main` through pull requests, and CI is green
- Tested on a real device or emulator, in both light and dark mode, and in English and Traditional Chinese
- Add release note for this version in `README.md`
- Update the Play Store release notes in `distribution/whatsnew/` (`whatsnew-en-GB` and `whatsnew-zh-TW`, 500 characters max each)
- Update `README.md` Requirements if AGP, Gradle, Kotlin or SDK versions changed
- New UI strings are also in `values-zh-rTW/strings.xml` (lint fails on missing translations)

## 3. Only when relevant

- **Screenshots and feature graphic** (`doc/play-store/`) updated if the UI changed noticeably
- **Privacy policy** (app-doc repo) and Play Console **Data safety** updated if the app collects new data or uses a new third-party service (e.g. Crashlytics, a new API)
- **Store listing text** updated if features changed

## 4. Tag and release

```bash
git checkout main
git pull
git tag vX.Y.Z
git push origin vX.Y.Z
```

- The Release workflow on GitHub Actions succeeded (it uploads to Google Play before creating the GitHub Release)
- `MinWeather-X.Y.Z.aab` is attached to the GitHub Release

## 5. Google Play

- The new version appears in closed testing (Alpha) in Play Console, with the release notes from `distribution/whatsnew/`
- After review, install the update from Play and check weather loads (confirms the API key secret works)
- Let testers know a new version is available
