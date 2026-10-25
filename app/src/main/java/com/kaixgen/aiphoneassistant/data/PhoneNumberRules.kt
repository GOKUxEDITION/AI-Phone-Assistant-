/*
 * FILE INFORMATION: PhoneNumberRules.kt
 * Project: AI Phone Assistant
 * Purpose: Validate phone number input before handing it to Android's dialer.
 * Developer/Maintainer: Dipak Choudhary | Powered By: KAIxGenShopLabs
 * CREATOR SIGNATURE: Dipak Choudhary
 * SECTION START: PHONE NUMBER RULES
 */
package com.kaixgen.aiphoneassistant.data

object PhoneNumberRules {
    fun normalizeInput(input: String): String =
        input.filterIndexed { index, char ->
            char.isDigit() || char == '*' || char == '#' || (char == '+' && index == 0)
        }

    fun isDialable(input: String): Boolean =
        normalizeInput(input).any(Char::isDigit) &&
            normalizeInput(input).length <= 40
}
// SECTION END: PHONE NUMBER RULES
// CREATOR SIGNATURE: Dipak Choudhary | KAIxGenShopLabs
