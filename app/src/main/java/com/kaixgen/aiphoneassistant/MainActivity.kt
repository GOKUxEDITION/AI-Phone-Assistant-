/*
 * FILE INFORMATION: MainActivity.kt
 * Project: AI Phone Assistant | Developer/Maintainer: Dipak Choudhary
 * Powered By: KAIxGenShopLabs | CREATOR SIGNATURE: Dipak Choudhary
 * SECTION START: ACTIVITY AND COMPOSE UI
 */
package com.kaixgen.aiphoneassistant

import android.Manifest
import android.app.role.RoleManager
import android.telecom.TelecomManager
import android.os.Bundle
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.kaixgen.aiphoneassistant.data.PhoneDataRepository
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val repository by lazy { PhoneDataRepository(applicationContext) }
    private var pendingNumber: String? = null
    private val callPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        val target = pendingNumber
        pendingNumber = null
        if (granted && !target.isNullOrBlank()) placeTelecomCall(target)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(
                primary = Color(0xFF0B7A4B), secondary = Color(0xFF075C39),
                background = Color(0xFFF7FAF8), surface = Color.White
            )) {
                Surface(Modifier.fillMaxSize()) {
                    CallAssistantScreen(
                        initialNumber = intent?.data?.schemeSpecificPart.orEmpty(),
                        repository = repository,
                        isDefaultDialer = ::isDefaultDialer,
                        requestDefaultDialer = ::requestDefaultDialer,
                        onDial = ::openSystemDialer
                    )
                }
            }
        }
    }

    private fun isDefaultDialer(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getSystemService(RoleManager::class.java).isRoleHeld(RoleManager.ROLE_DIALER)
        } else {
            // SECTION START: LEGACY DEFAULT DIALER CHECK
            getSystemService(TelecomManager::class.java).defaultDialerPackage == packageName
            // SECTION END: LEGACY DEFAULT DIALER CHECK
        }

    private fun requestDefaultDialer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val manager = getSystemService(RoleManager::class.java)
            if (manager.isRoleAvailable(RoleManager.ROLE_DIALER) && !manager.isRoleHeld(RoleManager.ROLE_DIALER)) {
                @Suppress("DEPRECATION")
                startActivityForResult(manager.createRequestRoleIntent(RoleManager.ROLE_DIALER), 4102)
            }
        } else {
            startActivity(Intent("android.telecom.action.CHANGE_DEFAULT_DIALER")
                .putExtra("android.telecom.extra.CHANGE_DEFAULT_DIALER_PACKAGE_NAME", packageName))
        }
    }

    private fun openSystemDialer(number: String) {
        // SECTION START: OUTGOING CALL HANDOFF
        // If selected as the default dialer, a deliberate user action can initiate Telecom.
        if (isDefaultDialer()) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                placeTelecomCall(number)
            } else {
                pendingNumber = number
                callPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
            }
        } else {
            startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null)))
        }
        // SECTION END: OUTGOING CALL HANDOFF
    }

    private fun placeTelecomCall(number: String) {
        try {
            getSystemService(TelecomManager::class.java).placeCall(
                Uri.fromParts("tel", number, null), Bundle()
            )
        } catch (_: SecurityException) {
            startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null)))
        } catch (_: IllegalStateException) {
            startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null)))
        }
    }
}

private enum class HomeTab { DIALER, CONTACTS, HISTORY, SETTINGS }

@Composable
private fun CallAssistantScreen(
    initialNumber: String,
    repository: PhoneDataRepository,
    isDefaultDialer: () -> Boolean,
    requestDefaultDialer: () -> Unit,
    onDial: (String) -> Unit
) {
    var tab by remember { mutableStateOf(HomeTab.DIALER) }
    var number by remember { mutableStateOf(initialNumber) }
    var search by remember { mutableStateOf("") }
    var contacts by remember { mutableStateOf(emptyList<com.kaixgen.aiphoneassistant.data.ContactEntry>()) }
    var history by remember { mutableStateOf(emptyList<com.kaixgen.aiphoneassistant.data.CallEntry>()) }
    var defaultRole by remember { mutableStateOf(isDefaultDialer()) }
    var status by remember { mutableStateOf("Ready. Calls are confirmed in the system dialer.") }
    var refresh by remember { mutableIntStateOf(0) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        refresh++
        status = if (results.values.any { it }) "Permission updated." else "Permission not granted."
    }
    val speechLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
        if (!spoken.isNullOrBlank()) {
            val command = spoken.lowercase(Locale.getDefault())
            val target = command.removePrefix("call ").removePrefix("phone karo ").removeSuffix(" ko call karo").trim()
            val match = repository.searchContacts(target).firstOrNull()
            if (match != null) {
                number = match.number
                tab = HomeTab.DIALER
                status = "Contact selected: " + match.name + ". Call button dabakar confirm karein."
            } else {
                val digits = command.filter { it.isDigit() || it == '+' }
                if (digits.isNotBlank()) {
                    number = digits
                    tab = HomeTab.DIALER
                    status = "Number ready. Call button dabakar confirm karein."
                } else status = "Contact nahi mila. Contacts tab se search karein."
            }
        }
    }
    fun hasPermission(permission: String) =
        ContextCompat.checkSelfPermission(repository.context, permission) == PackageManager.PERMISSION_GRANTED

    LaunchedEffect(tab, search, refresh) {
        defaultRole = isDefaultDialer()
        contacts = if (hasPermission(Manifest.permission.READ_CONTACTS)) repository.searchContacts(search) else emptyList()
        history = if (defaultRole && hasPermission(Manifest.permission.READ_CALL_LOG)) repository.readCallHistory() else emptyList()
        if (tab == HomeTab.CONTACTS && !hasPermission(Manifest.permission.READ_CONTACTS)) status = "Contacts permission required."
        if (tab == HomeTab.HISTORY && (!defaultRole || !hasPermission(Manifest.permission.READ_CALL_LOG))) status = "History needs default Phone role and call-log permission."
    }

    Scaffold(
        topBar = {
            Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp)) {
                Text("AI Phone Assistant", style = MaterialTheme.typography.headlineSmall)
                Text("Har Call Ka Smart Saathi", color = MaterialTheme.colorScheme.primary)
                Text(status, style = MaterialTheme.typography.bodySmall)
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(tab == HomeTab.DIALER, { tab = HomeTab.DIALER }, icon = { Icon(Icons.Default.Phone, null) }, label = { Text("Dialer") })
                NavigationBarItem(tab == HomeTab.CONTACTS, { tab = HomeTab.CONTACTS }, icon = { Icon(Icons.Default.Contacts, null) }, label = { Text("Contacts") })
                NavigationBarItem(tab == HomeTab.HISTORY, { tab = HomeTab.HISTORY }, icon = { Icon(Icons.Default.History, null) }, label = { Text("History") })
                NavigationBarItem(tab == HomeTab.SETTINGS, { tab = HomeTab.SETTINGS }, icon = { Icon(Icons.Default.Settings, null) }, label = { Text("Setup") })
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
            when (tab) {
                HomeTab.DIALER -> {
                    OutlinedTextField(number, { number = it.filter { c -> c.isDigit() || c == '+' || c == '*' || c == '#' } },
                        label = { Text("Phone number") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { if (number.isNotBlank()) onDial(number) }, enabled = number.isNotBlank(), modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.Call, null); Spacer(Modifier.width(8.dp)); Text("Open phone dialer")
                    }
                    OutlinedButton(onClick = {
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                            .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            .putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
                            .putExtra(RecognizerIntent.EXTRA_PROMPT, "Contact ka naam ya phone number boliye")
                        try { speechLauncher.launch(intent) } catch (_: Exception) { status = "Speech recognition unavailable." }
                    }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.Mic, null); Spacer(Modifier.width(8.dp)); Text("Voice command")
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("Voice commands open the system dialer. Calls are never placed silently.")
                }
                HomeTab.CONTACTS -> {
                    OutlinedTextField(search, { search = it }, label = { Text("Search contacts") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    if (!hasPermission(Manifest.permission.READ_CONTACTS)) {
                        Button(onClick = { permissionLauncher.launch(arrayOf(Manifest.permission.READ_CONTACTS)) }) { Text("Grant Contacts access") }
                    }
                    LazyColumn {
                        items(contacts) { contact ->
                            ListItem(
                                headlineContent = { Text(contact.name) },
                                supportingContent = { Text(contact.number) },
                                trailingContent = { IconButton(onClick = { onDial(contact.number) }) { Icon(Icons.Default.Call, contentDescription = "Call contact") } },
                                modifier = Modifier.clickable { number = contact.number; tab = HomeTab.DIALER }
                            )
                            HorizontalDivider()
                        }
                    }
                }
                HomeTab.HISTORY -> {
                    if (!defaultRole) {
                        Text("Set this app as default Phone app to enable eligible call history.")
                        Button(onClick = requestDefaultDialer) { Text("Set as default Phone app") }
                    } else if (!hasPermission(Manifest.permission.READ_CALL_LOG)) {
                        Button(onClick = { permissionLauncher.launch(arrayOf(Manifest.permission.READ_CALL_LOG)) }) { Text("Grant call history permission") }
                    }
                    LazyColumn {
                        items(history) { entry ->
                            ListItem(
                                headlineContent = { Text(entry.name ?: entry.number) },
                                supportingContent = { Text(entry.typeLabel + " • " + entry.dateLabel + " • " + entry.durationSeconds + "s") },
                                trailingContent = { IconButton(onClick = { onDial(entry.number) }) { Icon(Icons.Default.Call, contentDescription = "Call back") } }
                            )
                            HorizontalDivider()
                        }
                    }
                }
                HomeTab.SETTINGS -> {
                    Text("Call Manager setup", style = MaterialTheme.typography.titleLarge)
                    Text(if (defaultRole) "Default Phone role: active" else "Default Phone role: not active")
                    Button(onClick = requestDefaultDialer, modifier = Modifier.fillMaxWidth()) { Text("Set as default Phone app") }
                    OutlinedButton(onClick = { permissionLauncher.launch(arrayOf(Manifest.permission.READ_CONTACTS)) }, modifier = Modifier.fillMaxWidth()) { Text("Contacts permission") }
                    OutlinedButton(onClick = {
                        if (defaultRole) permissionLauncher.launch(arrayOf(Manifest.permission.READ_CALL_LOG))
                        else status = "Set this app as default Phone app first."
                    }, modifier = Modifier.fillMaxWidth()) { Text("Call history permission") }
                    Spacer(Modifier.height(12.dp))
                    Text("Developer/Maintainer: Dipak Choudhary")
                    Text("Powered By: KAIxGenShopLabs")
                    Text("Call records stay on this device.")
                }
            }
        }
    }
}
// SECTION END: COMPOSE UI
// CREATOR SIGNATURE: Dipak Choudhary | KAIxGenShopLabs
