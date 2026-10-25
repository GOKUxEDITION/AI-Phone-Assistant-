# AI Phone Assistant

Android-native Call Manager starter built with Kotlin and Jetpack Compose.

**Repository:** GOKUxEDITION/AI-Phone-Assistant-  
**Initial scope:** safe outgoing dialer handoff; architecture foundation for contacts, call history, default-dialer role, and call controls.

## Current status

Implemented in this starter:
- Android Gradle project structure
- Kotlin + Jetpack Compose screen
- Phone-number entry
- Opens the system dialer using `Intent.ACTION_DIAL`; the user confirms the call

Not implemented yet:
- Contact lookup
- System call-history access
- Default Phone app role flow
- Telecom `InCallService`
- Incoming-call UI and active-call controls
- AI speech command handling
- Automated tests or verified build

## Build

Requirements:
- JDK 17
- Android SDK Platform 35
- Android Gradle Plugin 8.7.3 compatible Gradle wrapper (generate wrapper with a compatible Gradle installation)

Open the project in Android Studio and allow Gradle sync. Build with:

```bash
gradle :app:assembleDebug
```

A Gradle wrapper is not committed yet.

## Android permissions and privacy

The starter declares `READ_CONTACTS` for the future contacts feature but does not request it yet. It does not request call-log permissions or direct-call permission. `ACTION_DIAL` hands the number to the installed dialer and leaves call initiation to the user.

Call-log permissions are sensitive and subject to Google Play policy. Full call management requires an eligible default dialer implementation, a user-approved `RoleManager.ROLE_DIALER` flow, a correctly declared `InCallService`, and real-device testing.

## Implementation roadmap

1. Add runtime permission handling and contact search.
2. Add call-history reader only after policy/role eligibility is confirmed.
3. Implement default-dialer role request and incoming-call UI.
4. Implement `InCallService` lifecycle and capability-aware controls.
5. Add voice commands with contact disambiguation and confirmation.
6. Add unit/instrumented tests, privacy review, and device validation.

Never claim incoming-call controls or call history work until they have been implemented and tested on a real device.
