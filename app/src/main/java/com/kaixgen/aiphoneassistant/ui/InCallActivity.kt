/*
 * FILE INFORMATION: InCallActivity.kt
 * Purpose: Incoming and ongoing call UI driven by Telecom CallSession.
 * Developer/Maintainer: Dipak Choudhary | Powered By: KAIxGenShopLabs
 * CREATOR SIGNATURE: Dipak Choudhary
 * SECTION START: IN-CALL UI
 */
package com.kaixgen.aiphoneassistant.ui

import android.os.Build
import android.os.Bundle
import android.telecom.Call
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaixgen.aiphoneassistant.telecom.CallSession

class InCallActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // SECTION START: LOCK SCREEN COMPATIBILITY
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
        // SECTION END: LOCK SCREEN COMPATIBILITY
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) { InCallScreen() }
            }
        }
    }
}

@Composable
private fun InCallScreen() {
    val number = CallSession.number
    val state = CallSession.state
    val incoming = CallSession.incoming
    Column(
        modifier = Modifier.fillMaxSize().padding(28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(if (incoming) "Incoming call" else "Call in progress", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))
        Text(number.ifBlank { "Unknown caller" }, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(24.dp))
        Text(when (state) {
            Call.STATE_RINGING -> "Ringing"
            Call.STATE_DIALING -> "Calling"
            Call.STATE_ACTIVE -> "Connected"
            Call.STATE_HOLDING -> "On hold"
            Call.STATE_DISCONNECTED -> "Call ended"
            else -> "Connecting"
        })
        Spacer(Modifier.height(24.dp))
        if (incoming) {
            Button(onClick = { CallSession.answer() }) { Text("Answer") }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = { CallSession.reject() }) { Text("Decline") }
        } else if (state != Call.STATE_DISCONNECTED) {
            if (CallSession.canHold()) {
                OutlinedButton(onClick = { CallSession.holdOrUnhold() }) { Text("Hold / Resume") }
                Spacer(Modifier.height(8.dp))
            }
            Button(onClick = { CallSession.disconnect() }) { Text("End call") }
        } else {
            OutlinedButton(onClick = { /* Call has ended; returning is safe. */ }) { Text("Call ended") }
        }
    }
}
// SECTION END: IN-CALL UI
// CREATOR SIGNATURE: Dipak Choudhary | KAIxGenShopLabs
