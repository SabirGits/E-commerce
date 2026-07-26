package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.entity.ProductEntity
import com.example.ui.theme.*

@Composable
fun ProductCard(
  product: ProductEntity,
  isFavorite: Boolean,
  onProductClick: (ProductEntity) -> Unit,
  onFavoriteClick: (ProductEntity) -> Unit,
  onAddToCartClick: (ProductEntity) -> Unit,
  modifier: Modifier = Modifier
) {
  var isHovered by remember { mutableStateOf(false) }
  val scale by animateFloatAsState(
    targetValue = if (isHovered) 1.03f else 1f,
    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
    label = "card_scale"
  )

  GlassCard(
    cornerRadius = 18.dp,
    borderColor = if (product.isTrending) CyberCyan.copy(alpha = 0.5f) else GlassBorderDark,
    onClick = {
      isHovered = true
      onProductClick(product)
    },
    modifier = modifier
      .width(170.dp)
      .scale(scale)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)
    ) {
      // Image Container with Badge & Wishlist Heart
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(130.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(DarkBackground.copy(alpha = 0.6f))
      ) {
        AsyncImage(
          model = product.imageUrl,
          contentDescription = product.name,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )

        // Top Badges
        Column(
          modifier = Modifier
            .align(Alignment.TopStart)
            .padding(6.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          if (product.discountPercentage > 0) {
            Surface(
              color = HotPink,
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(
                text = "-${product.discountPercentage}%",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
          if (product.isFlashSale) {
            Surface(
              color = AccentGold,
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(
                text = "⚡ FLASH",
                color = Color.Black,
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
              )
            }
          }
        }

        // Wishlist Button
        IconButton(
          onClick = { onFavoriteClick(product) },
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(4.dp)
            .size(28.dp)
            .background(DarkSurface.copy(alpha = 0.7f), CircleShape)
        ) {
          Icon(
            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Favorite",
            tint = if (isFavorite) HotPink else Color.White.copy(alpha = 0.8f),
            modifier = Modifier.size(16.dp)
          )
        }

        // 3D Model Badge
        Surface(
          color = CyberCyan.copy(alpha = 0.8f),
          shape = RoundedCornerShape(topEnd = 8.dp),
          modifier = Modifier.align(Alignment.BottomStart)
        ) {
          Text(
            text = "3D VIEW",
            color = Color.Black,
            fontSize = 8.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Brand & Category
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = product.brand.uppercase(),
          color = CyberCyan,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.weight(1f)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = AccentGold,
            modifier = Modifier.size(12.dp)
          )
          Text(
            text = "${product.rating}",
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(3.dp))

      // Product Title
      Text(
        text = product.name,
        color = Color.White,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.height(36.dp)
      )

      Spacer(modifier = Modifier.height(6.dp))

      // Price & Add to Cart
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "$${product.price}",
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold
          )
          if (product.originalPrice > product.price) {
            Text(
              text = "$${product.originalPrice}",
              color = Color.White.copy(alpha = 0.45f),
              fontSize = 11.sp,
              textDecoration = TextDecoration.LineThrough
            )
          }
        }

        IconButton(
          onClick = { onAddToCartClick(product) },
          modifier = Modifier
            .size(34.dp)
            .background(
              Brush.linearGradient(listOf(NeonPurple, CyberCyan)),
              CircleShape
            )
        ) {
          Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = "Add to Cart",
            tint = Color.White,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}
