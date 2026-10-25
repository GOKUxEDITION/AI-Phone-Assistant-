/*
 * FILE INFORMATION: PhoneDataRepository.kt
 * Project: AI Phone Assistant
 * Purpose: Read on-device contacts and eligible system call history.
 * Developer/Maintainer: Dipak Choudhary | Powered By: KAIxGenShopLabs
 * CREATOR SIGNATURE: Dipak Choudhary
 * SECTION START: DATA MODELS AND REPOSITORY
 */
package com.kaixgen.aiphoneassistant.data

import android.content.Context
import android.provider.CallLog
import android.provider.ContactsContract
import java.text.DateFormat
import java.util.Date

data class ContactEntry(val id: String, val name: String, val number: String)
data class CallEntry(
    val number: String,
    val name: String?,
    val typeLabel: String,
    val dateLabel: String,
    val durationSeconds: Long
)

class PhoneDataRepository(val context: Context) {
    // SECTION START: CONTACTS PROVIDER
    fun searchContacts(query: String): List<ContactEntry> {
        val output = mutableListOf<ContactEntry>()
        val resolver = context.contentResolver
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        val selection = if (query.isBlank()) null else
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ? OR " +
                ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE ?"
        val args = if (query.isBlank()) null else arrayOf("%$query%", "%$query%")
        try {
            resolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, selection, args,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE NOCASE ASC"
            )?.use { cursor ->
                val idCol = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                val nameCol = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberCol = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                while (cursor.moveToNext()) {
                    if (nameCol >= 0 && numberCol >= 0) {
                        val name = cursor.getString(nameCol).orEmpty()
                        val number = cursor.getString(numberCol).orEmpty()
                        if (number.isNotBlank()) output += ContactEntry(
                            if (idCol >= 0) cursor.getString(idCol).orEmpty() else number,
                            name.ifBlank { number }, number
                        )
                    }
                }
            }
        } catch (_: SecurityException) {
            // Permission may be revoked while the query is running.
        }
        return output.distinctBy { it.number }.take(500)
    }
    // SECTION END: CONTACTS PROVIDER

    // SECTION START: SYSTEM CALL HISTORY
    fun readCallHistory(limit: Int = 200): List<CallEntry> {
        val output = mutableListOf<CallEntry>()
        val projection = arrayOf(
            CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME,
            CallLog.Calls.TYPE, CallLog.Calls.DATE, CallLog.Calls.DURATION
        )
        try {
            context.contentResolver.query(
                CallLog.Calls.CONTENT_URI, projection, null, null,
                CallLog.Calls.DATE + " DESC"
            )?.use { cursor ->
                val numberCol = cursor.getColumnIndex(CallLog.Calls.NUMBER)
                val nameCol = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)
                val typeCol = cursor.getColumnIndex(CallLog.Calls.TYPE)
                val dateCol = cursor.getColumnIndex(CallLog.Calls.DATE)
                val durationCol = cursor.getColumnIndex(CallLog.Calls.DURATION)
                var count = 0
                while (cursor.moveToNext() && count < limit) {
                    val number = if (numberCol >= 0) cursor.getString(numberCol).orEmpty() else ""
                    val name = if (nameCol >= 0) cursor.getString(nameCol) else null
                    val type = if (typeCol >= 0) cursor.getInt(typeCol) else -1
                    val date = if (dateCol >= 0) cursor.getLong(dateCol) else 0L
                    val duration = if (durationCol >= 0) cursor.getLong(durationCol) else 0L
                    val label = when (type) {
                        CallLog.Calls.INCOMING_TYPE -> "Incoming"
                        CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
                        CallLog.Calls.MISSED_TYPE -> "Missed"
                        CallLog.Calls.REJECTED_TYPE -> "Rejected"
                        CallLog.Calls.BLOCKED_TYPE -> "Blocked"
                        else -> "Call"
                    }
                    output += CallEntry(number, name, label, DateFormat.getDateTimeInstance().format(Date(date)), duration)
                    count++
                }
            }
        } catch (_: SecurityException) {
            // Call log remains unavailable unless Android grants access.
        }
        return output
    }
    // SECTION END: SYSTEM CALL HISTORY
}
// SECTION END: DATA MODELS AND REPOSITORY
// CREATOR SIGNATURE: Dipak Choudhary | KAIxGenShopLabs
