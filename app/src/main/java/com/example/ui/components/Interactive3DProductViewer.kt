package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

data class Point3D(val x: Float, val y: Float, val z: Float)
data class Edge3D(val start: Int, val end: Int, val color: Color = Color.Unspecified)

@Composable
fun Interactive3DProductViewer(
  modelType: String,
  productName: String,
  selectedColorHex: Color = CyberCyan,
  modifier: Modifier = Modifier
) {
  var rotationX by remember { mutableStateOf(15f) }
  var rotationY by remember { mutableStateOf(45f) }
  var zoom by remember { mutableStateOf(1f) }
  var autoRotate by remember { mutableStateOf(true) }
  var wireframeMode by remember { mutableStateOf(false) }

  val infiniteTransition = rememberInfiniteTransition(label = "3d_auto")
  val autoAngle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(12000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "angle"
  )

  val currentRotY = if (autoRotate) (rotationY + autoAngle) % 360f else rotationY

  // Get 3D Geometry based on model type
  val (vertices, edges) = remember(modelType) { getModelGeometry(modelType) }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(20.dp))
      .background(
        Brush.radialGradient(
          colors = listOf(
            selectedColorHex.copy(alpha = 0.25f),
            DarkSurface.copy(alpha = 0.9f),
            DarkBackground
          )
        )
      )
      .border(1.dp, selectedColorHex.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Header Badge
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Surface(
        color = selectedColorHex.copy(alpha = 0.2f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, selectedColorHex)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Explore,
            contentDescription = "3D Engine",
            tint = selectedColorHex,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "3D INTERACTIVE ENGINE • $modelType",
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // Auto Rotate Toggle
      IconButton(
        onClick = { autoRotate = !autoRotate },
        modifier = Modifier
          .size(36.dp)
          .background(if (autoRotate) selectedColorHex else Color.White.copy(alpha = 0.1f), CircleShape)
      ) {
        Icon(
          imageVector = Icons.Default.Autorenew,
          contentDescription = "Auto Rotate",
          tint = if (autoRotate) Color.Black else Color.White,
          modifier = Modifier.size(18.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Interactive 3D Canvas
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(260.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(Color.Black.copy(alpha = 0.4f))
        .pointerInput(Unit) {
          detectDragGestures(
            onDragStart = { autoRotate = false }
          ) { _, dragAmount ->
            rotationY += dragAmount.x * 0.8f
            rotationX = (rotationX - dragAmount.y * 0.8f).coerceIn(-80f, 80f)
          }
        }
        .pointerInput(Unit) {
          detectTransformGestures { _, _, zoomChange, _ ->
            autoRotate = false
            zoom = (zoom * zoomChange).coerceIn(0.6f, 2.2f)
          }
        },
      contentAlignment = Alignment.Center
    ) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val scale = (size.minDimension / 3.2f) * zoom

        val radX = Math.toRadians(rotationX.toDouble())
        val radY = Math.toRadians(currentRotY.toDouble())

        val cosX = cos(radX).toFloat()
        val sinX = sin(radX).toFloat()
        val cosY = cos(radY).toFloat()
        val sinY = sin(radY).toFloat()

        // Project 3D vertices to 2D screen coordinates
        val projected = vertices.map { pt ->
          // Rotate around Y
          val x1 = pt.x * cosY + pt.z * sinY
          val y1 = pt.y
          val z1 = -pt.x * sinY + pt.z * cosY

          // Rotate around X
          val x2 = x1
          val y2 = y1 * cosX - z1 * sinX
          val z2 = y1 * sinX + z1 * cosX

          // Perspective projection
          val distance = 4f
          val fov = 1f / (distance - z2 / 2f)
          val screenX = centerX + x2 * scale * fov * 3f
          val screenY = centerY + y2 * scale * fov * 3f
          val depth = z2

          Triple(Offset(screenX, screenY), depth, Offset(x2, y2))
        }

        // Draw Shadow floor
        drawOval(
          color = selectedColorHex.copy(alpha = 0.15f),
          topLeft = Offset(centerX - scale * 0.8f, centerY + scale * 0.8f),
          size = androidx.compose.ui.geometry.Size(scale * 1.6f, scale * 0.3f)
        )

        // Sort edges by average depth of their vertices so closer lines draw on top
        val sortedEdges = edges.sortedBy { edge ->
          -(projected[edge.start].second + projected[edge.end].second) / 2f
        }

        for (edge in sortedEdges) {
          val p1 = projected[edge.start]
          val p2 = projected[edge.end]
          val depthAvg = (p1.second + p2.second) / 2f

          // Depth based alpha and thickness
          val alpha = ((1.2f + depthAvg) / 2.5f).coerceIn(0.2f, 1f)
          val strokeWidth = ((1.8f + depthAvg * 0.8f) * zoom).coerceIn(1f, 5f)

          val edgeColor = if (edge.color != Color.Unspecified) edge.color else selectedColorHex

          drawLine(
            color = edgeColor.copy(alpha = alpha),
            start = p1.first,
            end = p2.first,
            strokeWidth = strokeWidth
          )
        }

        // Draw vertex glowing nodes
        for (pt in projected) {
          val alpha = ((1.2f + pt.second) / 2.5f).coerceIn(0.3f, 1f)
          drawCircle(
            color = Color.White.copy(alpha = alpha),
            radius = 3f * zoom,
            center = pt.first
          )
        }
      }

      // Gesture Tip Overlay
      Text(
        text = "🖐 Drag to Spin 360° • Pinch to Zoom",
        color = Color.White.copy(alpha = 0.7f),
        fontSize = 11.sp,
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .padding(bottom = 8.dp)
          .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
          .padding(horizontal = 10.dp, vertical = 4.dp)
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Control bar
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = { zoom = (zoom - 0.2f).coerceAtLeast(0.6f) },
          modifier = Modifier.size(32.dp).background(Color.White.copy(alpha = 0.1f), CircleShape)
        ) {
          Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out", tint = Color.White, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "${(zoom * 100).toInt()}%", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
          onClick = { zoom = (zoom + 0.2f).coerceAtMost(2.2f) },
          modifier = Modifier.size(32.dp).background(Color.White.copy(alpha = 0.1f), CircleShape)
        ) {
          Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In", tint = Color.White, modifier = Modifier.size(16.dp))
        }
      }

      Button(
        onClick = {
          rotationX = 15f
          rotationY = 45f
          zoom = 1f
          autoRotate = true
        },
        colors = ButtonDefaults.buttonColors(containerColor = selectedColorHex.copy(alpha = 0.2f)),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        modifier = Modifier.height(32.dp)
      ) {
        Text("Reset View", color = selectedColorHex, fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

// Generates 3D coordinates and edges for different product categories
fun getModelGeometry(type: String): Pair<List<Point3D>, List<Edge3D>> {
  return when (type.uppercase()) {
    "PHONE" -> {
      // 3D Smartphone Box with Camera Bump
      val w = 0.55f; val h = 1.1f; val d = 0.12f
      val v = listOf(
        Point3D(-w, -h, -d), Point3D(w, -h, -d), Point3D(w, h, -d), Point3D(-w, h, -d), // Front
        Point3D(-w, -h, d), Point3D(w, -h, d), Point3D(w, h, d), Point3D(-w, h, d),    // Back
        // Camera Bump on top-left back
        Point3D(-w*0.8f, -h*0.8f, d*1.6f), Point3D(-w*0.2f, -h*0.8f, d*1.6f),
        Point3D(-w*0.2f, -h*0.4f, d*1.6f), Point3D(-w*0.8f, -h*0.4f, d*1.6f)
      )
      val e = listOf(
        // Front face
        Edge3D(0,1), Edge3D(1,2), Edge3D(2,3), Edge3D(3,0),
        // Back face
        Edge3D(4,5), Edge3D(5,6), Edge3D(6,7), Edge3D(7,4),
        // Connecting struts
        Edge3D(0,4), Edge3D(1,5), Edge3D(2,6), Edge3D(3,7),
        // Camera bump
        Edge3D(8,9, HotPink), Edge3D(9,10, HotPink), Edge3D(10,11, HotPink), Edge3D(11,8, HotPink),
        Edge3D(8,4, HotPink), Edge3D(10,4, HotPink)
      )
      Pair(v, e)
    }
    "LAPTOP" -> {
      // Laptop keyboard deck + open screen
      val v = listOf(
        // Base deck (Y is horizontal plane)
        Point3D(-1f, 0.5f, -0.7f), Point3D(1f, 0.5f, -0.7f), Point3D(1f, 0.5f, 0.7f), Point3D(-1f, 0.5f, 0.7f),
        // Screen open upwards at angle
        Point3D(-1f, -0.6f, -0.9f), Point3D(1f, -0.6f, -0.9f)
      )
      val e = listOf(
        Edge3D(0,1), Edge3D(1,2), Edge3D(2,3), Edge3D(3,0), // Keyboard deck
        Edge3D(0,4, CyberCyan), Edge3D(1,5, CyberCyan), Edge3D(4,5, CyberCyan), // Screen frame
        Edge3D(2,0, Color.White.copy(alpha=0.3f)), Edge3D(3,1, Color.White.copy(alpha=0.3f)) // Keyboard cross
      )
      Pair(v, e)
    }
    "DRONE" -> {
      // Quadcopter Drone body + 4 rotors
      val v = listOf(
        Point3D(0f, 0f, 0f), // Center
        Point3D(-0.8f, 0f, -0.8f), Point3D(0.8f, 0f, -0.8f), // Front rotors
        Point3D(0.8f, 0f, 0.8f), Point3D(-0.8f, 0f, 0.8f),   // Rear rotors
        Point3D(0f, -0.3f, -0.3f), // Camera gimbal
        Point3D(-0.4f, 0.3f, 0f), Point3D(0.4f, 0.3f, 0f)    // Landing gear
      )
      val e = listOf(
        Edge3D(0,1), Edge3D(0,2), Edge3D(0,3), Edge3D(0,4), // Arms
        Edge3D(1,2, NeonGreen), Edge3D(2,3, NeonGreen), Edge3D(3,4, NeonGreen), Edge3D(4,1, NeonGreen), // Rotor ring
        Edge3D(0,5, HotPink), // Gimbal
        Edge3D(0,6), Edge3D(0,7) // Gear
      )
      Pair(v, e)
    }
    "CAMERA" -> {
      // DSLR Body + Lens cylinder
      val v = listOf(
        Point3D(-0.8f, -0.5f, -0.4f), Point3D(0.8f, -0.5f, -0.4f), Point3D(0.8f, 0.5f, -0.4f), Point3D(-0.8f, 0.5f, -0.4f), // Back
        Point3D(-0.8f, -0.5f, 0.2f), Point3D(0.8f, -0.5f, 0.2f), Point3D(0.8f, 0.5f, 0.2f), Point3D(-0.8f, 0.5f, 0.2f),    // Front body
        // Lens cylinder extending out
        Point3D(-0.4f, -0.3f, 0.9f), Point3D(0.4f, -0.3f, 0.9f), Point3D(0.4f, 0.3f, 0.9f), Point3D(-0.4f, 0.3f, 0.9f)
      )
      val e = listOf(
        Edge3D(0,1), Edge3D(1,2), Edge3D(2,3), Edge3D(3,0), // Body back
        Edge3D(4,5), Edge3D(5,6), Edge3D(6,7), Edge3D(7,4), // Body front
        Edge3D(0,4), Edge3D(1,5), Edge3D(2,6), Edge3D(3,7), // Struts
        Edge3D(8,9, CyberCyan), Edge3D(9,10, CyberCyan), Edge3D(10,11, CyberCyan), Edge3D(11,8, CyberCyan), // Lens tip
        Edge3D(4,8), Edge3D(5,9), Edge3D(6,10), Edge3D(7,11) // Lens barrel
      )
      Pair(v, e)
    }
    else -> {
      // Default Stylized Tech Cube / Hexagon Structure
      val s = 0.7f
      val v = listOf(
        Point3D(-s, -s, -s), Point3D(s, -s, -s), Point3D(s, s, -s), Point3D(-s, s, -s),
        Point3D(-s, -s, s), Point3D(s, -s, s), Point3D(s, s, s), Point3D(-s, s, s)
      )
      val e = listOf(
        Edge3D(0,1), Edge3D(1,2), Edge3D(2,3), Edge3D(3,0),
        Edge3D(4,5), Edge3D(5,6), Edge3D(6,7), Edge3D(7,4),
        Edge3D(0,4), Edge3D(1,5), Edge3D(2,6), Edge3D(3,7),
        Edge3D(0,6, HotPink), Edge3D(1,7, CyberCyan)
      )
      Pair(v, e)
    }
  }
}
