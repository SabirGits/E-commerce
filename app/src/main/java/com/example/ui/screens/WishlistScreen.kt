package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.ProductEntity
import com.example.ui.components.ProductCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.TrendHubViewModel

@Composable
fun WishlistScreen(
  viewModel: TrendHubViewModel,
  onProductClick: (ProductEntity) -> Unit,
  onExploreClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val wishlistItems by viewModel.wishlistItems.collectAsStateWithLifecycle()
  val allProducts by viewModel.allProducts.collectAsStateWithLifecycle()

  val favoriteProducts = remember(wishlistItems, allProducts) {
    allProducts.filter { prod -> wishlistItems.any { it.productId == prod.id } }
  }

  Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
    Text("💖 Saved Wishlist (${favoriteProducts.size})", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
    Spacer(modifier = Modifier.height(12.dp))

    if (favoriteProducts.isEmpty()) {
      Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = HotPink, modifier = Modifier.size(72.dp))
          Spacer(modifier = Modifier.height(16.dp))
          Text("Your wishlist is empty", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
          Text("Tap the heart icon on any product to save for later", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
          Spacer(modifier = Modifier.height(20.dp))
          Button(onClick = onExploreClick, colors = ButtonDefaults.buttonColors(containerColor = HotPink), shape = RoundedCornerShape(12.dp)) {
            Text("Discover Products", fontWeight = FontWeight.Bold)
          }
        }
      }
    } else {
      LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        contentPadding = PaddingValues(bottom = 100.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(favoriteProducts) { product ->
          ProductCard(
            product = product,
            isFavorite = true,
            onProductClick = onProductClick,
            onFavoriteClick = { viewModel.toggleWishlist(it) },
            onAddToCartClick = { viewModel.addToCart(it) },
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    }
  }
}
