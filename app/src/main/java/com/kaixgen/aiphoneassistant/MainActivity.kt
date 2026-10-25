package com.kaixgen.aiphoneassistant

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CallAssistantHome(
                        onOpenDialer = { number ->
                            val intent = Intent(
                                Intent.ACTION_DIAL,
                                Uri.fromParts("tel", number, null)
                            )
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CallAssistantHome(onOpenDialer: (String) -> Unit) {
    var number by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("AI Phone Assistant", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text("Call Manager • Starter build")
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = number,
            onValueChange = { number = it.filter { ch -> ch.isDigit() || ch == '+' || ch == '*' || ch == '#' } },
            label = { Text("Phone number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = { if (number.isNotBlank()) onOpenDialer(number) },
            enabled = number.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Open phone dialer")
        }
        Spacer(Modifier.height(12.dp))
        Text(
            "This starter opens the system dialer for user confirmation. " +
                "It does not yet answer, reject, or manage active calls."
        )
    }
}
