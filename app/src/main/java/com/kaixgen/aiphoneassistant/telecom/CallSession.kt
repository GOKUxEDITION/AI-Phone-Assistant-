/*
 * FILE INFORMATION: CallSession.kt
 * Project: AI Phone Assistant | Purpose: In-memory Telecom call state and supported controls.
 * Developer/Maintainer: Dipak Choudhary | Powered By: KAIxGenShopLabs
 * CREATOR SIGNATURE: Dipak Choudhary | SECTION START: CALL SESSION
 */
package com.kaixgen.aiphoneassistant.telecom

import android.telecom.Call
import android.telecom.CallAudioState
import android.telecom.InCallService
import android.telecom.VideoProfile
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object CallSession {
    private var inCallService: InCallService? = null
    var activeCall: Call? by mutableStateOf(null)
        private set
    var number: String by mutableStateOf("")
        private set
    var state: Int by mutableStateOf(Call.STATE_DISCONNECTED)
        private set
    var incoming: Boolean by mutableStateOf(false)
        private set
    var muted: Boolean by mutableStateOf(false)
        private set
    var speakerOn: Boolean by mutableStateOf(false)
        private set

    fun attachService(service: InCallService) {
        inCallService = service
        speakerOn = service.callAudioState?.route == CallAudioState.ROUTE_SPEAKER
    }

    fun detachService(service: InCallService) {
        if (inCallService === service) inCallService = null
    }

    fun updateAudioState(audioState: CallAudioState?) {
        speakerOn = audioState?.route == CallAudioState.ROUTE_SPEAKER
    }

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
            muted = false
        }
    }

    fun answer() { activeCall?.takeIf { it.state == Call.STATE_RINGING }?.answer(VideoProfile.STATE_AUDIO_ONLY) }
    fun reject() { activeCall?.takeIf { it.state == Call.STATE_RINGING }?.reject(false, null) }
    fun disconnect() { activeCall?.disconnect() }
    fun canHold(): Boolean = ((activeCall?.details?.callCapabilities ?: 0) and Call.Details.CAPABILITY_HOLD) != 0

    fun holdOrUnhold() {
        val call = activeCall ?: return
        if (!canHold()) return
        if (call.state == Call.STATE_HOLDING) call.unhold() else call.hold()
    }

    fun toggleMute() {
        val service = inCallService ?: return
        muted = !muted
        service.setMuted(muted)
    }

    fun toggleSpeaker() {
        val service = inCallService ?: return
        val target = if (speakerOn) CallAudioState.ROUTE_EARPIECE else CallAudioState.ROUTE_SPEAKER
        service.setAudioRoute(target)
        speakerOn = !speakerOn
    }
}
// SECTION END: CALL SESSION
// CREATOR SIGNATURE: Dipak Choudhary | KAIxGenShopLabs
