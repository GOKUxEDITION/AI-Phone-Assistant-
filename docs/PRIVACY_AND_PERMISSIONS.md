# Privacy and Permissions

## Current starter
- Declares READ_CONTACTS for planned contact search; no runtime prompt is implemented yet.
- Uses ACTION_DIAL, which opens the installed dialer for the user to confirm.
- Does not access call history, place a direct call, record calls, or upload contact data.

## Planned permissions
- READ_CONTACTS: only for user-requested contact search.
- CALL_PHONE: only if direct calling is explicitly implemented; ACTION_DIAL is preferred.
- READ_CALL_LOG: only if eligible under applicable Google Play policy and needed for core functionality.
- ROLE_DIALER: user-approved role request, not a permission that can be silently granted.
- BIND_INCALL_SERVICE: service binding protection declared on the InCallService; not a normal runtime permission.

Request permissions just in time, explain why they are needed, handle denial gracefully, and provide a settings path after permanent denial. Do not request unrelated permissions.

## Security requirements
- Never upload contacts or call records without a clear, separate purpose and informed consent.
- Never log full phone numbers or call contents in production logs.
- Do not implement covert call recording or hidden microphone capture.
- Treat phone numbers and call metadata as sensitive personal data.
- Test permission revocation and account/device sharing scenarios.
