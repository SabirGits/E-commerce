package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalOffer
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
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.entity.AdBannerEntity
import com.example.ui.theme.*

@Composable
fun TopBannerAdView(
  banner: AdBannerEntity,
  onDismiss: () -> Unit,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  var visible by remember { mutableStateOf(true) }
  AnimatedVisibility(visible = visible) {
    Surface(
      color = DarkSurfaceVariant,
      tonalElevation = 6.dp,
      modifier = modifier
        .fillMaxWidth()
        .clickable { onClick() }
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.horizontalGradient(
              colors = listOf(DeepPurple, CyberCyan.copy(alpha = 0.8f), NeonPurple)
            )
          )
          .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
          Surface(
            color = HotPink,
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.padding(end = 10.dp)
          ) {
            Text(
              text = banner.discountBadge,
              color = Color.White,
              fontSize = 11.sp,
              fontWeight = FontWeight.ExtraBold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
          Column {
            Text(
              text = banner.title,
              color = Color.White,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Text(
              text = banner.subtitle,
              color = Color.White.copy(alpha = 0.85f),
              fontSize = 11.sp,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        IconButton(
          onClick = {
            visible = false
            onDismiss()
          },
          modifier = Modifier.size(24.dp)
        ) {
          Icon(Icons.Default.Close, contentDescription = "Dismiss Ad", tint = Color.White, modifier = Modifier.size(16.dp))
        }
      }
    }
  }
}

@Composable
fun CarouselAdView(
  banners: List<AdBannerEntity>,
  onBannerClick: (AdBannerEntity) -> Unit,
  modifier: Modifier = Modifier
) {
  if (banners.isEmpty()) return

  var selectedIndex by remember { mutableStateOf(0) }
  val currentBanner = banners[selectedIndex % banners.size]

  // Auto rotate banner every 5 seconds
  LaunchedEffect(banners.size) {
    if (banners.size > 1) {
      while (true) {
        kotlinx.coroutines.delay(5000)
        selectedIndex = (selectedIndex + 1) % banners.size
      }
    }
  }

  Column(modifier = modifier.fillMaxWidth()) {
    GlassCard(
      cornerRadius = 20.dp,
      borderColor = NeonPurple.copy(alpha = 0.5f),
      onClick = { onBannerClick(currentBanner) },
      modifier = Modifier
        .fillMaxWidth()
        .height(180.dp)
    ) {
      Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
          model = currentBanner.imageUrl,
          contentDescription = currentBanner.title,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )

        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.horizontalGradient(
                colors = listOf(
                  DarkBackground.copy(alpha = 0.95f),
                  DarkBackground.copy(alpha = 0.6f),
                  Color.Transparent
                )
              )
            )
        )

        Column(
          modifier = Modifier
            .align(Alignment.CenterStart)
            .padding(20.dp)
            .fillMaxWidth(0.7f),
          verticalArrangement = Arrangement.Center
        ) {
          Surface(
            color = CyberCyan.copy(alpha = 0.2f),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan)
          ) {
            Text(
              text = "⚡ FEATURED AD • ${currentBanner.discountBadge}",
              color = CyberCyan,
              fontSize = 10.sp,
              fontWeight = FontWeight.ExtraBold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = currentBanner.title,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = currentBanner.subtitle,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 12.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
          Spacer(modifier = Modifier.height(12.dp))
          Button(
            onClick = { onBannerClick(currentBanner) },
            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            modifier = Modifier.height(34.dp)
          ) {
            Text("Explore Deal", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    if (banners.size > 1) {
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.align(Alignment.CenterHorizontally),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        banners.forEachIndexed { index, _ ->
          val isSelected = index == selectedIndex
          Box(
            modifier = Modifier
              .width(if (isSelected) 24.dp else 8.dp)
              .height(8.dp)
              .clip(CircleShape)
              .background(if (isSelected) CyberCyan else Color.White.copy(alpha = 0.3f))
              .clickable { selectedIndex = index }
          )
        }
      }
    }
  }
}

@Composable
fun SidebarInlineAdView(
  banner: AdBannerEntity,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  GlassCard(
    cornerRadius = 16.dp,
    borderColor = HotPink.copy(alpha = 0.6f),
    onClick = onClick,
    modifier = modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Surface(
        modifier = Modifier.size(50.dp),
        shape = RoundedCornerShape(12.dp),
        color = HotPink.copy(alpha = 0.2f)
      ) {
        Icon(
          imageVector = Icons.Default.LocalOffer,
          contentDescription = null,
          tint = HotPink,
          modifier = Modifier.padding(12.dp)
        )
      }
      Spacer(modifier = Modifier.width(14.dp))
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "SPECIAL OFFER AD",
            color = HotPink,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold
          )
          Spacer(modifier = Modifier.width(6.dp))
          Surface(
            color = NeonGreen.copy(alpha = 0.2f),
            shape = RoundedCornerShape(4.dp)
          ) {
            Text(
              text = banner.discountBadge,
              color = NeonGreen,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
          }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = banner.title,
          color = Color.White,
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = banner.subtitle,
          color = Color.White.copy(alpha = 0.7f),
          fontSize = 12.sp
        )
      }
      Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = HotPink),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        modifier = Modifier.height(32.dp)
      ) {
        Text("Claim", fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

@Composable
fun PopupOfferDialog(
  banner: AdBannerEntity,
  onDismiss: () -> Unit,
  onClaim: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    GlassCard(
      cornerRadius = 24.dp,
      borderColor = CyberCyan,
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
          }
        }

        Box(
          modifier = Modifier
            .size(70.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(NeonPurple, CyberCyan))),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.LocalOffer,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(36.dp)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "🎉 EXCLUSIVE POPUP DEAL!",
          color = CyberCyan,
          fontSize = 12.sp,
          fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = banner.title,
          color = Color.White,
          fontSize = 22.sp,
          fontWeight = FontWeight.ExtraBold,
          textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = banner.subtitle,
          color = Color.White.copy(alpha = 0.8f),
          fontSize = 14.sp,
          textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
          color = DarkSurfaceVariant,
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen)
        ) {
          Text(
            text = banner.discountBadge,
            color = NeonGreen,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
          )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
          onClick = {
            onClaim()
            onDismiss()
          },
          colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
          shape = RoundedCornerShape(14.dp)
        ) {
          Text("Apply Code & Save Now", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(onClick = onDismiss) {
          Text("No thanks, I prefer paying full price", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
        }
      }
    }
  }
}
