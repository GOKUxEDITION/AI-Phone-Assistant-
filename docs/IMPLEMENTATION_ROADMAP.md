<!-- FILE INFORMATION: IMPLEMENTATION_ROADMAP.md
Developer/Maintainer: Dipak Choudhary | Powered By: KAIxGenShopLabs
CREATOR SIGNATURE: Dipak Choudhary | SECTION START: ROADMAP -->
# Call Manager Implementation Roadmap

## Current repository implementation
- [x] Compose dialer screen and user-confirmed ACTION_DIAL handoff
- [x] Contacts Provider repository and searchable contacts UI
- [x] Call-log repository and history UI gated by role and permission
- [x] Default Phone role request flow
- [x] Telecom InCallService lifecycle adapter
- [x] Basic incoming/ongoing call UI with answer, reject, disconnect, capability-checked hold, mute and speaker routing
- [x] Speech recognition intent for Hindi voice command input
- [ ] Verified Gradle build and physical-device validation
- [ ] DTMF keypad, robust audio endpoint routing, richer call state UX and full accessibility pass
- [ ] Unit/instrumentation tests and accessibility audit

## Phase 1: Foundation
- Compose navigation and screen state
- Permission rationale and runtime permission requests
- Contact repository using Contacts Provider
- Unit tests for phone-number validation and contact matching

## Phase 2: Outgoing calls
- Use ACTION_DIAL by default
- Direct calling uses TelecomManager.placeCall only after the user taps a call action and grants CALL_PHONE; fallback is ACTION_DIAL
- Support dual-SIM behavior through system UI rather than guessing a subscription

## Phase 3: Default Phone role
- Explain role requirements before launching RoleManager request
- Handle ACTION_DIAL tel: intents
- Declare an InCallService with BIND_INCALL_SERVICE protection and Telecom metadata
- Validate incoming and ongoing call UI on a physical device

## Phase 4: Incoming and active calls
- Track Call objects and callbacks in a lifecycle-safe Telecom adapter
- Answer/reject/disconnect only through valid Telecom Call objects
- Enable hold only when supported; other controls require capability checks
- Handle disconnect causes, process recreation, Bluetooth and OEM differences

## Phase 5: Call history
- Verify role and Google Play policy eligibility before requesting READ_CALL_LOG
- Read CallLog.Calls through ContentResolver with bounded queries
- Handle denied/revoked permission, empty data, private numbers and OEM variations
- Do not mirror sensitive call history to cloud by default

## Phase 6: Voice assistant
- SpeechRecognizer intent opens the system speech recognition experience
- Resolve contact names and ask user to disambiguate in a future enhancement
- Confirm recipient through the system dialer before placing calls
- Keep microphone use visible and follow Android background restrictions

## Exit criteria
- Build succeeds on a documented JDK/Gradle/SDK matrix
- Unit tests cover parsing, matching and state transitions
- Real-device SIM tests cover incoming, outgoing, rejected, missed and disconnected calls
- Permissions, privacy disclosures and Play policy have been reviewed
- No feature is marked complete without evidence

## Known limitations
- This is not yet a verified production-ready dialer.
- InCallActivity launching from the background varies by Android version and OEM.
- READ_CALL_LOG is policy-sensitive; distribution eligibility must be assessed before Play release.
<!-- SECTION END: ROADMAP -->
<!-- CREATOR SIGNATURE: Dipak Choudhary | KAIxGenShopLabs -->
