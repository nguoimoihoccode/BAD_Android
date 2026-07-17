# CourtSide Android (Native)

Kotlin + Jetpack Compose CourtSide app. Feature parity with Flutter `BAD_Mobile/platform`.

## Structure
- `app/src/main/kotlin/com/maxton/bad_android/` — features (Clean Architecture)
- API base (emulator): `http://10.0.2.2:8000/api/v1/`

## Setup
1. Open this folder in Android Studio
2. Sync Gradle (wrapper may need to be generated)
3. Run on emulator/device

## Git history
Commits are split by feature (theme → network → auth → sessions → match → …).
Push with: create empty GitHub repo, then `git remote add origin <url>` and push `main` (or push commits one-by-one if preferred).
