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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.OrderEntity
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.TrendHubViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OrderHistoryScreen(
  viewModel: TrendHubViewModel,
  onOrderClick: (OrderEntity) -> Unit,
  onExploreClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val orders by viewModel.orders.collectAsStateWithLifecycle()

  Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
    Text("📦 Your Orders (${orders.size})", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
    Spacer(modifier = Modifier.height(12.dp))

    if (orders.isEmpty()) {
      Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(Icons.Default.LocalShipping, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(72.dp))
          Spacer(modifier = Modifier.height(16.dp))
          Text("No previous orders found", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
          Text("When you place an order, track status and invoices here.", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
          Spacer(modifier = Modifier.height(20.dp))
          Button(onClick = onExploreClick, colors = ButtonDefaults.buttonColors(containerColor = NeonPurple), shape = RoundedCornerShape(12.dp)) {
            Text("Start Shopping", fontWeight = FontWeight.Bold)
          }
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 20.dp)
      ) {
        items(orders) { order ->
          GlassCard(
            cornerRadius = 18.dp,
            onClick = { onOrderClick(order) },
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Surface(color = NeonPurple.copy(alpha = 0.25f), shape = RoundedCornerShape(8.dp)) {
                    Text("#ORD-${order.id + 1000}", color = NeonPurple, fontWeight = FontWeight.Black, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                  }
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date(order.createdAt)), color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                }

                val statusColor = when (order.status) {
                  "Delivered" -> NeonGreen
                  "Shipped" -> CyberCyan
                  "Processing" -> AccentGold
                  else -> HotPink
                }
                Surface(color = statusColor.copy(alpha = 0.2f), shape = CircleShape) {
                  Text(order.status.uppercase(), color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              Text(order.itemsSummary, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)

              Spacer(modifier = Modifier.height(8.dp))

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                  Text("Total Paid", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                  Text("$${String.format("%.2f", order.finalAmount)}", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                }

                Button(
                  onClick = { onOrderClick(order) },
                  colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                  shape = RoundedCornerShape(10.dp)
                ) {
                  Text("Track Order ➔", color = CyberCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }
      }
    }
  }
}
