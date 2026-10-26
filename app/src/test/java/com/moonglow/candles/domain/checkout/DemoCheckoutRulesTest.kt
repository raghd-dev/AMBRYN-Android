package com.moonglow.candles.domain.checkout

import com.moonglow.candles.data.model.CartItem
import com.moonglow.candles.data.model.Product
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DemoCheckoutRulesTest {
    private fun item(price: Double, qty: Int = 1) = CartItem(
        product = Product(
            id = "test-$price", name = "Sample", category = "Demo",
            description = "", price = price, imageRes = 0
        ),
        quantity = qty
    )

    @Test fun emptyCartHasZeroSubtotalAndNoShipping() {
        val result = calculateDemoTotals(emptyList())
        assertEquals(0.0, result.subtotal, 0.001)
        assertEquals(0.0, result.shipping, 0.001)
        assertEquals(0.0, result.total, 0.001)
    }

    @Test fun oneItemUnderThresholdPaysShipping() {
        val result = calculateDemoTotals(listOf(item(24.0)))
        assertEquals(24.0, result.subtotal, 0.001)
        assertEquals(5.99, result.shipping, 0.001)
        assertEquals(29.99, result.total, 0.001)
    }

    @Test fun twoItemsUnderThresholdPayShipping() {
        assertEquals(53.99, calculateDemoTotals(listOf(item(24.0, 2))).total, 0.001)
    }

    @Test fun exactlyFiftyHasShippingPerExistingDemoRule() {
        assertEquals(5.99, calculateDemoTotals(listOf(item(50.0))).shipping, 0.001)
    }

    @Test fun moreThanFiftyHasFreeShipping() {
        assertEquals(0.0, calculateDemoTotals(listOf(item(50.01))).shipping, 0.001)
    }

    @Test fun multipleProductTotalsAreSummed() {
        val totals = calculateDemoTotals(listOf(item(24.0), item(28.0)))
        assertEquals(52.0, totals.subtotal, 0.001)
        assertEquals(52.0, totals.total, 0.001)
    }

    @Test fun quantityIsAppliedToSubtotal() {
        assertEquals(72.0, calculateDemoTotals(listOf(item(24.0, 3))).subtotal, 0.001)
    }

    @Test fun cartSubtotalUnder50AndMixedQuantities() {
        val totals = calculateDemoTotals(listOf(item(12.0, 2), item(10.0)))
        assertEquals(34.0, totals.subtotal, 0.001)
        assertEquals(39.99, totals.total, 0.001)
    }

    @Test fun rejectsBlankRecipientName() {
        assertEquals("Please enter your full name.", validateDemoDelivery(" ", "123", "Street"))
    }

    @Test fun rejectsBlankPhone() {
        assertEquals("Please enter your phone number.", validateDemoDelivery("Demo", "  ", "Street"))
    }

    @Test fun rejectsBlankAddress() {
        assertEquals("Please enter your delivery address.", validateDemoDelivery("Demo", "123", "  "))
    }

    @Test fun missingMultipleFieldsPrioritizesName() {
        assertEquals("Please enter your full name.", validateDemoDelivery("", "", ""))
    }

    @Test fun validDeliveryReturnsNoError() {
        assertNull(validateDemoDelivery("Demo Customer", "5550100", "123 Sample Street"))
    }
}
