/*
 * FILE INFORMATION: PhoneNumberRulesTest.kt
 * Project: AI Phone Assistant
 * Purpose: Unit tests for dialer input normalization.
 * Developer/Maintainer: Dipak Choudhary | Powered By: KAIxGenShopLabs
 * CREATOR SIGNATURE: Dipak Choudhary
 * SECTION START: UNIT TESTS
 */
package com.kaixgen.aiphoneassistant.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PhoneNumberRulesTest {
    @Test
    fun keepsLeadingPlusAndDialCharacters() {
        assertEquals("+919876543210", PhoneNumberRules.normalizeInput("+91 98765-43210"))
    }

    @Test
    fun rejectsInputWithoutDigits() {
        assertFalse(PhoneNumberRules.isDialable("+#*"))
        assertTrue(PhoneNumberRules.isDialable("*123#"))
    }

    @Test
    fun rejectsExcessivelyLongInput() {
        assertFalse(PhoneNumberRules.isDialable("1".repeat(41)))
    }
}
// SECTION END: UNIT TESTS
// CREATOR SIGNATURE: Dipak Choudhary | KAIxGenShopLabs
