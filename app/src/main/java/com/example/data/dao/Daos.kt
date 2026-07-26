package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
  @Query("SELECT * FROM products ORDER BY id ASC")
  fun getAllProducts(): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products WHERE id = :id")
  fun getProductById(id: Int): Flow<ProductEntity?>

  @Query("SELECT * FROM products WHERE category = :category ORDER BY id ASC")
  fun getProductsByCategory(category: String): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%'")
  fun searchProducts(query: String): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products WHERE isTrending = 1")
  fun getTrendingProducts(): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products WHERE isBestSeller = 1")
  fun getBestSellers(): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products WHERE isFlashSale = 1")
  fun getFlashSaleProducts(): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products WHERE isDealOfDay = 1")
  fun getDealsOfDay(): Flow<List<ProductEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProduct(product: ProductEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(products: List<ProductEntity>)

  @Update
  suspend fun updateProduct(product: ProductEntity)

  @Delete
  suspend fun deleteProduct(product: ProductEntity)

  @Query("SELECT COUNT(*) FROM products")
  suspend fun getCount(): Int
}

@Dao
interface CategoryDao {
  @Query("SELECT * FROM categories")
  fun getAllCategories(): Flow<List<CategoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(categories: List<CategoryEntity>)

  @Query("SELECT COUNT(*) FROM categories")
  suspend fun getCount(): Int
}

@Dao
interface CartDao {
  @Query("SELECT * FROM cart_items ORDER BY id DESC")
  fun getCartItems(): Flow<List<CartItemEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCartItem(item: CartItemEntity)

  @Update
  suspend fun updateCartItem(item: CartItemEntity)

  @Query("DELETE FROM cart_items WHERE id = :id")
  suspend fun deleteCartItem(id: Int)

  @Query("DELETE FROM cart_items")
  suspend fun clearCart()
}

@Dao
interface WishlistDao {
  @Query("SELECT * FROM wishlist_items ORDER BY addedAt DESC")
  fun getWishlistItems(): Flow<List<WishlistItemEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertWishlistItem(item: WishlistItemEntity)

  @Query("DELETE FROM wishlist_items WHERE productId = :productId")
  suspend fun deleteByProductId(productId: Int)

  @Query("SELECT EXISTS(SELECT 1 FROM wishlist_items WHERE productId = :productId)")
  fun isFavorite(productId: Int): Flow<Boolean>
}

@Dao
interface OrderDao {
  @Query("SELECT * FROM orders ORDER BY createdAt DESC")
  fun getAllOrders(): Flow<List<OrderEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrder(order: OrderEntity)

  @Update
  suspend fun updateOrder(order: OrderEntity)
}

@Dao
interface CouponDao {
  @Query("SELECT * FROM coupons WHERE isActive = 1")
  fun getActiveCoupons(): Flow<List<CouponEntity>>

  @Query("SELECT * FROM coupons WHERE code = :code AND isActive = 1")
  suspend fun getCouponByCode(code: String): CouponEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCoupon(coupon: CouponEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(coupons: List<CouponEntity>)

  @Update
  suspend fun updateCoupon(coupon: CouponEntity)

  @Delete
  suspend fun deleteCoupon(coupon: CouponEntity)

  @Query("SELECT COUNT(*) FROM coupons")
  suspend fun getCount(): Int
}

@Dao
interface AdBannerDao {
  @Query("SELECT * FROM ad_banners WHERE isActive = 1 ORDER BY id ASC")
  fun getActiveBanners(): Flow<List<AdBannerEntity>>

  @Query("SELECT * FROM ad_banners ORDER BY id ASC")
  fun getAllBanners(): Flow<List<AdBannerEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBanner(banner: AdBannerEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(banners: List<AdBannerEntity>)

  @Update
  suspend fun updateBanner(banner: AdBannerEntity)

  @Delete
  suspend fun deleteBanner(banner: AdBannerEntity)

  @Query("SELECT COUNT(*) FROM ad_banners")
  suspend fun getCount(): Int
}

@Dao
interface ReviewDao {
  @Query("SELECT * FROM reviews WHERE productId = :productId ORDER BY id DESC")
  fun getReviewsForProduct(productId: Int): Flow<List<ReviewEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertReview(review: ReviewEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(reviews: List<ReviewEntity>)

  @Query("SELECT COUNT(*) FROM reviews")
  suspend fun getCount(): Int
}

@Dao
interface AddressDao {
  @Query("SELECT * FROM addresses ORDER BY isDefault DESC, id ASC")
  fun getAllAddresses(): Flow<List<UserAddressEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAddress(address: UserAddressEntity)

  @Update
  suspend fun updateAddress(address: UserAddressEntity)

  @Delete
  suspend fun deleteAddress(address: UserAddressEntity)
}
