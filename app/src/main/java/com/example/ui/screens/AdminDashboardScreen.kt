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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.*
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.TrendHubViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
  viewModel: TrendHubViewModel,
  onBackToStore: () -> Unit,
  modifier: Modifier = Modifier
) {
  val products by viewModel.allProducts.collectAsStateWithLifecycle()
  val orders by viewModel.orders.collectAsStateWithLifecycle()
  val banners by viewModel.allBanners.collectAsStateWithLifecycle()
  val coupons by viewModel.activeCoupons.collectAsStateWithLifecycle()

  var selectedTab by remember { mutableStateOf(0) } // 0 = Overview, 1 = Products, 2 = Orders, 3 = Banners, 4 = Coupons

  var showAddProductModal by remember { mutableStateOf(false) }
  var showAddBannerModal by remember { mutableStateOf(false) }
  var showAddCouponModal by remember { mutableStateOf(false) }

  val totalRevenue = remember(orders) { orders.sumOf { it.finalAmount } + 184290.0 }
  val totalOrdersCount = remember(orders) { orders.size + 342 }
  val totalStock = remember(products) { products.sumOf { it.stock } }

  Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
    // Header
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(color = HotPink, shape = RoundedCornerShape(8.dp)) {
          Text("👑 ADMIN", color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text("Management Suite", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
      }
      Button(
        onClick = {
          viewModel.toggleRoleMode()
          onBackToStore()
        },
        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant)
      ) {
        Icon(Icons.Default.Storefront, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Exit Admin", color = CyberCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Admin Tabs Bar
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      val tabs = listOf("📊 Overview", "📦 Catalog (${products.size})", "🚚 Orders (${orders.size})", "📢 Ads & Banners", "🎟️ Coupons")
      items(tabs.size) { index ->
        val sel = selectedTab == index
        val bg by animateColorAsState(if (sel) HotPink else DarkSurfaceVariant, label = "adm_tab")
        Surface(
          color = bg,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.clickable { selectedTab = index }
        ) {
          Text(
            text = tabs[index],
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = if (sel) FontWeight.ExtraBold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Tab Contents
    when (selectedTab) {
      0 -> {
        LazyColumn(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(14.dp),
          contentPadding = PaddingValues(bottom = 20.dp)
        ) {
          // Stats Grid
          item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              StatCard("Total Revenue", "$${String.format("%.0f", totalRevenue)}", Icons.Default.AttachMoney, NeonGreen, Modifier.weight(1f))
              StatCard("Total Orders", "$totalOrdersCount", Icons.Default.ShoppingBag, CyberCyan, Modifier.weight(1f))
            }
          }
          item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              StatCard("Active Users", "1,420", Icons.Default.People, HotPink, Modifier.weight(1f))
              StatCard("Stock Units", "$totalStock", Icons.Default.Inventory, AccentGold, Modifier.weight(1f))
            }
          }

          // Interactive Revenue Bar Chart Simulation
          item {
            GlassCard(cornerRadius = 20.dp, borderColor = CyberCyan, modifier = Modifier.fillMaxWidth()) {
              Column(modifier = Modifier.padding(18.dp)) {
                Text("📈 MONTHLY REVENUE GROWTH", color = CyberCyan, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                Text("Consistent upward trajectory in 3D hardware sales", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)

                Spacer(modifier = Modifier.height(16.dp))

                val monthlyData = listOf("Jan" to 0.4f, "Feb" to 0.55f, "Mar" to 0.5f, "Apr" to 0.7f, "May" to 0.85f, "Jun" to 1.0f)
                Row(
                  modifier = Modifier.fillMaxWidth().height(140.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.Bottom
                ) {
                  monthlyData.forEach { (month, heightRatio) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Box(
                        modifier = Modifier
                          .width(28.dp)
                          .fillMaxHeight(heightRatio)
                          .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                          .background(if (month == "Jun") HotPink else CyberCyan.copy(alpha = 0.7f))
                      )
                      Spacer(modifier = Modifier.height(6.dp))
                      Text(month, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                  }
                }
              }
            }
          }
        }
      }

      1 -> {
        // Products CRUD
        Column(modifier = Modifier.weight(1f)) {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Product Inventory (${products.size})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Button(onClick = { showAddProductModal = true }, colors = ButtonDefaults.buttonColors(containerColor = HotPink)) {
              Text("+ Add Product", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
          }
          Spacer(modifier = Modifier.height(10.dp))
          LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 20.dp)) {
            items(products) { prod ->
              GlassCard(cornerRadius = 14.dp, modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(prod.brand.uppercase(), color = CyberCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(prod.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("$${prod.price} • Stock: ${prod.stock} • Model: ${prod.modelType}", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                  }
                  IconButton(onClick = { viewModel.deleteProduct(prod) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = HotPink)
                  }
                }
              }
            }
          }
        }
      }

      2 -> {
        // Orders Management
        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 20.dp)) {
          items(orders) { order ->
            GlassCard(cornerRadius = 16.dp, modifier = Modifier.fillMaxWidth()) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text("#ORD-${order.id + 1000} • ${SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date(order.createdAt))}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                  Text("$${String.format("%.2f", order.finalAmount)}", color = NeonGreen, fontWeight = FontWeight.Black, fontSize = 15.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(order.itemsSummary, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("Address: ${order.shippingAddress}", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                  Surface(color = NeonPurple.copy(alpha = 0.2f), shape = RoundedCornerShape(6.dp)) {
                    Text("Status: ${order.status}", color = NeonPurple, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                  }

                  val nextAction = when (order.status) {
                    "Processing" -> "Shipped" to 3
                    "Shipped" -> "Delivered" to 5
                    else -> null
                  }

                  if (nextAction != null) {
                    Button(
                      onClick = { viewModel.updateOrderStatus(order, nextAction.first, nextAction.second) },
                      colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                      contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                      modifier = Modifier.height(32.dp)
                    ) {
                      Text("Advance to ${nextAction.first}", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                  } else {
                    Text("✔ Order Complete", color = NeonGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }
        }
      }

      3 -> {
        // Banners & Ads CRUD
        Column(modifier = Modifier.weight(1f)) {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Active Ad Campaigns (${banners.size})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Button(onClick = { showAddBannerModal = true }, colors = ButtonDefaults.buttonColors(containerColor = HotPink)) {
              Text("+ Create Ad", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
          }
          Spacer(modifier = Modifier.height(10.dp))
          LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 20.dp)) {
            items(banners) { ban ->
              GlassCard(cornerRadius = 14.dp, modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                  Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Surface(color = HotPink, shape = RoundedCornerShape(4.dp)) {
                        Text(ban.adType, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                      }
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(ban.discountBadge, color = NeonGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(ban.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(ban.subtitle, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                  }
                  IconButton(onClick = { viewModel.deleteBanner(ban) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = HotPink)
                  }
                }
              }
            }
          }
        }
      }

      4 -> {
        // Coupons CRUD
        Column(modifier = Modifier.weight(1f)) {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Discount Coupons (${coupons.size})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Button(onClick = { showAddCouponModal = true }, colors = ButtonDefaults.buttonColors(containerColor = HotPink)) {
              Text("+ New Coupon", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
          }
          Spacer(modifier = Modifier.height(10.dp))
          LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 20.dp)) {
            items(coupons) { coup ->
              GlassCard(cornerRadius = 14.dp, borderColor = CyberCyan.copy(alpha = 0.5f), modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                  Surface(color = CyberCyan.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, CyberCyan)) {
                    Text(coup.code, color = CyberCyan, fontWeight = FontWeight.Black, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                  }
                  Spacer(modifier = Modifier.width(12.dp))
                  Column(modifier = Modifier.weight(1f)) {
                    Text("Save ${coup.discountPercent}% OFF", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Min order: $${coup.minOrderAmount} • ${coup.description}", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  // Modals
  if (showAddProductModal) {
    var pName by remember { mutableStateOf("RTX 5090 Cyber Edition") }
    var pBrand by remember { mutableStateOf("Nvidia") }
    var pPrice by remember { mutableStateOf("1999.0") }
    var pCat by remember { mutableStateOf("Laptops") }
    var pModel by remember { mutableStateOf("GPU") }
    var pStock by remember { mutableStateOf("15") }

    AlertDialog(
      onDismissRequest = { showAddProductModal = false },
      containerColor = DarkSurface,
      title = { Text("Add New Product to Catalog", color = HotPink, fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(value = pName, onValueChange = { pName = it }, label = { Text("Product Name") }, singleLine = true)
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = pBrand, onValueChange = { pBrand = it }, label = { Text("Brand") }, singleLine = true, modifier = Modifier.weight(1f))
            OutlinedTextField(value = pPrice, onValueChange = { pPrice = it }, label = { Text("Price ($)") }, singleLine = true, modifier = Modifier.width(100.dp))
          }
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = pCat, onValueChange = { pCat = it }, label = { Text("Category") }, singleLine = true, modifier = Modifier.weight(1f))
            OutlinedTextField(value = pModel, onValueChange = { pModel = it }, label = { Text("3D Model (SMARTPHONE/LAPTOP/DRONE/HEADPHONES/CAMERA/GPU)") }, singleLine = true, modifier = Modifier.weight(1f))
          }
          OutlinedTextField(value = pStock, onValueChange = { pStock = it }, label = { Text("Stock Quantity") }, singleLine = true)
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val pr = pPrice.toDoubleOrNull() ?: 99.0
            val st = pStock.toIntOrNull() ?: 10
            viewModel.saveProduct(
              ProductEntity(
                name = pName,
                brand = pBrand,
                price = pr,
                originalPrice = pr * 1.2,
                discountPercentage = 15,
                rating = 4.9,
                reviewsCount = 1,
                imageUrl = "https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?auto=format&fit=crop&w=800&q=80",
                description = "Brand new next-gen tech product added via Admin Suite.",
                specs = "3D hardware matrix ready\n60fps viewport enabled",
                category = pCat,
                modelType = pModel,
                stock = st
              )
            )
            showAddProductModal = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = HotPink)
        ) {
          Text("Create Product", color = Color.White, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = { TextButton(onClick = { showAddProductModal = false }) { Text("Cancel") } }
    )
  }

  if (showAddBannerModal) {
    var bTitle by remember { mutableStateOf("MEGA CYBER SALE") }
    var bSub by remember { mutableStateOf("Up to 40% OFF all 3D gear") }
    var bBadge by remember { mutableStateOf("CODE: CYBER40") }
    var bType by remember { mutableStateOf("CAROUSEL") }

    AlertDialog(
      onDismissRequest = { showAddBannerModal = false },
      containerColor = DarkSurface,
      title = { Text("Create New Ad Campaign", color = HotPink, fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(value = bTitle, onValueChange = { bTitle = it }, label = { Text("Ad Title") }, singleLine = true)
          OutlinedTextField(value = bSub, onValueChange = { bSub = it }, label = { Text("Subtitle / Description") }, singleLine = true)
          OutlinedTextField(value = bBadge, onValueChange = { bBadge = it }, label = { Text("Discount Badge") }, singleLine = true)
          OutlinedTextField(value = bType, onValueChange = { bType = it }, label = { Text("Ad Type (TOP_BANNER/CAROUSEL/SIDEBAR/POPUP)") }, singleLine = true)
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.saveBanner(
              AdBannerEntity(
                title = bTitle,
                subtitle = bSub,
                discountBadge = bBadge,
                adType = bType.uppercase(),
                targetCategory = "Laptops",
                imageUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?auto=format&fit=crop&w=800&q=80"
              )
            )
            showAddBannerModal = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = HotPink)
        ) {
          Text("Launch Ad", color = Color.White, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = { TextButton(onClick = { showAddBannerModal = false }) { Text("Cancel") } }
    )
  }

  if (showAddCouponModal) {
    var cCode by remember { mutableStateOf("VIPTECH50") }
    var cPercent by remember { mutableStateOf("30") }
    var cMin by remember { mutableStateOf("150") }
    var cDesc by remember { mutableStateOf("Exclusive 30% discount on orders above $150") }

    AlertDialog(
      onDismissRequest = { showAddCouponModal = false },
      containerColor = DarkSurface,
      title = { Text("Create Discount Coupon", color = HotPink, fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(value = cCode, onValueChange = { cCode = it }, label = { Text("Promo Code") }, singleLine = true)
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = cPercent, onValueChange = { cPercent = it }, label = { Text("Discount (%)") }, singleLine = true, modifier = Modifier.weight(1f))
            OutlinedTextField(value = cMin, onValueChange = { cMin = it }, label = { Text("Min Order ($)") }, singleLine = true, modifier = Modifier.width(110.dp))
          }
          OutlinedTextField(value = cDesc, onValueChange = { cDesc = it }, label = { Text("Description") })
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.createCoupon(
              code = cCode,
              percent = cPercent.toIntOrNull() ?: 20,
              minAmount = cMin.toDoubleOrNull() ?: 50.0,
              desc = cDesc
            )
            showAddCouponModal = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = HotPink)
        ) {
          Text("Create Coupon", color = Color.White, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = { TextButton(onClick = { showAddCouponModal = false }) { Text("Cancel") } }
    )
  }
}

@Composable
fun StatCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier = Modifier) {
  GlassCard(cornerRadius = 16.dp, borderColor = color.copy(alpha = 0.5f), modifier = modifier.height(100.dp)) {
    Column(modifier = Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.SpaceBetween) {
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
        Text(title, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
      }
      Text(value, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
    }
  }
}
