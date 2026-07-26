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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.TrendHubViewModel

@Composable
fun ProfileScreen(
  viewModel: TrendHubViewModel,
  onNavigateToOrders: () -> Unit,
  onNavigateToWishlist: () -> Unit,
  onNavigateToAdmin: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isAdmin by viewModel.isAdminMode.collectAsStateWithLifecycle()
  val addresses by viewModel.addresses.collectAsStateWithLifecycle()
  var showAddAddressModal by remember { mutableStateOf(false) }

  var notificationsEnabled by remember { mutableStateOf(true) }
  var hwAcceleration by remember { mutableStateOf(true) }

  Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
    Text("👤 Account & Profile", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
    Spacer(modifier = Modifier.height(14.dp))

    LazyColumn(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      contentPadding = PaddingValues(bottom = 20.dp)
    ) {
      // 1. User Card
      item {
        GlassCard(cornerRadius = 20.dp, borderColor = CyberCyan, modifier = Modifier.fillMaxWidth()) {
          Row(modifier = Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(NeonPurple, CyberCyan))),
              contentAlignment = Alignment.Center
            ) {
              Text("S", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text("Sabir Rayabka", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
              Text(viewModel.currentUserEmail, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
              Spacer(modifier = Modifier.height(6.dp))
              Surface(color = if (isAdmin) HotPink else NeonGreen, shape = RoundedCornerShape(6.dp)) {
                Text(
                  text = if (isAdmin) "👑 ADMIN ACCESS GRANTED" else "🌟 VIP CUSTOMER",
                  color = if (isAdmin) Color.White else Color.Black,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Black,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }
            }
          }
        }
      }

      // 2. Admin Dashboard Switcher Banner
      item {
        GlassCard(
          cornerRadius = 18.dp,
          borderColor = HotPink,
          onClick = {
            viewModel.toggleRoleMode()
            if (!isAdmin) {
              onNavigateToAdmin()
            }
          },
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(46.dp), shape = RoundedCornerShape(12.dp), color = HotPink.copy(alpha = 0.2f)) {
              Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = HotPink, modifier = Modifier.padding(10.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text("Admin Management Suite", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
              Text(if (isAdmin) "Currently in Admin Mode. Click to toggle to User" else "Tap to switch to Admin Dashboard & manage catalog", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
            }
            Switch(
              checked = isAdmin,
              onCheckedChange = {
                viewModel.toggleRoleMode()
                if (!isAdmin) onNavigateToAdmin()
              },
              colors = SwitchDefaults.colors(checkedThumbColor = HotPink, checkedTrackColor = HotPink.copy(alpha = 0.4f))
            )
          }
        }
      }

      // 3. Quick Links
      item {
        GlassCard(cornerRadius = 18.dp, modifier = Modifier.fillMaxWidth()) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("QUICK SHORTCUTS", color = CyberCyan, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)

            ProfileRowItem("My Order History", Icons.Default.LocalShipping, CyberCyan, onClick = onNavigateToOrders)
            HorizontalDivider(color = GlassBorderDark)
            ProfileRowItem("Saved Wishlist", Icons.Default.Favorite, HotPink, onClick = onNavigateToWishlist)
          }
        }
      }

      // 4. Saved Addresses
      item {
        GlassCard(cornerRadius = 18.dp, modifier = Modifier.fillMaxWidth()) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
              Text("SAVED ADDRESSES (${addresses.size})", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
              TextButton(onClick = { showAddAddressModal = true }) {
                Text("+ Add New", color = CyberCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }

            if (addresses.isEmpty()) {
              Text("No saved addresses yet.", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
            } else {
              addresses.forEach { addr ->
                Surface(color = DarkBackground.copy(alpha = 0.6f), shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                  Column(modifier = Modifier.padding(10.dp)) {
                    Text(addr.fullName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("${addr.streetAddress}, ${addr.city}, ${addr.state} ${addr.zipCode}", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                  }
                }
              }
            }
          }
        }
      }

      // 5. Settings
      item {
        GlassCard(cornerRadius = 18.dp, modifier = Modifier.fillMaxWidth()) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("APP PREFERENCES", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
              Column {
                Text("Push Notifications", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Flash sales and order updates", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
              }
              Switch(checked = notificationsEnabled, onCheckedChange = { notificationsEnabled = it })
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
              Column {
                Text("3D Hardware Acceleration", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Enable 60 FPS wireframe matrix rendering", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
              }
              Switch(checked = hwAcceleration, onCheckedChange = { hwAcceleration = it }, colors = SwitchDefaults.colors(checkedThumbColor = CyberCyan))
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
              Column {
                Text("Cyberpunk Dark Mode", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Always active • Enterprise OLED contrast", color = NeonGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
              Icon(Icons.Default.Lock, contentDescription = null, tint = NeonGreen)
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
      title = { Text("Add New Address", color = CyberCyan, fontWeight = FontWeight.Bold) },
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
          Text("Save", color = Color.Black, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddAddressModal = false }) { Text("Cancel") }
      }
    )
  }
}

@Composable
fun ProfileRowItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color, onClick: () -> Unit) {
  Row(
    modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Surface(modifier = Modifier.size(36.dp), shape = RoundedCornerShape(10.dp), color = tint.copy(alpha = 0.2f)) {
      Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.padding(8.dp))
    }
    Spacer(modifier = Modifier.width(12.dp))
    Text(label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f))
    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
  }
}
