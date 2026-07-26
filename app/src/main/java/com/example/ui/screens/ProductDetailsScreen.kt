package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.entity.ProductEntity
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.TrendHubViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
  productId: Int,
  viewModel: TrendHubViewModel,
  onBackClick: () -> Unit,
  onCartClick: () -> Unit,
  onProductClick: (ProductEntity) -> Unit,
  modifier: Modifier = Modifier
) {
  val allProducts by viewModel.allProducts.collectAsStateWithLifecycle()
  val wishlistItems by viewModel.wishlistItems.collectAsStateWithLifecycle()
  val product = remember(allProducts, productId) { allProducts.find { it.id == productId } }
  val reviews by viewModel.getReviewsForProduct(productId).collectAsStateWithLifecycle(emptyList())

  if (product == null) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("Product not found", color = Color.White)
    }
    return
  }

  val isFavorite = wishlistItems.any { it.productId == product.id }
  var selectedTab by remember { mutableStateOf(0) } // 0 = 3D Viewer, 1 = HD Gallery, 2 = Specs, 3 = Reviews

  val colorsList = remember(product) { product.availableColors.split(",").map { it.trim() } }
  val storageList = remember(product) { product.availableStorage.split(",").map { it.trim() } }
  var selectedColor by remember { mutableStateOf(colorsList.firstOrNull() ?: "Titanium Gray") }
  var selectedStorage by remember { mutableStateOf(storageList.firstOrNull() ?: "256GB") }

  var showReviewModal by remember { mutableStateOf(false) }
  var addedToast by remember { mutableStateOf(false) }

  val colorHex = when (selectedColor.lowercase()) {
    "cyber cyan" -> CyberCyan
    "neon purple" -> NeonPurple
    "hot pink" -> HotPink
    "alpine white" -> Color.White
    "obsidian black" -> Color(0xFF1E293B)
    else -> Color(0xFF94A3B8)
  }

  Box(modifier = modifier.fillMaxSize()) {
    LazyColumn(
      contentPadding = PaddingValues(bottom = 120.dp),
      modifier = Modifier.fillMaxSize()
    ) {
      // Top Navigation Bar
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(40.dp).background(DarkSurfaceVariant, CircleShape)
          ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
          }

          Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            IconButton(
              onClick = { viewModel.toggleWishlist(product) },
              modifier = Modifier.size(40.dp).background(DarkSurfaceVariant, CircleShape)
            ) {
              Icon(
                if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = null,
                tint = if (isFavorite) HotPink else Color.White
              )
            }
            IconButton(
              onClick = onCartClick,
              modifier = Modifier.size(40.dp).background(DarkSurfaceVariant, CircleShape)
            ) {
              Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = CyberCyan)
            }
          }
        }
      }

      // View Mode Tabs (3D vs Gallery)
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .background(DarkSurfaceVariant, RoundedCornerShape(14.dp))
            .padding(4.dp),
          horizontalArrangement = Arrangement.SpaceEvenly
        ) {
          val tabs = listOf("🧊 3D Viewer", "📸 HD Gallery", "⚙️ Specs", "⭐ Reviews (${reviews.size})")
          tabs.forEachIndexed { index, title ->
            val sel = selectedTab == index
            val bg by animateColorAsState(if (sel) NeonPurple else Color.Transparent, label = "tab_bg")
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(bg)
                .clickable { selectedTab = index }
                .padding(vertical = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = title,
                color = if (sel) Color.White else Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
                fontWeight = if (sel) FontWeight.ExtraBold else FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
        }
      }

      // Content based on tab
      item {
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)) {
          when (selectedTab) {
            0 -> {
              Interactive3DProductViewer(
                modelType = product.modelType,
                productName = product.name,
                selectedColorHex = colorHex
              )
            }
            1 -> {
              GlassCard(cornerRadius = 20.dp, modifier = Modifier.fillMaxWidth().height(280.dp)) {
                AsyncImage(
                  model = product.imageUrl,
                  contentDescription = product.name,
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.fillMaxSize()
                )
              }
            }
            2 -> {
              GlassCard(cornerRadius = 16.dp, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  Text("TECHNICAL SPECIFICATIONS", color = CyberCyan, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                  product.specs.split("\n").forEach { specLine ->
                    Row(verticalAlignment = Alignment.Top) {
                      Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp).padding(top = 2.dp))
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(specLine, color = Color.White, fontSize = 13.sp, lineHeight = 18.sp)
                    }
                  }
                }
              }
            }
            3 -> {
              Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${product.rating}", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                      Row {
                        repeat(5) { Icon(Icons.Default.Star, contentDescription = null, tint = AccentGold, modifier = Modifier.size(14.dp)) }
                      }
                      Text("${product.reviewsCount} verified buyers", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                    }
                  }
                  Button(
                    onClick = { showReviewModal = true },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                  ) {
                    Text("Write Review", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                  }
                }

                if (reviews.isEmpty()) {
                  Text("No reviews yet. Be the first to review!", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
                } else {
                  reviews.forEach { rev ->
                    GlassCard(cornerRadius = 14.dp, modifier = Modifier.fillMaxWidth()) {
                      Column(modifier = Modifier.padding(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                          Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(modifier = Modifier.size(32.dp), shape = CircleShape, color = NeonPurple) {
                              Box(contentAlignment = Alignment.Center) { Text(rev.userName.take(1), color = Color.White, fontWeight = FontWeight.Bold) }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                              Text(rev.userName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                              if (rev.isVerifiedBuyer) {
                                Text("✔ Verified Buyer • ${rev.date}", color = NeonGreen, fontSize = 10.sp)
                              }
                            }
                          }
                          Row { repeat(rev.rating) { Icon(Icons.Default.Star, contentDescription = null, tint = AccentGold, modifier = Modifier.size(12.dp)) } }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(rev.comment, color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }

      // Title & Price Section
      item {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(color = CyberCyan.copy(alpha = 0.2f), shape = RoundedCornerShape(6.dp)) {
              Text(product.brand.uppercase(), color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Inventory2, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("In Stock: ${product.stock} units left", color = NeonGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(product.name, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)

          Spacer(modifier = Modifier.height(8.dp))

          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("$${product.price}", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Black)
            if (product.originalPrice > product.price) {
              Text("$${product.originalPrice}", color = Color.White.copy(alpha = 0.4f), fontSize = 16.sp, textDecoration = TextDecoration.LineThrough)
              Surface(color = HotPink, shape = RoundedCornerShape(6.dp)) {
                Text("SAVE ${product.discountPercentage}%", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))
          Text("ABOUT PRODUCT", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          Text(product.description, color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp, lineHeight = 20.sp)
        }
      }

      // Color Selector
      if (colorsList.isNotEmpty() && colorsList[0].isNotBlank()) {
        item {
          Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text("CHOOSE COLOR: $selectedColor", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              items(colorsList) { col ->
                val sel = selectedColor == col
                Surface(
                  color = if (sel) NeonPurple else DarkSurfaceVariant,
                  shape = RoundedCornerShape(12.dp),
                  border = BorderStroke(1.dp, if (sel) CyberCyan else GlassBorderDark),
                  modifier = Modifier.clickable { selectedColor = col }
                ) {
                  Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    val cHex = when (col.lowercase()) {
                      "cyber cyan" -> CyberCyan
                      "neon purple" -> NeonPurple
                      "hot pink" -> HotPink
                      "alpine white" -> Color.White
                      else -> Color(0xFF475569)
                    }
                    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(cHex))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(col, color = Color.White, fontSize = 12.sp, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal)
                  }
                }
              }
            }
          }
        }
      }

      // Storage Selector
      if (storageList.isNotEmpty() && storageList[0].isNotBlank()) {
        item {
          Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text("SELECT CONFIGURATION: $selectedStorage", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              items(storageList) { stor ->
                val sel = selectedStorage == stor
                Surface(
                  color = if (sel) CyberCyan else DarkSurfaceVariant,
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier.clickable { selectedStorage = stor }
                ) {
                  Text(
                    stor,
                    color = if (sel) Color.Black else Color.White,
                    fontSize = 13.sp,
                    fontWeight = if (sel) FontWeight.ExtraBold else FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                  )
                }
              }
            }
          }
        }
      }

      // Related Products
      item {
        val related = allProducts.filter { it.category == product.category && it.id != product.id }
        if (related.isNotEmpty()) {
          Column(modifier = Modifier.padding(top = 16.dp)) {
            SectionHeader(title = "Similar Tech In ${product.category}")
            LazyRow(
              horizontalArrangement = Arrangement.spacedBy(12.dp),
              contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
              items(related) { rel ->
                ProductCard(
                  product = rel,
                  isFavorite = wishlistItems.any { it.productId == rel.id },
                  onProductClick = onProductClick,
                  onFavoriteClick = { viewModel.toggleWishlist(it) },
                  onAddToCartClick = { viewModel.addToCart(it) }
                )
              }
            }
          }
        }
      }
    }

    // Bottom Sticky Action Bar
    Surface(
      color = DarkSurface.copy(alpha = 0.95f),
      tonalElevation = 16.dp,
      modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(0.4f)) {
          Text("Total Price", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
          Text("$${product.price}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
        }

        OutlinedButton(
          onClick = {
            viewModel.addToCart(product, selectedColor, selectedStorage)
            addedToast = true
          },
          border = BorderStroke(1.dp, CyberCyan),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.weight(0.6f).height(48.dp)
        ) {
          Icon(Icons.Default.AddShoppingCart, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Add to Cart", color = CyberCyan, fontWeight = FontWeight.Bold)
        }

        Button(
          onClick = {
            viewModel.addToCart(product, selectedColor, selectedStorage)
            onCartClick()
          },
          colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.weight(0.6f).height(48.dp)
        ) {
          Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Buy Now", color = Color.White, fontWeight = FontWeight.ExtraBold)
        }
      }
    }

    if (addedToast) {
      LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2000)
        addedToast = false
      }
      Surface(
        color = NeonGreen,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.align(Alignment.TopCenter).padding(top = 20.dp)
      ) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Added ${product.name} to cart!", color = Color.Black, fontWeight = FontWeight.Bold)
        }
      }
    }

    // Write Review Dialog
    if (showReviewModal) {
      var revName by remember { mutableStateOf("Verified Tech Lover") }
      var revComment by remember { mutableStateOf("") }
      var revRating by remember { mutableStateOf(5) }

      AlertDialog(
        onDismissRequest = { showReviewModal = false },
        containerColor = DarkSurface,
        title = { Text("Write a Product Review", color = CyberCyan, fontWeight = FontWeight.Bold) },
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Your Name", color = Color.White, fontSize = 12.sp)
            OutlinedTextField(
              value = revName,
              onValueChange = { revName = it },
              singleLine = true,
              colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )
            Text("Rating Stars: $revRating ⭐", color = Color.White, fontSize = 12.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              (1..5).forEach { star ->
                IconButton(onClick = { revRating = star }) {
                  Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = if (star <= revRating) AccentGold else Color.Gray
                  )
                }
              }
            }
            Text("Your Experience", color = Color.White, fontSize = 12.sp)
            OutlinedTextField(
              value = revComment,
              onValueChange = { revComment = it },
              placeholder = { Text("How was the performance, battery, or build?") },
              modifier = Modifier.height(100.dp),
              colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )
          }
        },
        confirmButton = {
          Button(
            onClick = {
              if (revComment.isNotBlank()) {
                viewModel.addReview(product.id, revName, revRating, revComment)
                showReviewModal = false
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
          ) {
            Text("Submit Review", color = Color.Black, fontWeight = FontWeight.Bold)
          }
        },
        dismissButton = {
          TextButton(onClick = { showReviewModal = false }) { Text("Cancel") }
        }
      )
    }
  }
}
