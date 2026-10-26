package com.moonglow.candles.data.model

data class Product(
    val id: String,
    val name: String,
    val category: String,
    val description: String,
    val price: Double,
    val imageRes: Int,
    val rating: Double = 4.5,
    val reviews: Int = 0,
    val avgRating: Double = rating,  // Added field
    val reviewCount: Int = reviews,   // Added field
    val scentNotes: List<String> = emptyList(),
    val burnTime: String = "50+ hours",
    val isBestSeller: Boolean = false
)

data class Kit(
    val id: String,
    val name: String,
    val level: String,
    val price: Double,
    val imageRes: Int,
    val includes: List<String>,
    val description: String
)

data class Workshop(
    val id: String,
    val title: String,
    val type: String,
    val price: Double,
    val dateLabel: String,
    val duration: String,
    val seatsLeft: Int,
    val imageRes: Int,
    val description: String
)

data class CartItem(
    val product: Product,
    val quantity: Int
)

data class UserProfile(
    val name: String = "AMBRYN Guest",
    val email: String = "guest@example.com",
    val points: Int = 1250,
    val memberTier: String = "Gold Member"
)

data class Order(
    val id: String,
    val items: List<CartItem>,
    val total: Double,
    val status: String,
    val dateLabel: String,
    val paymentMethod: String,
    val address: String
)

data class Booking(
    val id: String,
    val workshopTitle: String,
    val dateLabel: String,
    val seats: Int,
    val status: String
)
