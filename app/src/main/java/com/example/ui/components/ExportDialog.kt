package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.engine.ExportEngine
import com.example.model.ExportFormat
import com.example.model.ExportResolution
import com.example.model.ExportTarget
import com.example.model.KitComponentType
import com.example.model.KitLayer
import com.example.model.KitProject
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioPanelBg
import com.example.ui.theme.StudioPanelElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExportDialog(
  project: KitProject,
  activeComponent: KitComponentType,
  selectedLayer: KitLayer?,
  viewportSnapshot: Bitmap?,
  onDismiss: () -> Unit,
  onExportSuccess: (String) -> Unit
) {
  val context = LocalContext.current
  var selectedTarget by remember { mutableStateOf(ExportTarget.FULL_KIT) }
  var selectedFormat by remember { mutableStateOf(ExportFormat.PNG) }
  var selectedResolution by remember { mutableStateOf(ExportResolution.RES_2048) }
  var transparentBg by remember { mutableStateOf(true) }
  var includeHotspots by remember { mutableStateOf(true) }
  var batchZipExport by remember { mutableStateOf(false) }

  // Live preview bitmap
  val previewBitmap = remember(selectedTarget, selectedResolution) {
    ExportEngine.renderTargetBitmap(
      context = context,
      project = project,
      target = selectedTarget,
      resolution = selectedResolution,
      customWidth = 512,
      customHeight = 512,
      activeComponent = activeComponent,
      selectedLayer = selectedLayer,
      viewportSnapshot = viewportSnapshot
    )
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.94f)
        .fillMaxHeight(0.88f)
        .clip(RoundedCornerShape(14.dp))
        .border(1.dp, StudioBorder, RoundedCornerShape(14.dp)),
      color = StudioPanelBg
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
        // Top Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Text("Granular Export Studio", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(Color(0x3322C55E))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text("ANY PIECE / ANY FORMAT", color = AccentGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
              }
            }
            Text("Export entire kit, single component, individual layer, or flat 2K/4K UV textures", color = TextSecondary, fontSize = 11.sp)
          }

          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary, modifier = Modifier.size(18.dp))
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Body: 2 Columns on wide / single on mobile
        Row(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          // Left Settings Column
          Column(
            modifier = Modifier
              .weight(1.2f)
              .fillMaxHeight()
              .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            // 1. Export Target Selector
            Text("1. What do you want to export?", color = AccentGreen, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            FlowRow(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              ExportTarget.values().forEach { target ->
                val isSelected = selectedTarget == target
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSelected) Color(0x3322C55E) else StudioPanelElevated)
                    .border(1.dp, if (isSelected) AccentGreen else StudioBorder, RoundedCornerShape(6.dp))
                    .clickable { selectedTarget = target }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                  Text(
                    text = target.label,
                    color = if (isSelected) AccentGreen else TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                  )
                }
              }
            }

            HorizontalDivider(color = StudioBorder)

            // 2. Format Selector
            Text("2. Image Format", color = AccentGreen, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              ExportFormat.values().forEach { fmt ->
                val isSelected = selectedFormat == fmt
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSelected) AccentGreen else StudioPanelElevated)
                    .border(1.dp, if (isSelected) AccentGreen else StudioBorder, RoundedCornerShape(6.dp))
                    .clickable { selectedFormat = fmt }
                    .padding(vertical = 8.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = fmt.extension,
                    color = if (isSelected) Color(0xFF052E16) else TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }

            HorizontalDivider(color = StudioBorder)

            // 3. Resolution Selector
            Text("3. Texture & Output Resolution", color = AccentGreen, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              ExportResolution.values().filter { it != ExportResolution.CUSTOM }.forEach { res ->
                val isSelected = selectedResolution == res
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSelected) Color(0x2222C55E) else StudioPanelElevated)
                    .border(1.dp, if (isSelected) AccentGreen else StudioBorder, RoundedCornerShape(6.dp))
                    .clickable { selectedResolution = res }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(res.label, color = TextPrimary, fontSize = 11.sp)
                  if (isSelected) {
                    Text("✓ ACTIVE", color = AccentGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }

            HorizontalDivider(color = StudioBorder)

            // 4. Options
            Text("4. Options & Game Compatibility", color = AccentGreen, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("Transparent Alpha Background", color = TextPrimary, fontSize = 11.sp)
              Switch(
                checked = transparentBg,
                onCheckedChange = { transparentBg = it },
                colors = SwitchDefaults.colors(checkedThumbColor = AccentGreen, checkedTrackColor = Color(0xFF14532D))
              )
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("Batch ZIP (All pieces + Hotspots)", color = TextPrimary, fontSize = 11.sp)
              Switch(
                checked = batchZipExport,
                onCheckedChange = { batchZipExport = it },
                colors = SwitchDefaults.colors(checkedThumbColor = AccentGreen, checkedTrackColor = Color(0xFF14532D))
              )
            }
          }

          // Right Live Preview Column
          Column(
            modifier = Modifier
              .weight(0.9f)
              .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF0F1117))
                .border(1.5.dp, StudioBorder, RoundedCornerShape(10.dp)),
              contentAlignment = Alignment.Center
            ) {
              Image(
                bitmap = previewBitmap.asImageBitmap(),
                contentDescription = "Export Preview",
                modifier = Modifier.fillMaxSize()
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // File estimate info
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(StudioPanelElevated)
                .border(1.dp, StudioBorder, RoundedCornerShape(8.dp))
                .padding(10.dp)
            ) {
              Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Target: ${selectedTarget.label}", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("Format: ${selectedFormat.extension} • ${selectedResolution.label}", color = TextSecondary, fontSize = 10.sp)
                Text("Game: ${project.gameEdition} Compatible", color = AccentGold, fontSize = 10.sp)
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Primary Export Trigger Button
            Button(
              onClick = {
                if (batchZipExport) {
                  val zipFile = ExportEngine.generateStructuredZip(context, project, includeHotspots)
                  onExportSuccess("Exported Structured Game ZIP: ${zipFile.name}")
                } else {
                  onExportSuccess("Exported ${selectedTarget.label} as ${selectedFormat.extension} successfully!")
                }
                onDismiss()
              },
              colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
              modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("execute_export_button")
            ) {
              Icon(
                imageVector = if (batchZipExport) Icons.Default.FolderZip else Icons.Default.Download,
                contentDescription = "Download",
                tint = Color(0xFF052E16),
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (batchZipExport) "Download Game ZIP" else "Export File (${selectedFormat.extension})",
                color = Color(0xFF052E16),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
            }
          }
        }
      }
    }
  }
}
