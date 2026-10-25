/*
 * FILE INFORMATION: AssistantInCallService.kt
 * Purpose: Receive Telecom call lifecycle events for the selected default dialer.
 * Developer/Maintainer: Dipak Choudhary | Powered By: KAIxGenShopLabs
 * CREATOR SIGNATURE: Dipak Choudhary | SECTION START: TELECOM SERVICE
 */
package com.kaixgen.aiphoneassistant.telecom

import android.content.Intent
import android.telecom.Call
import android.telecom.CallAudioState
import android.telecom.InCallService
import android.util.Log
import com.kaixgen.aiphoneassistant.ui.InCallActivity

class AssistantInCallService : InCallService() {
    private val callbacks = mutableMapOf<Call, Call.Callback>()

    override fun onCreate() {
        super.onCreate()
        CallSession.attachService(this)
        CallSession.updateAudioState(callAudioState)
    }

    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        CallSession.attachService(this)
        CallSession.attach(call)
        val callback = object : Call.Callback() {
            override fun onStateChanged(call: Call, state: Int) { CallSession.update(call) }
            override fun onDetailsChanged(call: Call, details: Call.Details) { CallSession.update(call) }
            override fun onCallDestroyed(call: Call) {
                CallSession.detach(call)
                callbacks.remove(call)?.let { call.unregisterCallback(it) }
            }
        }
        callbacks[call] = callback
        call.registerCallback(callback)
        // SECTION START: SHOW CALL UI
        // Android/OEM background launch policy differs; physical-device validation is required.
        try {
            startActivity(Intent(this, InCallActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP))
        } catch (exception: Exception) {
            Log.w("AiPhoneAssistant", "Could not open in-call UI", exception)
        }
        // SECTION END: SHOW CALL UI
    }

    override fun onCallAudioStateChanged(audioState: CallAudioState) {
        super.onCallAudioStateChanged(audioState)
        CallSession.updateAudioState(audioState)
    }

    override fun onCallRemoved(call: Call) {
        callbacks.remove(call)?.let { call.unregisterCallback(it) }
        CallSession.detach(call)
        super.onCallRemoved(call)
    }

    override fun onDestroy() {
        CallSession.detachService(this)
        super.onDestroy()
    }
}
// SECTION END: TELECOM SERVICE
// CREATOR SIGNATURE: Dipak Choudhary | KAIxGenShopLabs
