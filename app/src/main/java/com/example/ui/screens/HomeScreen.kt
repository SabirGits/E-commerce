package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.entity.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.TrendHubViewModel

@Composable
fun HomeScreen(
  viewModel: TrendHubViewModel,
  onProductClick: (ProductEntity) -> Unit,
  onCategoryClick: (String) -> Unit,
  onExploreClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val products by viewModel.allProducts.collectAsStateWithLifecycle()
  val categories by viewModel.categories.collectAsStateWithLifecycle()
  val activeBanners by viewModel.activeBanners.collectAsStateWithLifecycle()
  val wishlistItems by viewModel.wishlistItems.collectAsStateWithLifecycle()
  val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
  val popupAd by viewModel.activePopupAd.collectAsStateWithLifecycle()

  var showNewsletterToast by remember { mutableStateOf(false) }
  var newsletterEmail by remember { mutableStateOf("") }

  val topBanner = activeBanners.find { it.adType == "TOP_BANNER" }
  val carouselBanners = activeBanners.filter { it.adType == "CAROUSEL" || it.adType == "OFFER" }
  val sidebarAd = activeBanners.find { it.adType == "SIDEBAR" }

  val flashSaleProducts = products.filter { it.isFlashSale || it.discountPercentage >= 15 }
  val bestSellers = products.filter { it.isBestSeller }
  val trendingProducts = products.filter { it.isTrending }
  val dealsOfDay = products.filter { it.isDealOfDay || it.originalPrice > it.price }

  Box(modifier = modifier.fillMaxSize()) {
    LazyVerticalGrid(
      columns = GridCells.Adaptive(minSize = 160.dp),
      contentPadding = PaddingValues(bottom = 100.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
      modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp)
    ) {
      // 1. Top Banner Ad
      if (topBanner != null) {
        item(span = { GridItemSpan(maxLineSpan) }) {
          TopBannerAdView(
            banner = topBanner,
            onDismiss = { /* Handled internally */ },
            onClick = { onCategoryClick(topBanner.targetCategory) },
            modifier = Modifier.padding(top = 8.dp)
          )
        }
      }

      // 2. Hero 3D Tech Banner
      item(span = { GridItemSpan(maxLineSpan) }) {
        Hero3DBanner(onExploreClick = onExploreClick)
      }

      // 3. Category Pills
      item(span = { GridItemSpan(maxLineSpan) }) {
        Column {
          SectionHeader(title = "Categories", subtitle = "Explore 3D models & tech")
          LazyRow(
            contentPadding = PaddingValues(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(categories) { cat ->
              val isSelected = selectedCategory == cat.name
              val bg by androidx.compose.animation.animateColorAsState(
                if (isSelected) NeonPurple else DarkSurfaceVariant,
                label = "cat_bg"
              )
              Surface(
                color = bg,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, if (isSelected) CyberCyan else GlassBorderDark),
                modifier = Modifier.clickable {
                  viewModel.setCategory(cat.name)
                  onCategoryClick(cat.name)
                }
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = getCategoryIcon(cat.iconName),
                    contentDescription = cat.name,
                    tint = if (isSelected) Color.White else CyberCyan,
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = cat.name,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                  )
                }
              }
            }
          }
        }
      }

      // 4. Flash Sale Carousel
      if (flashSaleProducts.isNotEmpty()) {
        item(span = { GridItemSpan(maxLineSpan) }) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 6.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text("⚡ FLASH SALE", color = HotPink, fontSize = 18.sp, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.width(8.dp))
                FlashSaleTimer()
              }
              TextButton(onClick = onExploreClick) {
                Text("View All", color = CyberCyan, fontSize = 12.sp)
              }
            }
            LazyRow(
              horizontalArrangement = Arrangement.spacedBy(12.dp),
              contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
              items(flashSaleProducts) { product ->
                ProductCard(
                  product = product,
                  isFavorite = wishlistItems.any { it.productId == product.id },
                  onProductClick = onProductClick,
                  onFavoriteClick = { viewModel.toggleWishlist(it) },
                  onAddToCartClick = { viewModel.addToCart(it) }
                )
              }
            }
          }
        }
      }

      // 5. Carousel Ad Banner
      if (carouselBanners.isNotEmpty()) {
        item(span = { GridItemSpan(maxLineSpan) }) {
          CarouselAdView(
            banners = carouselBanners,
            onBannerClick = { onCategoryClick(it.targetCategory) },
            modifier = Modifier.padding(vertical = 8.dp)
          )
        }
      }

      // 6. Today's Deals
      if (dealsOfDay.isNotEmpty()) {
        item(span = { GridItemSpan(maxLineSpan) }) {
          Column {
            SectionHeader(title = "Today's Top Deals", subtitle = "Handpicked savings just for you", actionText = "See All", onActionClick = onExploreClick)
            LazyRow(
              horizontalArrangement = Arrangement.spacedBy(12.dp),
              contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
              items(dealsOfDay) { product ->
                ProductCard(
                  product = product,
                  isFavorite = wishlistItems.any { it.productId == product.id },
                  onProductClick = onProductClick,
                  onFavoriteClick = { viewModel.toggleWishlist(it) },
                  onAddToCartClick = { viewModel.addToCart(it) }
                )
              }
            }
          }
        }
      }

      // 7. Sidebar / Special Offer Ad
      if (sidebarAd != null) {
        item(span = { GridItemSpan(maxLineSpan) }) {
          SidebarInlineAdView(
            banner = sidebarAd,
            onClick = { onCategoryClick(sidebarAd.targetCategory) },
            modifier = Modifier.padding(vertical = 6.dp)
          )
        }
      }

      // 8. Best Sellers
      item(span = { GridItemSpan(maxLineSpan) }) {
        SectionHeader(title = "Best Sellers", subtitle = "Trending across the community", actionText = "Explore", onActionClick = onExploreClick)
      }

      items(bestSellers.take(6)) { product ->
        ProductCard(
          product = product,
          isFavorite = wishlistItems.any { it.productId == product.id },
          onProductClick = onProductClick,
          onFavoriteClick = { viewModel.toggleWishlist(it) },
          onAddToCartClick = { viewModel.addToCart(it) },
          modifier = Modifier.fillMaxWidth()
        )
      }

      // 9. Trending Products Grid Header
      item(span = { GridItemSpan(maxLineSpan) }) {
        SectionHeader(title = "Trending Now", subtitle = "Next-gen gadgets in high demand")
      }

      items(trendingProducts) { product ->
        ProductCard(
          product = product,
          isFavorite = wishlistItems.any { it.productId == product.id },
          onProductClick = onProductClick,
          onFavoriteClick = { viewModel.toggleWishlist(it) },
          onAddToCartClick = { viewModel.addToCart(it) },
          modifier = Modifier.fillMaxWidth()
        )
      }

      // 10. Customer Reviews & Newsletter Footer
      item(span = { GridItemSpan(maxLineSpan) }) {
        Column(modifier = Modifier.padding(top = 16.dp)) {
          SectionHeader(title = "Community Reviews", subtitle = "What tech enthusiasts say about TRENDHUB")
          CommunityReviewsSection()

          Spacer(modifier = Modifier.height(20.dp))

          // Newsletter Box
          GlassCard(
            cornerRadius = 20.dp,
            borderColor = CyberCyan,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.padding(20.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(Icons.Default.MarkEmailRead, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(36.dp))
              Spacer(modifier = Modifier.height(8.dp))
              Text("Join the 3D Tech Revolution", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
              Text(
                "Subscribe to receive flash sale alerts and exclusive 20% discount vouchers!",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
              )
              Spacer(modifier = Modifier.height(14.dp))
              Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                  value = newsletterEmail,
                  onValueChange = { newsletterEmail = it },
                  placeholder = { Text("Enter your email...", fontSize = 12.sp) },
                  singleLine = true,
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = CyberCyan,
                    unfocusedBorderColor = GlassBorderDark
                  ),
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier.weight(1f).height(50.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                  onClick = {
                    if (newsletterEmail.contains("@")) {
                      showNewsletterToast = true
                      newsletterEmail = ""
                    }
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier.height(50.dp)
                ) {
                  Text("Join", fontWeight = FontWeight.Bold)
                }
              }
              if (showNewsletterToast) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("🎉 Subscribed! Use coupon code 'TREND20' at checkout!", color = NeonGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }

    // Popup Ad Modal
    if (popupAd != null) {
      PopupOfferDialog(
        banner = popupAd!!,
        onDismiss = { viewModel.dismissPopupAd() },
        onClaim = {
          viewModel.applyCouponCode("TREND20")
          viewModel.dismissPopupAd()
        }
      )
    }
  }
}

@Composable
fun Hero3DBanner(onExploreClick: () -> Unit) {
  GlassCard(
    cornerRadius = 24.dp,
    borderColor = NeonPurple,
    modifier = Modifier
      .fillMaxWidth()
      .height(210.dp)
      .padding(vertical = 4.dp)
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      // Futuristic Tech Hero Background Image generated earlier
      AsyncImage(
        model = com.example.R.drawable.tech_hero_banner_1785088443456,
        contentDescription = "3D Tech Showcase",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
      )

      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.horizontalGradient(
              colors = listOf(
                DarkBackground.copy(alpha = 0.95f),
                DarkBackground.copy(alpha = 0.7f),
                Color.Transparent
              )
            )
          )
      )

      Column(
        modifier = Modifier
          .align(Alignment.CenterStart)
          .padding(22.dp)
          .fillMaxWidth(0.68f),
        verticalArrangement = Arrangement.Center
      ) {
        Surface(
          color = HotPink.copy(alpha = 0.25f),
          shape = RoundedCornerShape(6.dp),
          border = BorderStroke(1.dp, HotPink)
        ) {
          Text(
            text = "✨ 3D ENGINE CONNECTED",
            color = HotPink,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "NEXT-GEN TECH\nIN TRUE 3D",
          color = Color.White,
          fontSize = 22.sp,
          fontWeight = FontWeight.Black,
          lineHeight = 24.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Spin, rotate, and customize realistic wireframes & shaders.",
          color = Color.White.copy(alpha = 0.8f),
          fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
          onClick = onExploreClick,
          colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
          contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.height(36.dp)
        ) {
          Text("Explore 3D Store", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
        }
      }
    }
  }
}

@Composable
fun CommunityReviewsSection() {
  val sampleReviews = listOf(
    Pair("Alex M.", "The 3D interactive viewer in TRENDHUB is incredible. Being able to spin the iPhone 16 Pro and see camera specs in real time helped me decide immediately! ⭐⭐⭐⭐⭐"),
    Pair("Sarah J.", "Super fast shipping! Applied coupon TREND20 and saved $50 on my new Sony noise canceling headphones. ⭐⭐⭐⭐⭐"),
    Pair("David K.", "Best tech e-commerce app on Android. Dark mode cyberpunk styling is gorgeous and checkout was super smooth. ⭐⭐⭐⭐⭐")
  )

  LazyRow(
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
  ) {
    items(sampleReviews) { (author, text) ->
      GlassCard(
        cornerRadius = 16.dp,
        modifier = Modifier.width(260.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              modifier = Modifier.size(28.dp),
              shape = CircleShape,
              color = NeonPurple
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(author.take(1), color = Color.White, fontWeight = FontWeight.Bold)
              }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(author, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(text, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, lineHeight = 16.sp)
        }
      }
    }
  }
}

fun getCategoryIcon(iconName: String): androidx.compose.ui.graphics.vector.ImageVector {
  return when (iconName) {
    "smartphone" -> Icons.Default.Smartphone
    "laptop" -> Icons.Default.Laptop
    "headphones" -> Icons.Default.Headphones
    "camera" -> Icons.Default.CameraAlt
    "sports_esports" -> Icons.Default.SportsEsports
    "watch" -> Icons.Default.Watch
    "tv" -> Icons.Default.Tv
    else -> Icons.Default.Cable
  }
}
