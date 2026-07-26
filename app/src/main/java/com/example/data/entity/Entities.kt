package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val name: String,
  val brand: String,
  val category: String, // e.g. "Smartphones", "Laptops", "Audio", "Cameras", "Gaming", "Wearables", "Accessories", "Smart Home"
  val price: Double,
  val originalPrice: Double,
  val rating: Double,
  val reviewsCount: Int,
  val description: String,
  val specs: String, // Comma or newline separated specs
  val imageUrl: String,
  val isTrending: Boolean = false,
  val isBestSeller: Boolean = false,
  val isFlashSale: Boolean = false,
  val isNewArrival: Boolean = false,
  val isDealOfDay: Boolean = false,
  val stock: Int = 10,
  val discountPercentage: Int = 0,
  val modelType: String = "PHONE", // "PHONE", "LAPTOP", "AUDIO", "CAMERA", "DRONE", "WATCH", "CHAIR", "DEFAULT"
  val availableColors: String = "Titanium Gray,Cyber Cyan,Obsidian Black,Neon Purple",
  val availableStorage: String = "128GB,256GB,512GB,1TB"
)

@Entity(tableName = "categories")
data class CategoryEntity(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val name: String,
  val iconName: String,
  val itemCount: Int
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val productId: Int,
  val productName: String,
  val productBrand: String,
  val productPrice: Double,
  val productImageUrl: String,
  val quantity: Int = 1,
  val selectedColor: String = "Titanium Gray",
  val selectedStorage: String = "256GB",
  val modelType: String = "PHONE"
)

@Entity(tableName = "wishlist_items")
data class WishlistItemEntity(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val productId: Int,
  val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class OrderEntity(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val orderNumber: String,
  val userId: String = "user_101",
  val totalAmount: Double,
  val discountAmount: Double,
  val taxAmount: Double,
  val shippingCost: Double,
  val finalAmount: Double,
  val status: String, // "Order Placed", "Payment Verified", "Packed at Warehouse", "Shipped via Express", "Out for Delivery", "Delivered"
  val itemsSummary: String,
  val shippingAddress: String,
  val paymentMethod: String,
  val createdAt: Long = System.currentTimeMillis(),
  val trackingStep: Int = 1
)

@Entity(tableName = "coupons")
data class CouponEntity(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val code: String,
  val discountPercent: Int,
  val minOrderAmount: Double,
  val description: String,
  val isActive: Boolean = true
)

@Entity(tableName = "ad_banners")
data class AdBannerEntity(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val title: String,
  val subtitle: String,
  val imageUrl: String,
  val targetCategory: String,
  val adType: String, // "TOP_BANNER", "SIDEBAR", "CAROUSEL", "POPUP", "OFFER"
  val discountBadge: String,
  val isActive: Boolean = true
)

@Entity(tableName = "reviews")
data class ReviewEntity(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val productId: Int,
  val userName: String,
  val userAvatar: String,
  val rating: Int,
  val comment: String,
  val date: String,
  val isVerifiedBuyer: Boolean = true
)

@Entity(tableName = "addresses")
data class UserAddressEntity(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val fullName: String,
  val phone: String,
  val streetAddress: String,
  val city: String,
  val state: String,
  val zipCode: String,
  val isDefault: Boolean = false
)
