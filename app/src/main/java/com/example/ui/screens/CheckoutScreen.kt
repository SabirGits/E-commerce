package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.OrderEntity
import com.example.data.entity.UserAddressEntity
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.RazorpayStatus
import com.example.ui.viewmodel.TrendHubViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
  viewModel: TrendHubViewModel,
  onOrderSuccess: (OrderEntity) -> Unit,
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
  val addresses by viewModel.addresses.collectAsStateWithLifecycle()
  val appliedCoupon by viewModel.appliedCoupon.collectAsStateWithLifecycle()
  val razorpayStatus by viewModel.razorpayStatus.collectAsStateWithLifecycle()
  val otpInput by viewModel.otpInput.collectAsStateWithLifecycle()

  var selectedAddress by remember { mutableStateOf<UserAddressEntity?>(null) }
  var selectedPayment by remember { mutableStateOf("Razorpay (Secure Gateway)") }
  var showAddAddressModal by remember { mutableStateOf(false) }

  // Set default address
  LaunchedEffect(addresses) {
    if (selectedAddress == null && addresses.isNotEmpty()) {
      selectedAddress = addresses.find { it.isDefault } ?: addresses.first()
    }
  }

  val subtotal = cartItems.sumOf { it.productPrice * it.quantity }
  val discount = if (appliedCoupon != null) subtotal * (appliedCoupon!!.discountPercent / 100.0) else 0.0
  val tax = (subtotal - discount) * 0.08
  val shipping = if (subtotal > 100.0 || appliedCoupon?.code == "FREEFLY") 0.0 else 15.0
  val finalTotal = (subtotal - discount + tax + shipping).coerceAtLeast(0.0)

  val paymentOptions = listOf(
    "Razorpay (Secure Gateway)" to "Instant OTP verification via Gmail SMTP",
    "Credit / Debit Card (Visa/MC)" to "Pay securely using card details",
    "UPI / Google Pay / PhonePe" to "Fast UPI payment transfer",
    "Cash on Delivery (COD)" to "Pay when order arrives at doorstep"
  )

  Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
    // Header
    Row(verticalAlignment = Alignment.CenterVertically) {
      IconButton(onClick = onBackClick, modifier = Modifier.size(36.dp).background(DarkSurfaceVariant, CircleShape)) {
        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
      }
      Spacer(modifier = Modifier.width(12.dp))
      Text("Secure Checkout", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
    }

    Spacer(modifier = Modifier.height(14.dp))

    LazyColumn(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      contentPadding = PaddingValues(bottom = 20.dp)
    ) {
      // 1. Shipping Address Section
      item {
        GlassCard(cornerRadius = 18.dp, modifier = Modifier.fillMaxWidth()) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("SHIPPING ADDRESS", color = CyberCyan, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
              }
              TextButton(onClick = { showAddAddressModal = true }) {
                Text("+ Add New", color = HotPink, fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (addresses.isEmpty()) {
              Surface(
                color = DarkSurfaceVariant,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, GlassBorderDark),
                modifier = Modifier.fillMaxWidth().clickable { showAddAddressModal = true }
              ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.AddLocationAlt, contentDescription = null, tint = HotPink)
                  Spacer(modifier = Modifier.width(12.dp))
                  Text("No saved addresses. Tap to add your shipping address.", color = Color.White, fontSize = 13.sp)
                }
              }
            } else {
              addresses.forEach { addr ->
                val sel = selectedAddress?.id == addr.id
                Surface(
                  color = if (sel) DarkSurfaceVariant else DarkBackground.copy(alpha = 0.5f),
                  shape = RoundedCornerShape(12.dp),
                  border = BorderStroke(1.dp, if (sel) CyberCyan else GlassBorderDark),
                  modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { selectedAddress = addr }
                ) {
                  Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                      selected = sel,
                      onClick = { selectedAddress = addr },
                      colors = RadioButtonDefaults.colors(selectedColor = CyberCyan)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(addr.fullName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        if (addr.isDefault) {
                          Spacer(modifier = Modifier.width(8.dp))
                          Surface(color = NeonPurple, shape = RoundedCornerShape(4.dp)) {
                            Text("DEFAULT", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                          }
                        }
                      }
                      Text("${addr.streetAddress}, ${addr.city}, ${addr.state} ${addr.zipCode}", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                      Text("Phone: ${addr.phone}", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                    }
                  }
                }
              }
            }
          }
        }
      }

      // 2. Payment Methods Section
      item {
        GlassCard(cornerRadius = 18.dp, modifier = Modifier.fillMaxWidth()) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Payment, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("SELECT PAYMENT METHOD", color = NeonGreen, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            paymentOptions.forEach { (method, desc) ->
              val sel = selectedPayment == method
              Surface(
                color = if (sel) DarkSurfaceVariant else DarkBackground.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, if (sel) NeonGreen else GlassBorderDark),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { selectedPayment = method }
              ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                  RadioButton(
                    selected = sel,
                    onClick = { selectedPayment = method },
                    colors = RadioButtonDefaults.colors(selectedColor = NeonGreen)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Column {
                    Text(method, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(desc, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                  }
                }
              }
            }
          }
        }
      }

      // 3. Order Summary
      item {
        GlassCard(cornerRadius = 18.dp, modifier = Modifier.fillMaxWidth()) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text("ORDER SUMMARY", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(10.dp))
            cartItems.forEach { item ->
              Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${item.productName} x${item.quantity}", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                Text("$${String.format("%.2f", item.productPrice * item.quantity)}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
              }
            }
            HorizontalDivider(color = GlassBorderDark, modifier = Modifier.padding(vertical = 8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Total Amount Due", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
              Text("$${String.format("%.2f", finalTotal)}", color = NeonGreen, fontSize = 18.sp, fontWeight = FontWeight.Black)
            }
          }
        }
      }
    }

    // Pay Button
    Button(
      onClick = {
        if (selectedAddress == null) {
          showAddAddressModal = true
        } else if (selectedPayment.contains("Razorpay")) {
          viewModel.initiateRazorpayCheckout()
        } else {
          // Direct placement for COD/UPI
          val addrStr = "${selectedAddress!!.fullName}, ${selectedAddress!!.streetAddress}, ${selectedAddress!!.city}, ${selectedAddress!!.state} ${selectedAddress!!.zipCode} (Ph: ${selectedAddress!!.phone})"
          viewModel.verifyRazorpayOtpAndPlaceOrder(addrStr, selectedPayment, onOrderSuccess)
        }
      },
      colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
      shape = RoundedCornerShape(14.dp),
      modifier = Modifier.fillMaxWidth().height(52.dp)
    ) {
      Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color.White)
      Spacer(modifier = Modifier.width(8.dp))
      Text("Pay $${String.format("%.2f", finalTotal)} Now", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
    }
  }

  // Razorpay Gateway Interactive Simulation Modal
  if (razorpayStatus == RazorpayStatus.WAITING_FOR_OTP || razorpayStatus == RazorpayStatus.FAILED || razorpayStatus == RazorpayStatus.SUCCESS) {
    Dialog(onDismissRequest = { viewModel.resetRazorpayStatus() }) {
      GlassCard(
        cornerRadius = 24.dp,
        borderColor = CyberCyan,
        modifier = Modifier.fillMaxWidth().padding(16.dp)
      ) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
          // Razorpay Header Badge
          Surface(color = Color(0xFF032541), shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, CyberCyan)) {
            Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
              Text("⚡ RAZORPAY", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Text("SECURE GATEWAY", color = CyberCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          if (razorpayStatus == RazorpayStatus.SUCCESS) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text("Payment Successful!", color = NeonGreen, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            Text("Redirecting to order tracking...", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
          } else {
            Text("Merchant: TRENDHUB 3D INC", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text("Amount: $${String.format("%.2f", finalTotal)}", color = NeonGreen, fontSize = 24.sp, fontWeight = FontWeight.Black)

            Spacer(modifier = Modifier.height(12.dp))

            Surface(color = DarkSurfaceVariant, shape = RoundedCornerShape(10.dp), border = BorderStroke(1.dp, GlassBorderDark)) {
              Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📧 NODEMAILER SMTP OTP SENT", color = HotPink, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                Text("A 4-digit verification code was sent to:", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                Text(viewModel.currentUserEmail, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Enter 4-Digit Security OTP", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
              value = otpInput,
              onValueChange = { viewModel.setOtpInput(it) },
              placeholder = { Text("e.g. 1234", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
              singleLine = true,
              textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontSize = 22.sp, fontWeight = FontWeight.Black, letterSpacing = 8.sp),
              colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = CyberCyan),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.fillMaxWidth().height(56.dp)
            )

            if (razorpayStatus == RazorpayStatus.FAILED) {
              Spacer(modifier = Modifier.height(6.dp))
              Text("❌ Incorrect OTP code. Try '1234'", color = HotPink, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = { viewModel.setOtpInput("1234") }) {
              Text("💡 Quick Test: Fill Test OTP (1234)", color = CyberCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
              onClick = {
                val addrStr = "${selectedAddress!!.fullName}, ${selectedAddress!!.streetAddress}, ${selectedAddress!!.city}, ${selectedAddress!!.state} ${selectedAddress!!.zipCode} (Ph: ${selectedAddress!!.phone})"
                viewModel.verifyRazorpayOtpAndPlaceOrder(addrStr, selectedPayment, onOrderSuccess)
              },
              colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
              modifier = Modifier.fillMaxWidth().height(48.dp),
              shape = RoundedCornerShape(12.dp)
            ) {
              Text("Verify & Complete Payment", color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            }

            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = { viewModel.resetRazorpayStatus() }) {
              Text("Cancel Payment", color = Color.White.copy(alpha = 0.5f))
            }
          }
        }
      }
    }
  }

  // Add Address Modal
  if (showAddAddressModal) {
    var name by remember { mutableStateOf("Sabir Rayabka") }
    var phone by remember { mutableStateOf("+1 555-0192") }
    var street by remember { mutableStateOf("108 Cyber Avenue, Suite 404") }
    var city by remember { mutableStateOf("San Francisco") }
    var state by remember { mutableStateOf("CA") }
    var zip by remember { mutableStateOf("94105") }

    AlertDialog(
      onDismissRequest = { showAddAddressModal = false },
      containerColor = DarkSurface,
      title = { Text("Add New Shipping Address", color = CyberCyan, fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, singleLine = true)
          OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") }, singleLine = true)
          OutlinedTextField(value = street, onValueChange = { street = it }, label = { Text("Street Address") }, singleLine = true)
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("City") }, singleLine = true, modifier = Modifier.weight(1f))
            OutlinedTextField(value = state, onValueChange = { state = it }, label = { Text("State") }, singleLine = true, modifier = Modifier.width(80.dp))
          }
          OutlinedTextField(value = zip, onValueChange = { zip = it }, label = { Text("Zip Code") }, singleLine = true)
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (name.isNotBlank() && street.isNotBlank()) {
              viewModel.addAddress(name, phone, street, city, state, zip)
              showAddAddressModal = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
        ) {
          Text("Save Address", color = Color.Black, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddAddressModal = false }) { Text("Cancel") }
      }
    )
  }
}
