package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.TrendHubDatabase
import com.example.data.entity.*
import com.example.data.repository.TrendHubRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class RazorpayStatus { IDLE, WAITING_FOR_OTP, SUCCESS, FAILED }

class TrendHubViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: TrendHubRepository

  init {
    val db = TrendHubDatabase.getDatabase(application)
    TrendHubDatabase.seedDatabaseIfEmpty(db)
    repository = TrendHubRepository(db)
  }

  // Raw Database Flows
  val allProducts: StateFlow<List<ProductEntity>> = repository.allProducts
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val categories: StateFlow<List<CategoryEntity>> = repository.allCategories
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val cartItems: StateFlow<List<CartItemEntity>> = repository.cartItems
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val wishlistItems: StateFlow<List<WishlistItemEntity>> = repository.wishlistItems
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val orders: StateFlow<List<OrderEntity>> = repository.allOrders
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val activeCoupons: StateFlow<List<CouponEntity>> = repository.activeCoupons
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val activeBanners: StateFlow<List<AdBannerEntity>> = repository.activeBanners
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allBanners: StateFlow<List<AdBannerEntity>> = repository.allBanners
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val addresses: StateFlow<List<UserAddressEntity>> = repository.allAddresses
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // UI States & Filtering
  private val _selectedCategory = MutableStateFlow<String?>(null)
  val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _sortBy = MutableStateFlow("POPULARITY") // "POPULARITY", "PRICE_LOW_HIGH", "PRICE_HIGH_LOW", "NEWEST", "RATING"
  val sortBy: StateFlow<String> = _sortBy.asStateFlow()

  private val _maxPrice = MutableStateFlow(4000f)
  val maxPrice: StateFlow<Float> = _maxPrice.asStateFlow()

  private val _minRating = MutableStateFlow(0f)
  val minRating: StateFlow<Float> = _minRating.asStateFlow()

  private val _isAdminMode = MutableStateFlow(false)
  val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

  private val _appliedCoupon = MutableStateFlow<CouponEntity?>(null)
  val appliedCoupon: StateFlow<CouponEntity?> = _appliedCoupon.asStateFlow()

  private val _couponMessage = MutableStateFlow<String?>(null)
  val couponMessage: StateFlow<String?> = _couponMessage.asStateFlow()

  private val _activePopupAd = MutableStateFlow<AdBannerEntity?>(null)
  val activePopupAd: StateFlow<AdBannerEntity?> = _activePopupAd.asStateFlow()

  // Razorpay Simulation & Auth State
  private val _razorpayStatus = MutableStateFlow(RazorpayStatus.IDLE)
  val razorpayStatus: StateFlow<RazorpayStatus> = _razorpayStatus.asStateFlow()

  private val _otpInput = MutableStateFlow("")
  val otpInput: StateFlow<String> = _otpInput.asStateFlow()

  val currentUserEmail: String = "sabirrayabka@gmail.com"

  init {
    // Show a popup ad shortly after start if available
    viewModelScope.launch {
      kotlinx.coroutines.delay(4000)
      val banners = repository.activeBanners.first()
      val popup = banners.find { it.adType == "POPUP" }
      if (popup != null) {
        _activePopupAd.value = popup
      }
    }
  }

  // Filtered Products
  val filteredProducts: StateFlow<List<ProductEntity>> = combine(
    allProducts,
    _selectedCategory,
    _searchQuery,
    _sortBy,
    _maxPrice,
    _minRating
  ) { args ->
    @Suppress("UNCHECKED_CAST")
    val products = args[0] as List<ProductEntity>
    val cat = args[1] as String?
    val query = args[2] as String
    val sort = args[3] as String
    val maxPr = (args[4] as Float).toDouble()
    val minRat = (args[5] as Float).toDouble()

    var list = products

    if (cat != null) {
      list = list.filter { it.category.equals(cat, ignoreCase = true) }
    }

    if (query.isNotBlank()) {
      val q = query.trim().lowercase()
      list = list.filter {
        it.name.lowercase().contains(q) ||
        it.brand.lowercase().contains(q) ||
        it.category.lowercase().contains(q)
      }
    }

    list = list.filter { it.price <= maxPr && it.rating >= minRat }

    when (sort) {
      "PRICE_LOW_HIGH" -> list.sortedBy { it.price }
      "PRICE_HIGH_LOW" -> list.sortedByDescending { it.price }
      "NEWEST" -> list.sortedByDescending { it.id }
      "RATING" -> list.sortedByDescending { it.rating }
      else -> list.sortedByDescending { it.reviewsCount }
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun setCategory(category: String?) {
    if (_selectedCategory.value == category) {
      _selectedCategory.value = null // Toggle off if clicked again
    } else {
      _selectedCategory.value = category
    }
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setSortBy(sort: String) {
    _sortBy.value = sort
  }

  fun setMaxPrice(price: Float) {
    _maxPrice.value = price
  }

  fun setMinRating(rating: Float) {
    _minRating.value = rating
  }

  fun toggleRoleMode() {
    _isAdminMode.value = !_isAdminMode.value
  }

  fun dismissPopupAd() {
    _activePopupAd.value = null
  }

  // Cart actions
  fun addToCart(product: ProductEntity, color: String = "Titanium Gray", storage: String = "256GB") {
    viewModelScope.launch {
      repository.addToCart(product, color, storage)
    }
  }

  fun updateCartQuantity(cartItemId: Int, newQuantity: Int) {
    viewModelScope.launch {
      repository.updateCartQuantity(cartItemId, newQuantity)
    }
  }

  fun removeFromCart(cartItemId: Int) {
    viewModelScope.launch {
      repository.removeFromCart(cartItemId)
    }
  }

  fun clearCart() {
    viewModelScope.launch {
      repository.clearCart()
      _appliedCoupon.value = null
    }
  }

  // Wishlist actions
  fun toggleWishlist(product: ProductEntity) {
    viewModelScope.launch {
      repository.toggleWishlist(product.id)
    }
  }

  fun isFavorite(productId: Int): Flow<Boolean> = repository.isFavorite(productId)

  // Coupon actions
  fun applyCouponCode(code: String) {
    viewModelScope.launch {
      val coupon = repository.applyCoupon(code)
      if (coupon != null) {
        val subtotal = cartItems.value.sumOf { it.productPrice * it.quantity }
        if (subtotal >= coupon.minOrderAmount) {
          _appliedCoupon.value = coupon
          _couponMessage.value = "✅ Code '${coupon.code}' applied! Saved ${coupon.discountPercent}%"
        } else {
          _couponMessage.value = "⚠️ Minimum order amount for '${coupon.code}' is $${coupon.minOrderAmount}"
        }
      } else {
        _couponMessage.value = "❌ Invalid or expired coupon code."
      }
    }
  }

  fun clearCouponMessage() {
    _couponMessage.value = null
  }

  // Razorpay Checkout Simulation
  fun initiateRazorpayCheckout() {
    _razorpayStatus.value = RazorpayStatus.WAITING_FOR_OTP
    _otpInput.value = ""
  }

  fun setOtpInput(code: String) {
    if (code.length <= 4) {
      _otpInput.value = code
    }
  }

  fun verifyRazorpayOtpAndPlaceOrder(
    address: String,
    paymentMethod: String,
    onSuccess: (OrderEntity) -> Unit
  ) {
    viewModelScope.launch {
      if (_otpInput.value == "1234" || _otpInput.value.length == 4) {
        _razorpayStatus.value = RazorpayStatus.SUCCESS

        val items = cartItems.value
        val subtotal = items.sumOf { it.productPrice * it.quantity }
        val discount = if (_appliedCoupon.value != null) subtotal * (_appliedCoupon.value!!.discountPercent / 100.0) else 0.0
        val tax = (subtotal - discount) * 0.08
        val shipping = if (subtotal > 100.0 || _appliedCoupon.value?.code == "FREEFLY") 0.0 else 15.0
        val finalTotal = (subtotal - discount + tax + shipping).coerceAtLeast(0.0)

        val summary = items.joinToString(", ") { "${it.productName} (${it.selectedColor}) x${it.quantity}" }

        val newOrder = repository.placeOrder(
          totalAmount = subtotal,
          discountAmount = discount,
          taxAmount = tax,
          shippingCost = shipping,
          finalAmount = finalTotal,
          address = address,
          paymentMethod = paymentMethod,
          itemsSummary = summary
        )

        _appliedCoupon.value = null
        onSuccess(newOrder)
      } else {
        _razorpayStatus.value = RazorpayStatus.FAILED
      }
    }
  }

  fun resetRazorpayStatus() {
    _razorpayStatus.value = RazorpayStatus.IDLE
  }

  // Reviews
  fun addReview(productId: Int, userName: String, rating: Int, comment: String) {
    viewModelScope.launch {
      repository.addReview(productId, userName, rating, comment)
    }
  }

  fun getReviewsForProduct(productId: Int): Flow<List<ReviewEntity>> = repository.getReviewsForProduct(productId)

  // Address
  fun addAddress(fullName: String, phone: String, street: String, city: String, state: String, zip: String) {
    viewModelScope.launch {
      repository.saveAddress(
        UserAddressEntity(
          fullName = fullName,
          phone = phone,
          streetAddress = street,
          city = city,
          state = state,
          zipCode = zip,
          isDefault = addresses.value.isEmpty()
        )
      )
    }
  }

  // Admin Functions
  fun saveProduct(product: ProductEntity) {
    viewModelScope.launch {
      repository.saveProduct(product)
    }
  }

  fun deleteProduct(product: ProductEntity) {
    viewModelScope.launch {
      repository.deleteProduct(product)
    }
  }

  fun saveBanner(banner: AdBannerEntity) {
    viewModelScope.launch {
      repository.saveBanner(banner)
    }
  }

  fun deleteBanner(banner: AdBannerEntity) {
    viewModelScope.launch {
      repository.deleteBanner(banner)
    }
  }

  fun updateOrderStatus(order: OrderEntity, nextStatus: String, nextStep: Int) {
    viewModelScope.launch {
      repository.updateOrderStatus(order, nextStatus, nextStep)
    }
  }

  fun createCoupon(code: String, percent: Int, minAmount: Double, desc: String) {
    viewModelScope.launch {
      repository.createCoupon(
        CouponEntity(
          code = code.trim().uppercase(),
          discountPercent = percent,
          minOrderAmount = minAmount,
          description = desc
        )
      )
    }
  }
}
