/*
 * FILE INFORMATION: CallSession.kt
 * Purpose: In-memory state bridge between Android Telecom and the in-call UI.
 * Developer/Maintainer: Dipak Choudhary | Powered By: KAIxGenShopLabs
 * CREATOR SIGNATURE: Dipak Choudhary | SECTION START: CALL SESSION
 */
package com.kaixgen.aiphoneassistant.telecom

import android.telecom.Call
import android.telecom.VideoProfile
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object CallSession {
    var activeCall: Call? by mutableStateOf(null)
        private set
    var number: String by mutableStateOf("")
        private set
    var state: Int by mutableStateOf(Call.STATE_DISCONNECTED)
        private set
    var incoming: Boolean by mutableStateOf(false)
        private set

    fun attach(call: Call) {
        activeCall = call
        number = call.details.handle?.schemeSpecificPart.orEmpty()
        state = call.state
        incoming = call.state == Call.STATE_RINGING
    }

    fun update(call: Call) {
        if (activeCall === call) {
            state = call.state
            number = call.details.handle?.schemeSpecificPart.orEmpty()
            incoming = call.state == Call.STATE_RINGING
        }
    }

    fun detach(call: Call) {
        if (activeCall === call) {
            activeCall = null
            state = Call.STATE_DISCONNECTED
            incoming = false
            number = ""
        }
    }

    fun answer() { activeCall?.takeIf { it.state == Call.STATE_RINGING }?.answer(VideoProfile.STATE_AUDIO_ONLY) }
    fun reject() { activeCall?.takeIf { it.state == Call.STATE_RINGING }?.reject(false, null) }
    fun disconnect() { activeCall?.disconnect() }
    fun holdOrUnhold() {
        val call = activeCall ?: return
        if (call.details.callCapabilities and Call.Details.CAPABILITY_HOLD != 0) {
            if (call.state == Call.STATE_HOLDING) call.unhold() else call.hold()
        }
    }
    fun canHold(): Boolean = (activeCall?.details?.callCapabilities ?: 0) and Call.Details.CAPABILITY_HOLD != 0
}
// SECTION END: CALL SESSION
// CREATOR SIGNATURE: Dipak Choudhary | KAIxGenShopLabs
