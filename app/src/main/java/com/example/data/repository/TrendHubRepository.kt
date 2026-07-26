package com.example.data.repository

import com.example.data.db.TrendHubDatabase
import com.example.data.entity.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlin.random.Random

class TrendHubRepository(private val db: TrendHubDatabase) {

  val allProducts: Flow<List<ProductEntity>> = db.productDao().getAllProducts()
  val allCategories: Flow<List<CategoryEntity>> = db.categoryDao().getAllCategories()
  val cartItems: Flow<List<CartItemEntity>> = db.cartDao().getCartItems()
  val wishlistItems: Flow<List<WishlistItemEntity>> = db.wishlistDao().getWishlistItems()
  val allOrders: Flow<List<OrderEntity>> = db.orderDao().getAllOrders()
  val activeCoupons: Flow<List<CouponEntity>> = db.couponDao().getActiveCoupons()
  val activeBanners: Flow<List<AdBannerEntity>> = db.adBannerDao().getActiveBanners()
  val allBanners: Flow<List<AdBannerEntity>> = db.adBannerDao().getAllBanners()
  val allAddresses: Flow<List<UserAddressEntity>> = db.addressDao().getAllAddresses()

  fun getProductById(id: Int): Flow<ProductEntity?> = db.productDao().getProductById(id)
  fun getProductsByCategory(category: String): Flow<List<ProductEntity>> = db.productDao().getProductsByCategory(category)
  fun searchProducts(query: String): Flow<List<ProductEntity>> = db.productDao().searchProducts(query)
  fun getTrendingProducts(): Flow<List<ProductEntity>> = db.productDao().getTrendingProducts()
  fun getBestSellers(): Flow<List<ProductEntity>> = db.productDao().getBestSellers()
  fun getFlashSaleProducts(): Flow<List<ProductEntity>> = db.productDao().getFlashSaleProducts()
  fun getDealsOfDay(): Flow<List<ProductEntity>> = db.productDao().getDealsOfDay()
  fun getReviewsForProduct(productId: Int): Flow<List<ReviewEntity>> = db.reviewDao().getReviewsForProduct(productId)
  fun isFavorite(productId: Int): Flow<Boolean> = db.wishlistDao().isFavorite(productId)

  suspend fun addToCart(product: ProductEntity, color: String = "Titanium Gray", storage: String = "256GB") {
    val currentCart = cartItems.first()
    val existing = currentCart.find { it.productId == product.id && it.selectedColor == color && it.selectedStorage == storage }
    if (existing != null) {
      db.cartDao().updateCartItem(existing.copy(quantity = existing.quantity + 1))
    } else {
      db.cartDao().insertCartItem(
        CartItemEntity(
          productId = product.id,
          productName = product.name,
          productBrand = product.brand,
          productPrice = product.price,
          productImageUrl = product.imageUrl,
          quantity = 1,
          selectedColor = color,
          selectedStorage = storage,
          modelType = product.modelType
        )
      )
    }
  }

  suspend fun updateCartQuantity(cartItemId: Int, newQuantity: Int) {
    if (newQuantity <= 0) {
      db.cartDao().deleteCartItem(cartItemId)
    } else {
      val currentCart = cartItems.first()
      val item = currentCart.find { it.id == cartItemId }
      if (item != null) {
        db.cartDao().updateCartItem(item.copy(quantity = newQuantity))
      }
    }
  }

  suspend fun removeFromCart(cartItemId: Int) {
    db.cartDao().deleteCartItem(cartItemId)
  }

  suspend fun clearCart() {
    db.cartDao().clearCart()
  }

  suspend fun toggleWishlist(productId: Int) {
    val current = wishlistItems.first()
    val exists = current.any { it.productId == productId }
    if (exists) {
      db.wishlistDao().deleteByProductId(productId)
    } else {
      db.wishlistDao().insertWishlistItem(WishlistItemEntity(productId = productId))
    }
  }

  suspend fun applyCoupon(code: String): CouponEntity? {
    return db.couponDao().getCouponByCode(code.trim().uppercase())
  }

  suspend fun placeOrder(
    totalAmount: Double,
    discountAmount: Double,
    taxAmount: Double,
    shippingCost: Double,
    finalAmount: Double,
    address: String,
    paymentMethod: String,
    itemsSummary: String
  ): OrderEntity {
    val orderNum = "ORD-" + Random.nextInt(100000, 999999)
    val order = OrderEntity(
      orderNumber = orderNum,
      totalAmount = totalAmount,
      discountAmount = discountAmount,
      taxAmount = taxAmount,
      shippingCost = shippingCost,
      finalAmount = finalAmount,
      status = "Order Placed",
      itemsSummary = itemsSummary,
      shippingAddress = address,
      paymentMethod = paymentMethod,
      trackingStep = 1
    )
    db.orderDao().insertOrder(order)
    db.cartDao().clearCart()
    return order
  }

  suspend fun addReview(productId: Int, userName: String, rating: Int, comment: String) {
    db.reviewDao().insertReview(
      ReviewEntity(
        productId = productId,
        userName = userName,
        userAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150&q=80",
        rating = rating,
        comment = comment,
        date = "Just now",
        isVerifiedBuyer = true
      )
    )
  }

  suspend fun saveAddress(address: UserAddressEntity) {
    db.addressDao().insertAddress(address)
  }

  // Admin Actions
  suspend fun saveProduct(product: ProductEntity) {
    if (product.id == 0) {
      db.productDao().insertProduct(product)
    } else {
      db.productDao().updateProduct(product)
    }
  }

  suspend fun deleteProduct(product: ProductEntity) {
    db.productDao().deleteProduct(product)
  }

  suspend fun saveBanner(banner: AdBannerEntity) {
    db.adBannerDao().insertBanner(banner)
  }

  suspend fun deleteBanner(banner: AdBannerEntity) {
    db.adBannerDao().deleteBanner(banner)
  }

  suspend fun updateOrderStatus(order: OrderEntity, newStatus: String, newStep: Int) {
    db.orderDao().updateOrder(order.copy(status = newStatus, trackingStep = newStep))
  }

  suspend fun createCoupon(coupon: CouponEntity) {
    db.couponDao().insertCoupon(coupon)
  }
}
