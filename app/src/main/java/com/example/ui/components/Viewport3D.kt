package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.Kit3DRenderer
import com.example.model.KitComponentType
import com.example.model.KitProject
import com.example.model.LightingPreset
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioPanelBg
import com.example.ui.theme.StudioPanelElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.roundToInt

@Composable
fun Viewport3D(
  project: KitProject,
  selectedComponent: KitComponentType,
  shirtBitmap: Bitmap?,
  shortsBitmap: Bitmap?,
  socksBitmap: Bitmap?,
  yaw: Float,
  pitch: Float,
  zoom: Float,
  isWireframe: Boolean,
  isAutoRotating: Boolean,
  onOrbit: (deltaYaw: Float, deltaPitch: Float) -> Unit,
  onZoom: (factor: Float) -> Unit,
  onSetCameraAngle: (yaw: Float, pitch: Float) -> Unit,
  onResetCamera: () -> Unit,
  onToggleWireframe: () -> Unit,
  onToggleAutoRotate: () -> Unit,
  onSelectComponent: (KitComponentType) -> Unit,
  onExportPng: () -> Unit,
  onExportTexture: () -> Unit,
  onMinikitClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val renderer = remember { Kit3DRenderer() }

  // Auto rotation animation
  val infiniteTransition = rememberInfiniteTransition(label = "auto_rotate")
  val autoYaw by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 12000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "yaw_spin"
  )

  val effectiveYaw = if (isAutoRotating) (yaw + autoYaw) % 360f else yaw

  // Lighting dropdown
  var lightingMenuOpen by remember { mutableStateOf(false) }
  var cameraMenuOpen by remember { mutableStateOf(false) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.radialGradient(
          colors = listOf(Color(0xFF232733), Color(0xFF12141A)),
          radius = 1200f
        )
      )
  ) {
    // 3D Canvas
    Canvas(
      modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
          detectTransformGestures { _, pan, gestureZoom, _ ->
            if (gestureZoom != 1f) {
              onZoom(gestureZoom)
            }
            if (pan.x != 0f || pan.y != 0f) {
              onOrbit(pan.x * 0.4f, pan.y * 0.4f)
            }
          }
        }
        .testTag("viewport_3d_canvas")
    ) {
      renderer.render(
        drawScope = this,
        project = project,
        shirtBitmap = shirtBitmap,
        shortsBitmap = shortsBitmap,
        socksBitmap = socksBitmap,
        yawDegrees = effectiveYaw,
        pitchDegrees = pitch,
        zoom = zoom,
        showWireframe = isWireframe,
        lightingPreset = project.lightingPreset,
        modelFit = project.modelFit,
        collarTemplate = project.collarTemplate,
        sportType = project.sport
      )
    }

    // Top-Center Floating Toolbar: Component Tabs
    Box(
      modifier = Modifier
        .align(Alignment.TopCenter)
        .padding(top = 10.dp)
        .clip(RoundedCornerShape(20.dp))
        .background(Color(0xCC1A1C24))
        .border(1.dp, StudioBorder, RoundedCornerShape(20.dp))
        .padding(horizontal = 6.dp, vertical = 4.dp)
        .testTag("floating_component_toolbar")
    ) {
      Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        KitComponentType.values().forEach { comp ->
          val isSelected = selectedComponent == comp
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .background(if (isSelected) AccentGreen else Color.Transparent)
              .clickable { onSelectComponent(comp) }
              .padding(horizontal = 10.dp, vertical = 5.dp)
          ) {
            Text(
              text = comp.label,
              color = if (isSelected) Color(0xFF052E16) else TextPrimary,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              fontSize = 11.sp
            )
          }
        }
      }
    }

    // Top-Right Floating Controls
    Column(
      modifier = Modifier
        .align(Alignment.TopEnd)
        .padding(top = 10.dp, end = 10.dp),
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      // 3D Turntable Spin
      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(CircleShape)
          .background(if (isAutoRotating) AccentGreen else Color(0xCC1A1C24))
          .border(1.dp, StudioBorder, CircleShape)
          .clickable(onClick = onToggleAutoRotate),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.RotateRight,
          contentDescription = "Auto Rotate 360",
          tint = if (isAutoRotating) Color(0xFF052E16) else TextPrimary,
          modifier = Modifier.size(18.dp)
        )
      }

      // Wireframe Toggle
      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(CircleShape)
          .background(if (isWireframe) AccentGold else Color(0xCC1A1C24))
          .border(1.dp, StudioBorder, CircleShape)
          .clickable(onClick = onToggleWireframe),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.GridView,
          contentDescription = "Toggle Wireframe",
          tint = if (isWireframe) Color(0xFF422006) else TextPrimary,
          modifier = Modifier.size(16.dp)
        )
      }

      // Lighting Presets Dropdown
      Box {
        Box(
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(Color(0xCC1A1C24))
            .border(1.dp, StudioBorder, CircleShape)
            .clickable { lightingMenuOpen = true },
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.LightMode,
            contentDescription = "Lighting Presets",
            tint = TextPrimary,
            modifier = Modifier.size(16.dp)
          )
        }

        DropdownMenu(
          expanded = lightingMenuOpen,
          onDismissRequest = { lightingMenuOpen = false },
          modifier = Modifier.background(StudioPanelElevated)
        ) {
          LightingPreset.values().forEach { lp ->
            DropdownMenuItem(
              text = { Text(lp.label, color = TextPrimary, fontSize = 11.sp) },
              onClick = {
                lightingMenuOpen = false
              }
            )
          }
        }
      }

      // Camera Angle Presets (Front, Back, Side, 3/4)
      Box {
        Box(
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(Color(0xCC1A1C24))
            .border(1.dp, StudioBorder, CircleShape)
            .clickable { cameraMenuOpen = true },
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.PhotoCamera,
            contentDescription = "Camera Angles",
            tint = TextPrimary,
            modifier = Modifier.size(16.dp)
          )
        }

        DropdownMenu(
          expanded = cameraMenuOpen,
          onDismissRequest = { cameraMenuOpen = false },
          modifier = Modifier.background(StudioPanelElevated)
        ) {
          DropdownMenuItem(
            text = { Text("Front (0°)", color = TextPrimary, fontSize = 11.sp) },
            onClick = { onSetCameraAngle(0f, 0f); cameraMenuOpen = false }
          )
          DropdownMenuItem(
            text = { Text("Back (180°)", color = TextPrimary, fontSize = 11.sp) },
            onClick = { onSetCameraAngle(180f, 0f); cameraMenuOpen = false }
          )
          DropdownMenuItem(
            text = { Text("Left Side (90°)", color = TextPrimary, fontSize = 11.sp) },
            onClick = { onSetCameraAngle(90f, 0f); cameraMenuOpen = false }
          )
          DropdownMenuItem(
            text = { Text("Right Side (-90°)", color = TextPrimary, fontSize = 11.sp) },
            onClick = { onSetCameraAngle(-90f, 0f); cameraMenuOpen = false }
          )
          DropdownMenuItem(
            text = { Text("3/4 Isometric (45°)", color = TextPrimary, fontSize = 11.sp) },
            onClick = { onSetCameraAngle(45f, 15f); cameraMenuOpen = false }
          )
          DropdownMenuItem(
            text = { Text("Reset View", color = AccentGreen, fontSize = 11.sp) },
            onClick = { onResetCamera(); cameraMenuOpen = false }
          )
        }
      }

      // Reset View Button
      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(CircleShape)
          .background(Color(0xCC1A1C24))
          .border(1.dp, StudioBorder, CircleShape)
          .clickable(onClick = onResetCamera),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.RestartAlt,
          contentDescription = "Reset Camera",
          tint = TextSecondary,
          modifier = Modifier.size(16.dp)
        )
      }
    }

    // Bottom-Left Info Overlay
    Box(
      modifier = Modifier
        .align(Alignment.BottomStart)
        .padding(start = 12.dp, bottom = 10.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(Color(0x9912141A))
        .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
      Text(
        text = "Yaw: ${effectiveYaw.roundToInt()}° • Pitch: ${pitch.roundToInt()}° • Zoom: ${(zoom * 100).roundToInt()}%",
        color = TextSecondary,
        fontSize = 10.sp
      )
    }

    // Bottom-Right Floating Action Pills (Export, Texture, Minikit)
    Row(
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(end = 12.dp, bottom = 10.dp),
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Free Plus Badge
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0x3322C55E))
          .border(1.dp, AccentGreen, RoundedCornerShape(12.dp))
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Text("✨ All Features Free", color = AccentGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
      }

      // 2K Texture Pill
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(StudioPanelElevated)
          .border(1.dp, StudioBorder, RoundedCornerShape(12.dp))
          .clickable(onClick = onExportTexture)
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Text("2K UV Texture", color = TextPrimary, fontSize = 10.sp)
      }

      // Minikit Card Pill
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(StudioPanelElevated)
          .border(1.dp, StudioBorder, RoundedCornerShape(12.dp))
          .clickable(onClick = onMinikitClick)
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Text("FUT Minikit", color = TextPrimary, fontSize = 10.sp)
      }

      // Export PNG Pill
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(AccentGreen)
          .clickable(onClick = onExportPng)
          .padding(horizontal = 10.dp, vertical = 4.dp)
          .testTag("floating_export_pill")
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(Icons.Default.Download, contentDescription = "Export PNG", tint = Color(0xFF052E16), modifier = Modifier.size(12.dp))
          Text("Download PNG", color = Color(0xFF052E16), fontWeight = FontWeight.Bold, fontSize = 10.sp)
        }
      }
    }
  }
}
