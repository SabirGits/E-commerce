package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun TrendHubTopBar(
  cartCount: Int,
  wishlistCount: Int,
  isAdmin: Boolean,
  onRoleToggle: () -> Unit,
  onSearchClick: () -> Unit,
  onCartClick: () -> Unit,
  onWishlistClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val roleBg by animateColorAsState(if (isAdmin) HotPink else CyberCyan.copy(alpha = 0.2f), label = "role_bg")

  Surface(
    color = DarkSurface.copy(alpha = 0.85f),
    tonalElevation = 8.dp,
    modifier = modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Logo & Title
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(Brush.linearGradient(listOf(NeonPurple, CyberCyan))),
            contentAlignment = Alignment.Center
          ) {
            Text("T", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "TRENDHUB",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                color = NeonGreen,
                shape = CircleShape
              ) {
                Text(
                  text = "3D",
                  color = Color.Black,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Black,
                  modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                )
              }
            }
            Text(
              text = "NEXT-GEN E-COMMERCE",
              color = CyberCyan,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        // Actions
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Role Switcher Pill (Customer vs Admin)
          Surface(
            color = roleBg,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.clickable { onRoleToggle() }
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = if (isAdmin) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                contentDescription = "Role",
                tint = if (isAdmin) Color.White else CyberCyan,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (isAdmin) "ADMIN" else "USER",
                color = if (isAdmin) Color.White else CyberCyan,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold
              )
            }
          }

          // Wishlist Button with Badge
          Box {
            IconButton(
              onClick = onWishlistClick,
              modifier = Modifier.size(36.dp).background(DarkSurfaceVariant, CircleShape)
            ) {
              Icon(Icons.Default.FavoriteBorder, contentDescription = "Wishlist", tint = Color.White, modifier = Modifier.size(18.dp))
            }
            if (wishlistCount > 0) {
              Surface(
                color = HotPink,
                shape = CircleShape,
                modifier = Modifier.align(Alignment.TopEnd).size(16.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Text("$wishlistCount", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          }

          // Cart Button with Badge
          Box {
            IconButton(
              onClick = onCartClick,
              modifier = Modifier.size(36.dp).background(DarkSurfaceVariant, CircleShape)
            ) {
              Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = CyberCyan, modifier = Modifier.size(18.dp))
            }
            if (cartCount > 0) {
              Surface(
                color = NeonGreen,
                shape = CircleShape,
                modifier = Modifier.align(Alignment.TopEnd).size(16.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Text("$cartCount", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Search Trigger Bar
      Surface(
        color = DarkSurfaceVariant,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderDark),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onSearchClick() }
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Search, contentDescription = "Search", tint = CyberCyan, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Search smartphones, laptops, drones, 3D gear...",
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 13.sp,
            modifier = Modifier.weight(1f)
          )
          Surface(
            color = NeonPurple.copy(alpha = 0.2f),
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = "AI SEARCH",
              color = NeonPurple,
              fontSize = 9.sp,
              fontWeight = FontWeight.Black,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
fun TrendHubBottomNav(
  currentRoute: String,
  isAdmin: Boolean,
  onNavigate: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val items = if (isAdmin) {
    listOf(
      BottomNavItem("admin_dash", "Admin", Icons.Default.Dashboard),
      BottomNavItem("home", "Store", Icons.Default.Storefront),
      BottomNavItem("products", "Catalog", Icons.Default.Inventory),
      BottomNavItem("orders", "Orders", Icons.Default.LocalShipping),
      BottomNavItem("profile", "Profile", Icons.Default.Person)
    )
  } else {
    listOf(
      BottomNavItem("home", "Home", Icons.Default.Home),
      BottomNavItem("products", "Explore", Icons.Default.Explore),
      BottomNavItem("cart", "Cart", Icons.Default.ShoppingCart),
      BottomNavItem("wishlist", "Wishlist", Icons.Default.Favorite),
      BottomNavItem("profile", "Profile", Icons.Default.Person)
    )
  }

  Surface(
    color = DarkSurface.copy(alpha = 0.95f),
    tonalElevation = 12.dp,
    modifier = modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp, horizontal = 12.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      items.forEach { item ->
        val selected = currentRoute == item.route
        val pillBg by animateColorAsState(
          if (selected) NeonPurple else Color.Transparent,
          label = "nav_pill"
        )

        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier
            .clickable { onNavigate(item.route) }
            .padding(vertical = 4.dp, horizontal = 8.dp)
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .background(pillBg)
              .padding(horizontal = 14.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = item.icon,
              contentDescription = item.label,
              tint = if (selected) Color.White else Color.White.copy(alpha = 0.6f),
              modifier = Modifier.size(20.dp)
            )
          }
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = item.label,
            color = if (selected) Color.White else Color.White.copy(alpha = 0.6f),
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
          )
        }
      }
    }
  }
}

data class BottomNavItem(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
fun SectionHeader(
  title: String,
  subtitle: String? = null,
  actionText: String? = "See All",
  onActionClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .width(4.dp)
          .height(22.dp)
          .clip(RoundedCornerShape(2.dp))
          .background(Brush.verticalGradient(listOf(NeonPurple, CyberCyan)))
      )
      Spacer(modifier = Modifier.width(8.dp))
      Column {
        Text(text = title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        if (subtitle != null) {
          Text(text = subtitle, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
        }
      }
    }

    if (actionText != null && onActionClick != null) {
      TextButton(onClick = onActionClick) {
        Text(text = actionText, color = CyberCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

@Composable
fun FlashSaleTimer(modifier: Modifier = Modifier) {
  var secondsLeft by remember { mutableStateOf(4 * 3600 + 23 * 60 + 58) }

  LaunchedEffect(Unit) {
    while (secondsLeft > 0) {
      delay(1000)
      secondsLeft--
    }
  }

  val hours = secondsLeft / 3600
  val minutes = (secondsLeft % 3600) / 60
  val seconds = secondsLeft % 60

  Row(
    modifier = modifier
      .background(HotPink.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
      .border(1.dp, HotPink, RoundedCornerShape(12.dp))
      .padding(horizontal = 12.dp, vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    Icon(Icons.Default.Bolt, contentDescription = "Flash", tint = HotPink, modifier = Modifier.size(16.dp))
    Text("ENDS IN:", color = HotPink, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
    Spacer(modifier = Modifier.width(4.dp))
    TimerBox(String.format("%02d", hours))
    Text(":", color = Color.White, fontWeight = FontWeight.Bold)
    TimerBox(String.format("%02d", minutes))
    Text(":", color = Color.White, fontWeight = FontWeight.Bold)
    TimerBox(String.format("%02d", seconds))
  }
}

@Composable
private fun TimerBox(valText: String) {
  Surface(
    color = HotPink,
    shape = RoundedCornerShape(4.dp)
  ) {
    Text(
      text = valText,
      color = Color.White,
      fontSize = 11.sp,
      fontWeight = FontWeight.Black,
      modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
    )
  }
}
