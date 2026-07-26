package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Floating3DBackground(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
  val infiniteTransition = rememberInfiniteTransition(label = "bg_anim")
  val time by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(20000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "time"
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            DarkBackground,
            Color(0xFF100820),
            Color(0xFF07040D)
          )
        )
      )
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height

      // Glowing Neon Orbs
      val orb1X = w * 0.3f + cos(Math.toRadians(time.toDouble())).toFloat() * 120f
      val orb1Y = h * 0.2f + sin(Math.toRadians(time.toDouble())).toFloat() * 100f
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(NeonPurple.copy(alpha = 0.18f), Color.Transparent),
          center = Offset(orb1X, orb1Y),
          radius = 450f
        ),
        radius = 450f,
        center = Offset(orb1X, orb1Y)
      )

      val orb2X = w * 0.75f - cos(Math.toRadians(time * 0.8)).toFloat() * 140f
      val orb2Y = h * 0.65f + sin(Math.toRadians(time * 0.8)).toFloat() * 120f
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(CyberCyan.copy(alpha = 0.14f), Color.Transparent),
          center = Offset(orb2X, orb2Y),
          radius = 500f
        ),
        radius = 500f,
        center = Offset(orb2X, orb2Y)
      )

      // Holographic Grid Lines at Bottom
      val gridSpacing = 45f
      val yStart = h * 0.6f
      for (i in 0..12) {
        val y = yStart + (i * gridSpacing)
        val alpha = ((i / 12f) * 0.15f).coerceIn(0f, 0.15f)
        drawLine(
          color = CyberCyan.copy(alpha = alpha),
          start = Offset(0f, y),
          end = Offset(w, y),
          strokeWidth = 1f
        )
      }
    }

    // Content Layer
    content()
  }
}
