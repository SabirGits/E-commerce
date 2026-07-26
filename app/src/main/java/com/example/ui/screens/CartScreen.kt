package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.entity.CartItemEntity
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.TrendHubViewModel

@Composable
fun CartScreen(
  viewModel: TrendHubViewModel,
  onCheckoutClick: () -> Unit,
  onExploreClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
  val appliedCoupon by viewModel.appliedCoupon.collectAsStateWithLifecycle()
  val couponMessage by viewModel.couponMessage.collectAsStateWithLifecycle()

  var couponInput by remember { mutableStateOf("") }

  val subtotal = cartItems.sumOf { it.productPrice * it.quantity }
  val discount = if (appliedCoupon != null) subtotal * (appliedCoupon!!.discountPercent / 100.0) else 0.0
  val tax = (subtotal - discount) * 0.08
  val shipping = if (subtotal == 0.0 || subtotal > 100.0 || appliedCoupon?.code == "FREEFLY") 0.0 else 15.0
  val finalTotal = (subtotal - discount + tax + shipping).coerceAtLeast(0.0)

  Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("🛒 Shopping Cart (${cartItems.size})", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
      if (cartItems.isNotEmpty()) {
        TextButton(onClick = { viewModel.clearCart() }) {
          Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = HotPink, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Clear", color = HotPink, fontSize = 12.sp)
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    if (cartItems.isEmpty()) {
      Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(Icons.Default.RemoveShoppingCart, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(72.dp))
          Spacer(modifier = Modifier.height(16.dp))
          Text("Your cart is empty", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
          Text("Discover latest 3D tech gadgets and gear up!", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
          Spacer(modifier = Modifier.height(20.dp))
          Button(
            onClick = onExploreClick,
            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Start Shopping Now", fontWeight = FontWeight.Bold)
          }
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
      ) {
        items(cartItems) { item ->
          CartItemRow(
            item = item,
            onIncrease = { viewModel.updateCartQuantity(item.id, item.quantity + 1) },
            onDecrease = { viewModel.updateCartQuantity(item.id, item.quantity - 1) },
            onRemove = { viewModel.removeFromCart(item.id) }
          )
        }
      }

      // Coupon Box
      GlassCard(cornerRadius = 16.dp, borderColor = CyberCyan.copy(alpha = 0.5f), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text("🎁 PROMO CODES & VOUCHERS", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
          Spacer(modifier = Modifier.height(8.dp))
          Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
              value = couponInput,
              onValueChange = { couponInput = it },
              placeholder = { Text("Try code 'TREND20' or 'FREEFLY'", fontSize = 12.sp) },
              singleLine = true,
              colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.weight(1f).height(48.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
              onClick = {
                if (couponInput.isNotBlank()) {
                  viewModel.applyCouponCode(couponInput)
                  couponInput = ""
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.height(48.dp)
            ) {
              Text("Apply", color = Color.Black, fontWeight = FontWeight.Bold)
            }
          }
          if (couponMessage != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(couponMessage!!, color = if (couponMessage!!.startsWith("✅")) NeonGreen else HotPink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Price Summary Card
      Surface(
        color = DarkSurface,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderDark),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Subtotal", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
            Text("$${String.format("%.2f", subtotal)}", color = Color.White, fontWeight = FontWeight.Bold)
          }
          if (discount > 0) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Coupon Discount (${appliedCoupon?.code})", color = NeonGreen, fontSize = 13.sp)
              Text("-$${String.format("%.2f", discount)}", color = NeonGreen, fontWeight = FontWeight.ExtraBold)
            }
          }
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Estimated Tax (8%)", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
            Text("$${String.format("%.2f", tax)}", color = Color.White, fontWeight = FontWeight.Bold)
          }
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Express Shipping", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
            Text(if (shipping == 0.0) "FREE" else "$${String.format("%.2f", shipping)}", color = if (shipping == 0.0) CyberCyan else Color.White, fontWeight = FontWeight.Bold)
          }

          HorizontalDivider(color = GlassBorderDark, modifier = Modifier.padding(vertical = 4.dp))

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Total Amount", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
            Text("$${String.format("%.2f", finalTotal)}", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
          }

          Spacer(modifier = Modifier.height(6.dp))

          Button(
            onClick = onCheckoutClick,
            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().height(52.dp)
          ) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Proceed to Secure Checkout", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
          }
        }
      }
    }
  }
}

@Composable
fun CartItemRow(
  item: CartItemEntity,
  onIncrease: () -> Unit,
  onDecrease: () -> Unit,
  onRemove: () -> Unit
) {
  GlassCard(cornerRadius = 16.dp, modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier.size(75.dp).clip(RoundedCornerShape(10.dp)).background(DarkBackground)
      ) {
        AsyncImage(
          model = item.productImageUrl,
          contentDescription = item.productName,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(item.productBrand.uppercase(), color = CyberCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(item.productName, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text("Config: ${item.selectedColor} • ${item.selectedStorage}", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text("$${item.productPrice * item.quantity}", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
      }

      // Quantity Stepper
      Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.SpaceBetween) {
        IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
          Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Gray, modifier = Modifier.size(16.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
          color = DarkSurfaceVariant,
          shape = RoundedCornerShape(8.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderDark)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onDecrease, modifier = Modifier.size(28.dp)) {
              Text("-", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Text("${item.quantity}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 6.dp))
            IconButton(onClick = onIncrease, modifier = Modifier.size(28.dp)) {
              Text("+", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
          }
        }
      }
    }
  }
}
