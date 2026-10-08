package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.KitComponentType
import com.example.model.KitProject
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioPanelBg
import com.example.ui.theme.StudioPanelElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun Texture2DView(
  project: KitProject,
  selectedComponent: KitComponentType,
  textureBitmap: Bitmap?,
  onExportFlatTexture: () -> Unit,
  modifier: Modifier = Modifier
) {
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
        .padding(bottom = 12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "2D Flat UV Texture Map",
          color = TextPrimary,
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp
        )
        Text(
          text = "${selectedComponent.label} Component • 2048 x 2048 Canvas",
          color = TextSecondary,
          fontSize = 11.sp
        )
      }

      Button(
        onClick = onExportFlatTexture,
        colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
        modifier = Modifier.testTag("export_flat_texture_button")
      ) {
        Icon(Icons.Default.Download, contentDescription = "Export", tint = Color(0xFF052E16), modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Export Texture PNG", color = Color(0xFF052E16), fontWeight = FontWeight.Bold, fontSize = 11.sp)
      }
    }

    // Texture Canvas Preview Box
    Box(
      modifier = Modifier
        .size(340.dp)
        .aspectRatio(1f)
        .clip(RoundedCornerShape(8.dp))
        .background(Color(0xFF0F1117))
        .border(1.5.dp, StudioBorder, RoundedCornerShape(8.dp)),
      contentAlignment = Alignment.Center
    ) {
      if (textureBitmap != null && !textureBitmap.isRecycled) {
        Image(
          bitmap = textureBitmap.asImageBitmap(),
          contentDescription = "2D Texture UV Map",
          modifier = Modifier.fillMaxSize()
        )
      } else {
        Text("Rendering texture...", color = TextSecondary, fontSize = 12.sp)
      }

      // UV Guidelines Watermark overlay
      Box(
        modifier = Modifier
          .align(Alignment.BottomStart)
          .padding(8.dp)
          .clip(RoundedCornerShape(4.dp))
          .background(Color(0x99000000))
          .padding(horizontal = 6.dp, vertical = 2.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          Icon(Icons.Default.GridOn, contentDescription = "UV Grid", tint = AccentGreen, modifier = Modifier.size(12.dp))
          Text("UV Safe Margins: Active", color = TextPrimary, fontSize = 9.sp)
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Info description
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(StudioPanelElevated)
        .border(1.dp, StudioBorder, RoundedCornerShape(8.dp))
        .padding(12.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Texture Engine Details", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        Text(
          text = "This unwarped 2048x2048 texture contains all active raster, vector and text layers in z-index order. Ready for export to FIFA Editor Tool, PES, eFootball, and modding engines.",
          color = TextSecondary,
          fontSize = 11.sp,
          lineHeight = 16.sp
        )
      }
    }
  }
}
