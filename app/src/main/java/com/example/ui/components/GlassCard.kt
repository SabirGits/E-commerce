package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

@Composable
fun GlassCard(
  modifier: Modifier = Modifier,
  cornerRadius: Dp = 16.dp,
  borderWidth: Dp = 1.dp,
  borderColor: Color = GlassBorderDark,
  backgroundColor: Color = DarkSurface.copy(alpha = 0.7f),
  onClick: (() -> Unit)? = null,
  content: @Composable BoxScope.() -> Unit
) {
  var mod = modifier
    .shadow(elevation = 8.dp, shape = RoundedCornerShape(cornerRadius), ambientColor = NeonPurple, spotColor = CyberCyan)
    .clip(RoundedCornerShape(cornerRadius))
    .background(
      Brush.linearGradient(
        colors = listOf(
          backgroundColor,
          DarkSurfaceVariant.copy(alpha = 0.6f)
        )
      )
    )
    .border(BorderStroke(borderWidth, borderColor), RoundedCornerShape(cornerRadius))

  if (onClick != null) {
    mod = mod.clickable { onClick() }
  }

  Box(modifier = mod, content = content)
}
