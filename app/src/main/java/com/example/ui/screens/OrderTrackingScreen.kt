package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.OrderEntity
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.TrendHubViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
  orderId: Int,
  viewModel: TrendHubViewModel,
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val orders by viewModel.orders.collectAsStateWithLifecycle()
  val order = remember(orders, orderId) { orders.find { it.id == orderId } }
  var showInvoiceModal by remember { mutableStateOf(false) }
  var copiedToast by remember { mutableStateOf(false) }

  if (order == null) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("Order details not found", color = Color.White)
    }
    return
  }

  val steps = listOf("Order Placed", "Payment Verified", "Packed in Warehouse", "Shipped via Courier", "Out for Delivery", "Delivered")
  val currentStepIndex = order.trackingStep.coerceIn(0, 5)

  Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBackClick, modifier = Modifier.size(36.dp).background(DarkSurfaceVariant, CircleShape)) {
          Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text("Order #ORD-${order.id + 1000}", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
      }
      Button(onClick = { showInvoiceModal = true }, colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)) {
        Icon(Icons.Default.Receipt, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Invoice", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    LazyColumn(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      contentPadding = PaddingValues(bottom = 20.dp)
    ) {
      // 1. Status Banner
      item {
        GlassCard(cornerRadius = 20.dp, borderColor = NeonPurple, modifier = Modifier.fillMaxWidth()) {
          Row(modifier = Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
              modifier = Modifier.size(54.dp),
              shape = CircleShape,
              color = NeonPurple.copy(alpha = 0.2f)
            ) {
              Icon(Icons.Default.LocalShipping, contentDescription = null, tint = NeonPurple, modifier = Modifier.padding(14.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
              Text("STATUS: ${order.status.uppercase()}", color = NeonPurple, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
              Text("Estimated Delivery: 3 Business Days", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
              Text("Carrier: CyberLogistics Express • Tracking ID: TRK908234", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
            }
          }
        }
      }

      // 2. Step Progress Bar
      item {
        GlassCard(cornerRadius = 20.dp, modifier = Modifier.fillMaxWidth()) {
          Column(modifier = Modifier.padding(20.dp)) {
            Text("📍 LIVE TRACKING PROGRESS", color = CyberCyan, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(16.dp))

            steps.forEachIndexed { index, stepName ->
              val isCompleted = index <= currentStepIndex
              val isCurrent = index == currentStepIndex

              Row(verticalAlignment = Alignment.Top) {
                // Node icon & vertical line
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(30.dp)) {
                  Surface(
                    modifier = Modifier.size(24.dp),
                    shape = CircleShape,
                    color = if (isCompleted) NeonGreen else DarkSurfaceVariant
                  ) {
                    Box(contentAlignment = Alignment.Center) {
                      if (isCompleted) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                      } else {
                        Text("${index + 1}", color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                      }
                    }
                  }
                  if (index < steps.size - 1) {
                    Box(
                      modifier = Modifier
                        .width(2.dp)
                        .height(32.dp)
                        .background(if (index < currentStepIndex) NeonGreen else GlassBorderDark)
                    )
                  }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                  Text(
                    text = stepName,
                    color = if (isCompleted) Color.White else Color.White.copy(alpha = 0.4f),
                    fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.Medium,
                    fontSize = 14.sp
                  )
                  if (isCurrent) {
                    Text("In progress right now...", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  } else if (isCompleted) {
                    Text("Completed", color = NeonGreen.copy(alpha = 0.8f), fontSize = 10.sp)
                  }
                }
              }
            }
          }
        }
      }

      // 3. Order Details
      item {
        GlassCard(cornerRadius = 18.dp, modifier = Modifier.fillMaxWidth()) {
          Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("ITEMS PURCHASED", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
            Text(order.itemsSummary, color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp, lineHeight = 20.sp)

            HorizontalDivider(color = GlassBorderDark, modifier = Modifier.padding(vertical = 4.dp))

            Text("SHIPPING DESTINATION", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
            Text(order.shippingAddress, color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)

            HorizontalDivider(color = GlassBorderDark, modifier = Modifier.padding(vertical = 4.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Payment Method", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
              Text(order.paymentMethod, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Total Amount Paid", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
              Text("$${String.format("%.2f", order.finalAmount)}", color = NeonGreen, fontSize = 18.sp, fontWeight = FontWeight.Black)
            }
          }
        }
      }
    }
  }

  // Invoice Modal
  if (showInvoiceModal) {
    AlertDialog(
      onDismissRequest = { showInvoiceModal = false },
      containerColor = DarkSurface,
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = CyberCyan)
          Spacer(modifier = Modifier.width(8.dp))
          Text("TAX INVOICE #INV-${order.id + 4000}", color = CyberCyan, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text("TRENDHUB 3D ENTERPRISES INC.", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
          Text("100 Cyberpunk Way, Silicon Valley, CA", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
          Text("Date: ${SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date(order.createdAt))}", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
          HorizontalDivider(color = GlassBorderDark, modifier = Modifier.padding(vertical = 4.dp))
          Text("Billed To: ${order.shippingAddress}", color = Color.White, fontSize = 12.sp)
          HorizontalDivider(color = GlassBorderDark, modifier = Modifier.padding(vertical = 4.dp))
          Text("Summary:", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
          Text(order.itemsSummary, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
          Spacer(modifier = Modifier.height(6.dp))
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Subtotal:", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            Text("$${String.format("%.2f", order.totalAmount)}", color = Color.White, fontSize = 12.sp)
          }
          if (order.discountAmount > 0) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Discount:", color = NeonGreen, fontSize = 12.sp)
              Text("-$${String.format("%.2f", order.discountAmount)}", color = NeonGreen, fontSize = 12.sp)
            }
          }
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Tax (8%):", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            Text("$${String.format("%.2f", order.taxAmount)}", color = Color.White, fontSize = 12.sp)
          }
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Shipping:", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            Text(if (order.shippingCost == 0.0) "FREE" else "$${String.format("%.2f", order.shippingCost)}", color = Color.White, fontSize = 12.sp)
          }
          HorizontalDivider(color = GlassBorderDark, modifier = Modifier.padding(vertical = 4.dp))
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("TOTAL PAID:", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
            Text("$${String.format("%.2f", order.finalAmount)}", color = NeonGreen, fontWeight = FontWeight.Black, fontSize = 16.sp)
          }
          if (copiedToast) {
            Spacer(modifier = Modifier.height(4.dp))
            Text("📋 Invoice summary copied to clipboard simulation!", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      },
      confirmButton = {
        Button(
          onClick = { copiedToast = true },
          colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
        ) {
          Icon(Icons.Default.Share, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Copy / Share Invoice", color = Color.Black, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showInvoiceModal = false }) { Text("Close") }
      }
    )
  }
}
