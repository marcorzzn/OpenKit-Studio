package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.KitProject
import com.example.model.LayerType
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioPanelBg
import com.example.ui.theme.StudioPanelElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MinikitView(
  project: KitProject,
  onExportMinikit: () -> Unit,
  modifier: Modifier = Modifier
) {
  val baseLayer = project.shirtLayers.firstOrNull { it.type == LayerType.BASE_DESIGN }
  val primaryColor = baseLayer?.primaryColorObj ?: Color(0xFF14171E)
  val patternLayer = project.shirtLayers.firstOrNull { it.type == LayerType.PATTERN }
  val secondaryColor = patternLayer?.secondaryColorObj ?: Color(0xFF22C55E)

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(StudioPanelBg)
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    // Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text("FUT 2D Minikit Generator", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Text("Official 2D Card Icon for EA Sports FC Squad View", color = TextSecondary, fontSize = 11.sp)
      }

      Button(
        onClick = onExportMinikit,
        colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
        modifier = Modifier.testTag("export_minikit_button")
      ) {
        Icon(Icons.Default.Download, contentDescription = "Export Minikit", tint = Color(0xFF052E16), modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Export Minikit", color = Color(0xFF052E16), fontWeight = FontWeight.Bold, fontSize = 11.sp)
      }
    }

    // FUT Minikit Card Container
    Box(
      modifier = Modifier
        .width(260.dp)
        .height(360.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(
          Brush.verticalGradient(
            colors = listOf(Color(0xFF2E2413), Color(0xFF151410), Color(0xFF0A0A0A))
          )
        )
        .border(2.dp, AccentGold, RoundedCornerShape(16.dp))
        .padding(16.dp),
      contentAlignment = Alignment.TopCenter
    ) {
      Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Card Top Badge & Edition
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("KIT", color = AccentGold, fontWeight = FontWeight.Black, fontSize = 18.sp)
            Text("99", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 22.sp)
          }

          Icon(Icons.Default.Shield, contentDescription = "Crest", tint = AccentGold, modifier = Modifier.size(28.dp))
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2D Vector Jersey Silhouette with Dynamic Colors
        Canvas(modifier = Modifier.size(160.dp, 160.dp)) {
          val w = size.width
          val h = size.height

          // Shirt path
          val shirtPath = Path().apply {
            moveTo(w * 0.35f, h * 0.15f)
            cubicTo(w * 0.42f, h * 0.22f, w * 0.58f, h * 0.22f, w * 0.65f, h * 0.15f)
            lineTo(w * 0.85f, h * 0.25f)
            lineTo(w * 0.98f, h * 0.48f)
            lineTo(w * 0.86f, h * 0.58f)
            lineTo(w * 0.78f, h * 0.48f)
            lineTo(w * 0.78f, h * 0.92f)
            lineTo(w * 0.22f, h * 0.92f)
            lineTo(w * 0.22f, h * 0.48f)
            lineTo(w * 0.14f, h * 0.58f)
            lineTo(w * 0.02f, h * 0.48f)
            lineTo(w * 0.15f, h * 0.25f)
            close()
          }

          drawPath(path = shirtPath, color = primaryColor)

          // Accent stripes or patterns
          val stripeWidth = w * 0.1f
          drawRect(
            color = secondaryColor,
            topLeft = androidx.compose.ui.geometry.Offset(w * 0.45f, h * 0.22f),
            size = androidx.compose.ui.geometry.Size(stripeWidth, h * 0.70f)
          )

          // Collar Trim
          val collarPath = Path().apply {
            moveTo(w * 0.35f, h * 0.15f)
            cubicTo(w * 0.42f, h * 0.26f, w * 0.58f, h * 0.26f, w * 0.65f, h * 0.15f)
            lineTo(w * 0.60f, h * 0.15f)
            cubicTo(w * 0.55f, h * 0.22f, w * 0.45f, h * 0.22f, w * 0.40f, h * 0.15f)
            close()
          }
          drawPath(path = collarPath, color = Color(project.collarColor))
        }

        Spacer(modifier = Modifier.weight(1f))

        // Card Bottom: Project Name & Team
        Text(
          text = project.name.uppercase(),
          color = TextPrimary,
          fontWeight = FontWeight.Black,
          fontSize = 12.sp,
          maxLines = 1
        )
        Text(
          text = project.teamName,
          color = AccentGold,
          fontSize = 10.sp,
          fontWeight = FontWeight.Medium
        )
      }
    }
  }
}
