package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CameraPreset
import com.example.model.PresetsData
import com.example.model.SportType
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioPanelBg
import com.example.ui.theme.StudioPanelElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.StudioTab

@Composable
fun TopNavBar(
  projectName: String,
  currentSport: SportType,
  activeTab: StudioTab,
  canUndo: Boolean,
  canRedo: Boolean,
  onSelectSport: (SportType) -> Unit,
  onTabSelected: (StudioTab) -> Unit,
  onUndo: () -> Unit,
  onRedo: () -> Unit,
  onRandomize: () -> Unit,
  onSave: () -> Unit,
  onExport: () -> Unit,
  onLoadPreset: (String) -> Unit,
  onSelectCameraPreset: (CameraPreset) -> Unit
) {
  var presetsMenuExpanded by remember { mutableStateOf(false) }
  var sportMenuExpanded by remember { mutableStateOf(false) }
  var cameraMenuExpanded by remember { mutableStateOf(false) }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(StudioPanelBg)
      .border(1.dp, StudioBorder)
  ) {
    // Upper row: Branding, Sport Switcher, Undo/Redo, Presets & Export
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .padding(horizontal = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Left: Logo, Sport Switcher & Project Name
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Sport Switcher Dropdown (Modulo 6)
        Box {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(AccentGreen)
              .clickable { sportMenuExpanded = true }
              .padding(horizontal = 6.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(currentSport.icon, fontSize = 14.sp)
              Text("▼", color = Color(0xFF052E16), fontSize = 8.sp)
            }
          }

          DropdownMenu(
            expanded = sportMenuExpanded,
            onDismissRequest = { sportMenuExpanded = false },
            modifier = Modifier.background(StudioPanelElevated)
          ) {
            SportType.values().forEach { sport ->
              DropdownMenuItem(
                text = { Text("${sport.icon} ${sport.label}", color = TextPrimary, fontSize = 12.sp) },
                onClick = {
                  onSelectSport(sport)
                  sportMenuExpanded = false
                }
              )
            }
          }
        }

        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "OpenKit",
              color = TextPrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Studio",
              color = AccentGreen,
              fontWeight = FontWeight.SemiBold,
              fontSize = 15.sp
            )
          }
          Text(
            text = projectName,
            color = TextSecondary,
            fontSize = 10.sp,
            maxLines = 1
          )
        }

        Spacer(modifier = Modifier.width(4.dp))

        // Undo / Redo
        IconButton(
          onClick = onUndo,
          enabled = canUndo,
          modifier = Modifier
            .size(32.dp)
            .testTag("undo_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Undo,
            contentDescription = "Undo",
            tint = if (canUndo) TextPrimary else TextSecondary.copy(alpha = 0.4f),
            modifier = Modifier.size(16.dp)
          )
        }

        IconButton(
          onClick = onRedo,
          enabled = canRedo,
          modifier = Modifier
            .size(32.dp)
            .testTag("redo_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Redo,
            contentDescription = "Redo",
            tint = if (canRedo) TextPrimary else TextSecondary.copy(alpha = 0.4f),
            modifier = Modifier.size(16.dp)
          )
        }
      }

      // Right: Camera Presets, Templates, Randomize, Export
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        // Camera Presets Dropdown (Modulo 3)
        Box {
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(RoundedCornerShape(6.dp))
              .background(StudioPanelElevated)
              .border(1.dp, StudioBorder, RoundedCornerShape(6.dp))
              .clickable { cameraMenuExpanded = true },
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.CameraAlt, contentDescription = "Camera Angle", tint = TextPrimary, modifier = Modifier.size(16.dp))
          }

          DropdownMenu(
            expanded = cameraMenuExpanded,
            onDismissRequest = { cameraMenuExpanded = false },
            modifier = Modifier.background(StudioPanelElevated)
          ) {
            CameraPreset.values().forEach { cp ->
              DropdownMenuItem(
                text = { Text(cp.label, color = TextPrimary, fontSize = 11.sp) },
                onClick = {
                  onSelectCameraPreset(cp)
                  cameraMenuExpanded = false
                }
              )
            }
          }
        }

        // Templates Button
        Box {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(StudioPanelElevated)
              .border(1.dp, StudioBorder, RoundedCornerShape(6.dp))
              .clickable { presetsMenuExpanded = true }
              .padding(horizontal = 8.dp, vertical = 6.dp)
              .testTag("presets_dropdown_button")
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              Icon(Icons.Default.Layers, contentDescription = "Kits", tint = AccentGreen, modifier = Modifier.size(14.dp))
              Text("Kits", color = TextPrimary, fontSize = 11.sp)
            }
          }

          DropdownMenu(
            expanded = presetsMenuExpanded,
            onDismissRequest = { presetsMenuExpanded = false },
            modifier = Modifier.background(StudioPanelElevated)
          ) {
            PresetsData.getAllPresets().forEach { (name, _) ->
              DropdownMenuItem(
                text = { Text(name, color = TextPrimary, fontSize = 12.sp) },
                onClick = {
                  onLoadPreset(name)
                  presetsMenuExpanded = false
                }
              )
            }
          }
        }

        // Randomize
        IconButton(
          onClick = onRandomize,
          modifier = Modifier.size(32.dp).testTag("randomize_button")
        ) {
          Icon(Icons.Default.Casino, contentDescription = "Randomize", tint = TextSecondary, modifier = Modifier.size(18.dp))
        }

        // Save
        IconButton(
          onClick = onSave,
          modifier = Modifier.size(32.dp).testTag("save_project_button")
        ) {
          Icon(Icons.Default.Save, contentDescription = "Save", tint = TextSecondary, modifier = Modifier.size(18.dp))
        }

        // Export (Granular Studio Trigger)
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(AccentGreen)
            .clickable(onClick = onExport)
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("export_button")
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(Icons.Default.Download, contentDescription = "Export", tint = Color(0xFF052E16), modifier = Modifier.size(14.dp))
            Text("Export", color = Color(0xFF052E16), fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }
        }
      }
    }

    // Lower row: Navigation Tabs (3D View, 2D Texture, FUT Minikit, Kit Vision AI)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(38.dp)
        .horizontalScroll(rememberScrollState())
        .padding(horizontal = 12.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      StudioTab.values().forEach { tab ->
        val isActive = activeTab == tab
        Column(
          modifier = Modifier
            .clickable { onTabSelected(tab) }
            .padding(vertical = 4.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = tab.label,
            color = if (isActive) AccentGreen else TextSecondary,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            fontSize = 12.sp
          )
          Spacer(modifier = Modifier.height(4.dp))
          if (isActive) {
            Box(
              modifier = Modifier
                .height(2.dp)
                .fillMaxWidth()
                .background(AccentGreen)
            )
          }
        }
      }
    }
  }
}
