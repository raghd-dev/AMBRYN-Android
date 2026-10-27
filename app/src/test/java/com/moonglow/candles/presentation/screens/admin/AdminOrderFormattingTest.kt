package com.moonglow.candles.presentation.screens.admin

import org.junit.Assert.assertEquals
import org.junit.Test

class AdminOrderFormattingTest {
    @Test fun brandedOrderIdIsShortAndReadable() {
        assertEquals("#4DD32909", shortAdminOrderReference("AM-4dd32909-780c-46a0-aaa4-d40080ffb2a0"))
    }

    @Test fun plainUuidIsShortened() {
        assertEquals("#4DD32909", shortAdminOrderReference("4dd32909-780c-46a0-aaa4-d40080ffb2a0"))
    }

    @Test fun blankIdHasFallback() {
        assertEquals("#UNKNOWN", shortAdminOrderReference("  "))
    }

    @Test fun shortCustomIdIsPreserved() {
        assertEquals("#ABC123", shortAdminOrderReference("abc123"))
    }
}
