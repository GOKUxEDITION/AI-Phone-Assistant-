<!-- FILE INFORMATION: PRIVACY_AND_PERMISSIONS.md
Developer/Maintainer: Dipak Choudhary | Powered By: KAIxGenShopLabs
CREATOR SIGNATURE: Dipak Choudhary | SECTION START: PRIVACY -->
# Privacy and Permissions

## Current implementation
- Declares READ_CONTACTS and requests it when the user opens contact search.
- Uses ACTION_DIAL, which opens the installed dialer and leaves call confirmation to the user.
- Includes READ_CALL_LOG declaration and only attempts history reads after checking default Phone role and runtime permission.
- Does not record calls or upload contact/call data.
- Telecom call controls require the user to choose this app as the default Phone app.

## Permission boundaries
- READ_CONTACTS: only for user-requested contact search.
- CALL_PHONE: not declared; direct-call ACTION_CALL is not used.
- READ_CALL_LOG: policy-sensitive; request only when eligible and needed for core functionality.
- ROLE_DIALER: user-approved role request, not a permission that can be silently granted.
- BIND_INCALL_SERVICE: service binding protection declared on the InCallService; not a normal runtime permission.
- POST_NOTIFICATIONS: declared for future call notifications; current starter does not yet implement a notification channel.

## Security requirements
- Never upload contacts or call records without a clear, separate purpose and informed consent.
- Never log full phone numbers or call contents in production logs.
- Do not implement covert call recording or hidden microphone capture.
- Treat phone numbers and call metadata as sensitive personal data.
- Test permission revocation and account/device sharing scenarios.

## Release warning
This repository is a development starter and has not been built or tested on a physical device in this session. Default-dialer eligibility, Play policy, OEM call-screen behavior, and permission flows must be validated before release.
<!-- SECTION END: PRIVACY -->
<!-- CREATOR SIGNATURE: Dipak Choudhary | KAIxGenShopLabs -->
