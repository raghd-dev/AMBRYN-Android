package com.moonglow.candles.domain.cart

import com.moonglow.candles.data.model.CartItem
import com.moonglow.candles.data.model.Product
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CartItemRulesTest {
    private fun item(id: String, quantity: Int) = CartItem(
        Product(id, "Sample $id", "Candle", "Demo", 24.0, 0),
        quantity
    )

    @Test fun duplicateProductRowsMergeIntoOneAndSumQuantities() {
        val items = consolidateCartItems(listOf(item("p1", 1), item("p2", 1), item("p1", 2)))
        assertEquals(2, items.size)
        assertEquals("p1", items[0].product.id)
        assertEquals(3, items[0].quantity)
        assertEquals("p2", items[1].product.id)
        assertEquals(1, items[1].quantity)
    }

    @Test fun ignoresInvalidOrZeroQuantities() {
        val items = consolidateCartItems(listOf(item("p1", 0), item("p2", -1)))
        assertTrue(items.isEmpty())
    }

    @Test fun emptyCartStaysEmpty() {
        assertTrue(consolidateCartItems(emptyList()).isEmpty())
    }
}
