package com.moonglow.candles.data.repository

import com.moonglow.candles.R
import android.util.Log
import com.moonglow.candles.data.model.*
import com.moonglow.candles.data.remote.*
import com.moonglow.candles.domain.checkout.calculateDemoTotals
import com.moonglow.candles.domain.cart.consolidateCartItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max

class MoonGlowRepository {

    private val orderService = SupabaseOrderService()
    private val cartService = SupabaseCartService()
    private val wishlistService = SupabaseWishlistService()
    private val roleService = SupabaseRoleService()
    private val reviewService = SupabaseReviewService()

    val products = listOf(
        Product("p1", "Vanilla Dream", "Sweet", "Warm vanilla with caramel and soft musk.", 24.0, R.drawable.vanilla_glow, 4.9, 128, 4.9, 0, listOf("Vanilla", "Caramel", "Musk"), isBestSeller = true),
        Product("p2", "Lavender Fields", "Floral", "Calming lavender and chamomile for quiet evenings.", 24.0, R.drawable.lavender_dream, 4.8, 96, 4.8, 0, listOf("Lavender", "Chamomile"), isBestSeller = true),
        Product("p3", "Sage & Citrus", "Fresh", "Fresh sage with bright citrus notes.", 24.0, R.drawable.fresh_linen, 4.7, 87, 4.7, 0, listOf("Sage", "Bergamot", "Lemon")),
        Product("p4", "Midnight Amber", "Woody", "Rich amber with woody notes and soft smoke.", 28.0, R.drawable.amber_night, 4.9, 112, 4.9, 0, listOf("Amber", "Cedar", "Smoke"), isBestSeller = true),
        Product("p5", "Rose Oud", "Floral", "Velvety rose blended with luxurious oud.", 32.0, R.drawable.rose_calm, 4.8, 74, 4.8, 0, listOf("Rose", "Oud", "Saffron")),
        Product("p6", "Cedar Hearth", "Woody", "Cozy cedarwood with warm spices.", 26.0, R.drawable.cedarwood, 4.6, 63, 4.6, 0, listOf("Cedar", "Clove", "Tonka"))
    )

    val kits = listOf(
        Kit("k1", "Beginner's Journey", "Beginner", 89.99, R.drawable.beginner_kit, listOf("Soy wax", "3 essential oils", "6 jars", "Video guide"), "Everything needed to create your first handmade candles."),
        Kit("k2", "Artisan's Collection", "Premium", 149.99, R.drawable.premium_kit, listOf("5 lb soy wax", "8 oils", "12 containers", "Business guide"), "A premium kit for serious makers and small businesses."),
        Kit("k3", "Friends Night Kit", "Group", 119.99, R.drawable.gift_kit, listOf("Group supplies", "Game cards", "Gift packaging"), "A cozy candle-making night for friends.")
    )

    private val _workshops = MutableStateFlow(
        listOf(
            Workshop("w1", "Beginner's Bliss", "Studio", 85.0, "25 May", "3 hours", 8, R.drawable.beginner_kit, "Learn the basics and pour two beautiful candles."),
            Workshop("w2", "Advanced Artistry", "Studio", 145.0, "02 Jun", "5 hours", 5, R.drawable.advanced_workshop, "Layering, color blending, embedding, and premium techniques."),
            Workshop("w3", "Online Candle Business", "Course", 69.0, "Anytime", "12 lessons", 999, R.drawable.workshop_studio, "Learn candle making, branding, pricing, and selling online.")
        )
    )
    val workshops: StateFlow<List<Workshop>> = _workshops.asStateFlow()

    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    // Visible feedback for network/auth failures; never log JWTs or response payloads.
    private val _cartSyncError = MutableStateFlow<String?>(null)
    val cartSyncError: StateFlow<String?> = _cartSyncError.asStateFlow()

    private val _wishlist = MutableStateFlow<Set<String>>(emptySet())
    val wishlist: StateFlow<Set<String>> = _wishlist.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    val bookings: StateFlow<List<Booking>> = _bookings.asStateFlow()

    private val _user = MutableStateFlow<UserProfile?>(null)
    val user: StateFlow<UserProfile?> = _user.asStateFlow()

    private val _userRole = MutableStateFlow("customer")
    val userRole: StateFlow<String> = _userRole.asStateFlow()

    private val _notifications = MutableStateFlow(
        listOf("Welcome to AMBRYN", "New DIY kits are available", "Workshop seats are limited")
    )
    val notifications: StateFlow<List<String>> = _notifications.asStateFlow()

    // Reviews State
    private val _productReviews = MutableStateFlow<Map<String, List<ProductReview>>>(emptyMap())
    val productReviews: StateFlow<Map<String, List<ProductReview>>> = _productReviews.asStateFlow()

    fun setLoggedInUser(name: String, email: String) {
        _cartSyncError.value = null
        _user.value = UserProfile(name = name, email = email)

        CoroutineScope(Dispatchers.IO).launch {
            cartService.fetchCart(email, products)
                .onSuccess {
                    _cart.value = consolidateCartItems(it)
                }

            wishlistService.fetchWishlist(email)
                .onSuccess {
                    _wishlist.value = it
                }

            roleService.getRole(email)
                .onSuccess {
                    _userRole.value = it
                }
                .onFailure {
                    _userRole.value = "customer"
                }

            // Load reviews for all products
            products.forEach { product ->
                loadProductReviews(product.id)
            }
        }

        addNotification("Welcome back, $name!")
    }

    fun logout() {
        _cartSyncError.value = null
        _user.value = null
        _userRole.value = "customer"
        _cart.value = emptyList()
        _wishlist.value = emptySet()
        addNotification("You have logged out")
    }

    fun addToCart(product: Product) {
        _cart.value = consolidateCartItems(_cart.value).toMutableList().also { list ->
            val index = list.indexOfFirst { it.product.id == product.id }

            if (index >= 0) {
                list[index] = list[index].copy(quantity = list[index].quantity + 1)
            } else {
                list.add(CartItem(product = product, quantity = 1))
            }
        }

        syncCartOnline()
        addNotification("${product.name} added to cart")
    }

    fun addKitToCart(kit: Kit) {
        addToCart(
            Product(
                id = kit.id,
                name = kit.name,
                category = "DIY Kit",
                description = kit.description,
                price = kit.price,
                imageRes = kit.imageRes,
                rating = 4.9,
                reviews = 120,
                avgRating = 4.9,
                reviewCount = 0,
                scentNotes = kit.includes,
                burnTime = "Creative Experience"
            )
        )
    }

    fun changeQty(productId: String, delta: Int) {
        _cart.value = consolidateCartItems(_cart.value).mapNotNull { item ->
            if (item.product.id == productId) {
                item.copy(quantity = item.quantity + delta).takeIf { it.quantity > 0 }
            } else {
                item
            }
        }

        syncCartOnline()
    }

    fun removeFromCart(productId: String) {
        _cart.value = _cart.value.filterNot { it.product.id == productId }
        syncCartOnline()
        addNotification("Item removed from cart")
    }

    fun clearCart() {
        _cart.value = emptyList()
        syncCartOnline()
    }

    fun toggleWishlist(id: String) {
        _wishlist.value = _wishlist.value.toMutableSet().also {
            if (!it.add(id)) {
                it.remove(id)
                addNotification("Removed from wishlist")
            } else {
                addNotification("Added to wishlist")
            }
        }

        syncWishlistOnline()
    }

    fun bookWorkshop(workshopId: String): Result<Unit> {
        val workshop = _workshops.value.firstOrNull { it.id == workshopId }
            ?: return Result.failure(IllegalArgumentException("Workshop not found."))

        if (workshop.seatsLeft <= 0) {
            return Result.failure(IllegalStateException("No seats left for this workshop."))
        }

        _workshops.value = _workshops.value.map {
            if (it.id == workshopId) {
                it.copy(seatsLeft = max(0, it.seatsLeft - 1))
            } else {
                it
            }
        }

        _bookings.value = listOf(
            Booking(
                id = "BK-${System.currentTimeMillis().toString().takeLast(5)}",
                workshopTitle = workshop.title,
                dateLabel = workshop.dateLabel,
                seats = 1,
                status = "Reserved"
            )
        ) + _bookings.value

        addNotification("Seat reserved for ${workshop.title}")

        return Result.success(Unit)
    }

    suspend fun placeOrder(
        name: String,
        address: String,
        paymentMethod: String
    ): Result<Order> {
        if (_cart.value.isEmpty()) {
            return Result.failure(IllegalStateException("Your cart is empty."))
        }

        if (name.isBlank() || address.isBlank()) {
            return Result.failure(IllegalArgumentException("Please complete shipping details."))
        }

        val customerEmail = _user.value?.email
            ?: return Result.failure(IllegalStateException("Please login before checkout."))

        val totals = calculateDemoTotals(_cart.value)

        val order = Order(
            id = "AM-${java.util.UUID.randomUUID()}",
            items = _cart.value,
            total = totals.total,
            status = "Processing",
            dateLabel = SimpleDateFormat("dd MMM yyyy", Locale.US).format(Date()),
            paymentMethod = paymentMethod,
            address = address
        )

        // Do not report order completion or clear the cart until Supabase confirms it.
        val saved = orderService.saveOrder(order, name, customerEmail)
        if (saved.isFailure) return Result.failure(saved.exceptionOrNull()!!)

        _orders.value = listOf(order) + _orders.value
        clearCart()
        addNotification("Demo order ${order.id} saved successfully")
        return Result.success(order)
    }

    suspend fun submitReview(
        productId: String,
        rating: Int,
        reviewText: String
    ): Result<Unit> {
        val user = _user.value
        if (user == null) {
            return Result.failure(Exception("Please login to submit a review"))
        }

        val result = reviewService.submitReview(
            productId = productId,
            userEmail = user.email,
            userName = user.name,
            rating = rating,
            reviewText = reviewText
        )

        if (result.isSuccess) {
            loadProductReviews(productId)
            addNotification("Thank you for your review!")
        }

        return result
    }
    fun loadProductReviews(productId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            reviewService.getProductReviews(productId).onSuccess { reviews ->
                _productReviews.value = _productReviews.value.toMutableMap().apply {
                    this[productId] = reviews
                }
            }
        }
    }

    fun getProductReviews(productId: String): List<ProductReview> {
        return _productReviews.value[productId] ?: emptyList()
    }

    fun getAverageRating(productId: String): Double {
        val reviews = getProductReviews(productId)
        if (reviews.isEmpty()) {
            val product = products.find { it.id == productId }
            return product?.avgRating ?: 0.0
        }
        return reviews.map { it.rating }.average()
    }

    fun getReviewCount(productId: String): Int {
        val reviews = getProductReviews(productId)
        if (reviews.isEmpty()) {
            val product = products.find { it.id == productId }
            return product?.reviewCount ?: 0
        }
        return reviews.size
    }

    fun retryCartSync() = syncCartOnline()

    private fun syncCartOnline() {
        val email = _user.value?.email
        if (email == null) {
            _cartSyncError.value = "Sign in to save your cart online."
            Log.w("AMBRYN_CART_SYNC", "saveCart skipped: no signed-in app user")
            return
        }

        val snapshot = consolidateCartItems(_cart.value)
        _cartSyncError.value = null
        CoroutineScope(Dispatchers.IO).launch {
            cartService.saveCart(email = email, cart = snapshot)
                .onSuccess {
                    _cartSyncError.value = null
                    Log.d("AMBRYN_CART_SYNC", "saveCart completed successfully")
                }
                .onFailure { error ->
                    _cartSyncError.value =
                        "Your cart is visible here but was not saved online. Tap Retry."
                    // Only a safe category / HTTP status is logged, never response bodies,
                    // tokens, emails, product details or customer identifiers.
                    val message = error.message.orEmpty()
                    val httpStatus = Regex("Supabase HTTP ([0-9]{3})")
                        .find(message)?.groupValues?.get(1)
                    val reason = when {
                        httpStatus != null -> "HTTP $httpStatus"
                        message.contains("Session expired", true) -> "session expired"
                        message.contains("log in", true) -> "not authenticated"
                        message.contains("Cart owner mismatch", true) -> "identity mismatch"
                        message.contains("configured", true) -> "configuration error"
                        else -> error.javaClass.simpleName
                    }
                    Log.e("AMBRYN_CART_SYNC", "saveCart failed: $reason")
                }
        }
    }

    private fun syncWishlistOnline() {
        val email = _user.value?.email ?: return

        CoroutineScope(Dispatchers.IO).launch {
            wishlistService.saveWishlist(
                email = email,
                ids = _wishlist.value
            )
        }
    }

    private fun addNotification(message: String) {
        _notifications.value = listOf(message) + _notifications.value.take(9)
    }
}