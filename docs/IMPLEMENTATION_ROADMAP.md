# Call Manager Implementation Roadmap

## Phase 1: Foundation
- Compose navigation and screen state
- Permission rationale and runtime permission requests
- Contact repository using Contacts Provider
- Unit tests for phone-number validation and contact matching

## Phase 2: Outgoing calls
- Use ACTION_DIAL by default
- Optional ACTION_CALL only after explicit user intent and CALL_PHONE permission
- Support dual-SIM behavior through system UI rather than guessing a subscription

## Phase 3: Default Phone role
- Explain role requirements before launching RoleManager request
- Implement ACTION_DIAL handling for tel: URIs
- Declare an InCallService with correct BIND_INCALL_SERVICE protection and Telecom metadata
- Provide incoming and ongoing call UI before claiming default-dialer completeness

## Phase 4: Incoming and active calls
- Track Call objects and callbacks in a lifecycle-safe Telecom adapter
- Answer/reject/disconnect only through valid Telecom Call objects
- Enable mute, hold, DTMF and audio route controls only when supported
- Handle disconnect causes, process recreation, Bluetooth and OEM differences

## Phase 5: Call history
- Verify role and Google Play policy eligibility before requesting READ_CALL_LOG
- Read CallLog.Calls through ContentResolver with bounded queries
- Handle denied/revoked permission, empty data, private numbers and OEM variations
- Do not mirror sensitive call history to cloud by default

## Phase 6: Voice assistant
- SpeechRecognizer or approved speech-to-text provider
- Resolve contact names; ask user to choose when ambiguous
- Confirm recipient before placing calls
- Keep microphone use visible and follow Android background restrictions

## Exit criteria
- Build succeeds on a documented JDK/Gradle/SDK matrix
- Unit tests cover parsing, matching and state transitions
- Real-device SIM tests cover incoming, outgoing, rejected, missed and disconnected calls
- Permissions, privacy disclosures and Play policy have been reviewed
- No feature is marked complete without evidence
