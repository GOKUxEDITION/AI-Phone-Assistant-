<!-- FILE INFORMATION: README.md
Project: AI Phone Assistant | Developer/Maintainer: Dipak Choudhary
Powered By: KAIxGenShopLabs | CREATOR SIGNATURE: Dipak Choudhary
SECTION START: PROJECT OVERVIEW -->
# AI Phone Assistant

**Har Call Ka Smart Saathi**

Android-native call manager starter using Kotlin, Jetpack Compose, Android Telecom and the Contacts/Call Log providers.

- **Developer/Maintainer:** Dipak Choudhary
- **Powered By:** KAIxGenShopLabs
- **Package:** `com.kaixgen.aiphoneassistant`
- **Branch:** `main`

## Implemented in source
- Compose dialer and safe `ACTION_DIAL` handoff
- Device contact search through Contacts Provider
- Call history repository and history UI gated by default Phone role and runtime permission
- Default Phone role request flow
- Telecom `InCallService` lifecycle adapter
- Basic incoming/ongoing call UI with answer, reject, disconnect and capability-checked hold controls
- Hindi speech-recognition intent for voice commands
- Privacy and implementation documentation
- GitHub Actions workflow to assemble debug APK and run unit tests
- Phone number normalization unit tests

## Build locally
Requirements: JDK 17, Android SDK Platform 35, Gradle 8.9.

The Gradle wrapper is not included yet. With Gradle 8.9 installed:

```bash
gradle --version
gradle :app:assembleDebug
gradle :app:testDebugUnitTest
```

Or open the repository in Android Studio and allow Gradle sync.

## Important limitations
This is a development implementation baseline, not yet verified as production-ready. The CI workflow is configured, but its actual run must pass before claiming build success. Default Phone role behavior and background launch of the in-call screen vary by Android version and manufacturer and must be tested on a physical device.

Call history is sensitive and Google Play restricts call-log permissions. Assess policy eligibility before distribution. Call recording, silent calling, AI speaking to a remote caller, mute, DTMF, and Bluetooth audio routing are not implemented.

## Source governance
Source and configuration files use file-information headers, section markers, and creator signatures where supported by the file format. Do not remove attribution or mark features complete without build/device evidence.

<!-- SECTION END: PROJECT OVERVIEW -->
<!-- CREATOR SIGNATURE: Dipak Choudhary | KAIxGenShopLabs -->
