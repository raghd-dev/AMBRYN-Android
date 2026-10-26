package com.moonglow.candles.presentation.components

import org.junit.Assert.assertEquals
import org.junit.Test

class PriceFormatTest {
    @Test fun zeroShowsTwoDecimals() = assertEquals("$0.00", formatPrice(0.0))
    @Test fun wholeDollarShowsTwoDecimals() = assertEquals("$24.00", formatPrice(24.0))
    @Test fun centsAreShown() = assertEquals("$89.99", formatPrice(89.99))
    @Test fun roundingShowsTwoDecimals() = assertEquals("$24.13", formatPrice(24.125))
}
