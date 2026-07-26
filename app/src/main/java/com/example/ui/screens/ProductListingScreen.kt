package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
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
import com.example.ui.components.GlassCard
import com.example.ui.components.ProductCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.TrendHubViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListingScreen(
  viewModel: TrendHubViewModel,
  onProductClick: (ProductEntity) -> Unit,
  modifier: Modifier = Modifier
) {
  val products by viewModel.filteredProducts.collectAsStateWithLifecycle()
  val categories by viewModel.categories.collectAsStateWithLifecycle()
  val wishlistItems by viewModel.wishlistItems.collectAsStateWithLifecycle()
  val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val sortBy by viewModel.sortBy.collectAsStateWithLifecycle()
  val maxPrice by viewModel.maxPrice.collectAsStateWithLifecycle()
  val minRating by viewModel.minRating.collectAsStateWithLifecycle()

  var isGridView by remember { mutableStateOf(true) }
  var showFilterSheet by remember { mutableStateOf(false) }

  Column(modifier = modifier.fillMaxSize().padding(horizontal = 12.dp)) {
    Spacer(modifier = Modifier.height(8.dp))

    // Search Bar & View Toggle
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { viewModel.setSearchQuery(it) },
        placeholder = { Text("Search tech gadgets, drones, RTX rigs...", fontSize = 13.sp) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CyberCyan) },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { viewModel.setSearchQuery("") }) {
              Icon(Icons.Default.Clear, contentDescription = null, tint = Color.White)
            }
          }
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = Color.White,
          unfocusedTextColor = Color.White,
          focusedBorderColor = CyberCyan,
          unfocusedBorderColor = GlassBorderDark
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.weight(1f).height(52.dp)
      )

      // Filter Button
      IconButton(
        onClick = { showFilterSheet = true },
        modifier = Modifier
          .size(52.dp)
          .background(if (selectedCategory != null || maxPrice < 4000f) NeonPurple else DarkSurfaceVariant, RoundedCornerShape(14.dp))
      ) {
        Icon(Icons.Default.Tune, contentDescription = "Filter", tint = Color.White)
      }

      // Grid/List Toggle
      IconButton(
        onClick = { isGridView = !isGridView },
        modifier = Modifier
          .size(52.dp)
          .background(DarkSurfaceVariant, RoundedCornerShape(14.dp))
      ) {
        Icon(if (isGridView) Icons.Default.ViewList else Icons.Default.GridView, contentDescription = "Toggle View", tint = CyberCyan)
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Category Pill Bar
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      item {
        val isAll = selectedCategory == null
        FilterChip(
          selected = isAll,
          onClick = { viewModel.setCategory(null) },
          label = { Text("All Products", fontWeight = FontWeight.Bold) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = CyberCyan,
            selectedLabelColor = Color.Black,
            containerColor = DarkSurfaceVariant,
            labelColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp)
        )
      }

      items(categories) { cat ->
        val isSelected = selectedCategory == cat.name
        FilterChip(
          selected = isSelected,
          onClick = { viewModel.setCategory(cat.name) },
          label = { Text(cat.name, fontWeight = FontWeight.Bold) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = CyberCyan,
            selectedLabelColor = Color.Black,
            containerColor = DarkSurfaceVariant,
            labelColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Sorting Row
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Showing ${products.size} Items",
        color = Color.White.copy(alpha = 0.7f),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
      )

      Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Sort: ", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
        val sorts = listOf(
          "POPULARITY" to "Popular",
          "PRICE_LOW_HIGH" to "Price ↑",
          "PRICE_HIGH_LOW" to "Price ↓",
          "NEWEST" to "Newest"
        )
        sorts.forEach { (key, label) ->
          val isSel = sortBy == key
          Text(
            text = label,
            color = if (isSel) HotPink else Color.White,
            fontSize = 12.sp,
            fontWeight = if (isSel) FontWeight.ExtraBold else FontWeight.Normal,
            modifier = Modifier
              .clickable { viewModel.setSortBy(key) }
              .padding(horizontal = 6.dp, vertical = 4.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Product Grid or List
    if (products.isEmpty()) {
      Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(Icons.Default.SearchOff, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(64.dp))
          Spacer(modifier = Modifier.height(12.dp))
          Text("No tech products found", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
          Text("Try clearing search queries or price filters", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
          Spacer(modifier = Modifier.height(16.dp))
          Button(onClick = {
            viewModel.setSearchQuery("")
            viewModel.setCategory(null)
            viewModel.setMaxPrice(4000f)
            viewModel.setMinRating(0f)
          }) {
            Text("Reset Filters")
          }
        }
      }
    } else if (isGridView) {
      LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        contentPadding = PaddingValues(bottom = 100.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(products) { product ->
          ProductCard(
            product = product,
            isFavorite = wishlistItems.any { it.productId == product.id },
            onProductClick = onProductClick,
            onFavoriteClick = { viewModel.toggleWishlist(it) },
            onAddToCartClick = { viewModel.addToCart(it) },
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    } else {
      LazyColumn(
        contentPadding = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(products) { product ->
          ProductListItem(
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

  // Filter Modal Sheet
  if (showFilterSheet) {
    ModalBottomSheet(
      onDismissRequest = { showFilterSheet = false },
      containerColor = DarkSurface
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.Start
      ) {
        Text("⚙️ FILTER PRODUCTS", color = CyberCyan, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Max Price: $${maxPrice.toInt()}", color = Color.White, fontWeight = FontWeight.Bold)
        Slider(
          value = maxPrice,
          onValueChange = { viewModel.setMaxPrice(it) },
          valueRange = 50f..4000f,
          colors = SliderDefaults.colors(thumbColor = HotPink, activeTrackColor = HotPink)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Minimum Rating: ${if (minRating == 0f) "Any" else "${minRating.toInt()}+ Stars"}", color = Color.White, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
          listOf(0f to "Any", 4f to "4+ ⭐", 4.5f to "4.5+ ⭐", 4.8f to "4.8+ ⭐").forEach { (valRat, label) ->
            val sel = minRating == valRat
            Surface(
              color = if (sel) NeonPurple else DarkSurfaceVariant,
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.clickable { viewModel.setMinRating(valRat) }
            ) {
              Text(label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
            }
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
          OutlinedButton(
            onClick = {
              viewModel.setCategory(null)
              viewModel.setMaxPrice(4000f)
              viewModel.setMinRating(0f)
              showFilterSheet = false
            },
            modifier = Modifier.weight(1f)
          ) {
            Text("Reset", color = Color.White)
          }
          Button(
            onClick = { showFilterSheet = false },
            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
            modifier = Modifier.weight(1f)
          ) {
            Text("Apply Filters", color = Color.Black, fontWeight = FontWeight.Bold)
          }
        }
        Spacer(modifier = Modifier.height(32.dp))
      }
    }
  }
}

@Composable
fun ProductListItem(
  product: ProductEntity,
  isFavorite: Boolean,
  onProductClick: (ProductEntity) -> Unit,
  onFavoriteClick: (ProductEntity) -> Unit,
  onAddToCartClick: (ProductEntity) -> Unit
) {
  GlassCard(
    cornerRadius = 16.dp,
    onClick = { onProductClick(product) },
    modifier = Modifier.fillMaxWidth().height(120.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxSize().padding(10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(100.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(DarkBackground)
      ) {
        AsyncImage(
          model = product.imageUrl,
          contentDescription = product.name,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.SpaceBetween) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text(product.brand.uppercase(), color = CyberCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Star, contentDescription = null, tint = AccentGold, modifier = Modifier.size(12.dp))
            Text("${product.rating}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }
        }

        Text(product.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(product.description, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("$${product.price}", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            if (product.originalPrice > product.price) {
              Text("$${product.originalPrice}", color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp, textDecoration = TextDecoration.LineThrough)
            }
          }

          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            IconButton(
              onClick = { onFavoriteClick(product) },
              modifier = Modifier.size(32.dp).background(DarkSurfaceVariant, CircleShape)
            ) {
              Icon(if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = null, tint = if (isFavorite) HotPink else Color.White, modifier = Modifier.size(16.dp))
            }

            IconButton(
              onClick = { onAddToCartClick(product) },
              modifier = Modifier.size(32.dp).background(NeonPurple, CircleShape)
            ) {
              Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
          }
        }
      }
    }
  }
}
