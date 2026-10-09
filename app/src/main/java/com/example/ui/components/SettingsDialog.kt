package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.model.CollarTemplate
import com.example.model.KitProject
import com.example.model.LightingPreset
import com.example.model.ModelFit
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioPanelBg
import com.example.ui.theme.StudioPanelElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.roundToInt

@Composable
fun SettingsDialog(
  project: KitProject,
  onUpdateProject: ((KitProject) -> KitProject) -> Unit,
  onDismiss: () -> Unit
) {
  var nameInput by remember { mutableStateOf(project.name) }
  var teamInput by remember { mutableStateOf(project.teamName) }

  var modelDropdownExpanded by remember { mutableStateOf(false) }
  var lightingDropdownExpanded by remember { mutableStateOf(false) }
  var collarDropdownExpanded by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = StudioPanelBg,
    title = {
      Text(
        text = "Project & 3D Studio Settings",
        color = TextPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Project Name
        OutlinedTextField(
          value = nameInput,
          onValueChange = {
            nameInput = it
            onUpdateProject { p -> p.copy(name = it) }
          },
          label = { Text("Project / Kit Name") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = StudioPanelElevated,
            unfocusedContainerColor = StudioPanelElevated,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedBorderColor = AccentGreen,
            unfocusedBorderColor = StudioBorder
          )
        )

        // Team Name
        OutlinedTextField(
          value = teamInput,
          onValueChange = {
            teamInput = it
            onUpdateProject { p -> p.copy(teamName = it) }
          },
          label = { Text("Team / Club Name") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = StudioPanelElevated,
            unfocusedContainerColor = StudioPanelElevated,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedBorderColor = AccentGreen,
            unfocusedBorderColor = StudioBorder
          )
        )

        HorizontalDivider(color = StudioBorder)

        // 3D Model Fit
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Model Fit Variant:", color = TextSecondary, fontSize = 11.sp)
          Box {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(StudioPanelElevated)
                .border(1.dp, StudioBorder, RoundedCornerShape(4.dp))
                .clickable { modelDropdownExpanded = true }
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(project.modelFit.label, color = AccentGreen, fontSize = 11.sp)
            }
            DropdownMenu(
              expanded = modelDropdownExpanded,
              onDismissRequest = { modelDropdownExpanded = false },
              modifier = Modifier.background(StudioPanelElevated)
            ) {
              ModelFit.values().forEach { mf ->
                DropdownMenuItem(
                  text = { Text(mf.label, color = TextPrimary, fontSize = 11.sp) },
                  onClick = {
                    onUpdateProject { it.copy(modelFit = mf) }
                    modelDropdownExpanded = false
                  }
                )
              }
            }
          }
        }

        // Lighting Preset
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Lighting Environment:", color = TextSecondary, fontSize = 11.sp)
          Box {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(StudioPanelElevated)
                .border(1.dp, StudioBorder, RoundedCornerShape(4.dp))
                .clickable { lightingDropdownExpanded = true }
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(project.lightingPreset.label, color = AccentGreen, fontSize = 11.sp)
            }
            DropdownMenu(
              expanded = lightingDropdownExpanded,
              onDismissRequest = { lightingDropdownExpanded = false },
              modifier = Modifier.background(StudioPanelElevated)
            ) {
              LightingPreset.values().forEach { lp ->
                DropdownMenuItem(
                  text = { Text(lp.label, color = TextPrimary, fontSize = 11.sp) },
                  onClick = {
                    onUpdateProject { it.copy(lightingPreset = lp) }
                    lightingDropdownExpanded = false
                  }
                )
              }
            }
          }
        }

        // Collar Template
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Collar Template:", color = TextSecondary, fontSize = 11.sp)
          Box {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(StudioPanelElevated)
                .border(1.dp, StudioBorder, RoundedCornerShape(4.dp))
                .clickable { collarDropdownExpanded = true }
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(project.collarTemplate.label, color = AccentGreen, fontSize = 11.sp)
            }
            DropdownMenu(
              expanded = collarDropdownExpanded,
              onDismissRequest = { collarDropdownExpanded = false },
              modifier = Modifier.background(StudioPanelElevated)
            ) {
              CollarTemplate.values().forEach { ct ->
                DropdownMenuItem(
                  text = { Text(ct.label, color = TextPrimary, fontSize = 11.sp) },
                  onClick = {
                    onUpdateProject { it.copy(collarTemplate = ct) }
                    collarDropdownExpanded = false
                  }
                )
              }
            }
          }
        }

        HorizontalDivider(color = StudioBorder)

        // Shininess
        Column {
          Text("Global Fabric Shininess: ${project.globalShininess.roundToInt()}%", color = TextSecondary, fontSize = 11.sp)
          Slider(
            value = project.globalShininess,
            onValueChange = { gs -> onUpdateProject { it.copy(globalShininess = gs) } },
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(thumbColor = AccentGreen, activeTrackColor = AccentGreen)
          )
        }

        // Fabric Roughness / Knit
        Column {
          Text("Fabric Knit Micro-Texture: ${project.fabricRoughness.roundToInt()}%", color = TextSecondary, fontSize = 11.sp)
          Slider(
            value = project.fabricRoughness,
            onValueChange = { fr -> onUpdateProject { it.copy(fabricRoughness = fr) } },
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(thumbColor = AccentGreen, activeTrackColor = AccentGreen)
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onDismiss,
        colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
        modifier = Modifier.testTag("settings_done_button")
      ) {
        Text("Done", color = Color(0xFF052E16), fontWeight = FontWeight.Bold)
      }
    }
  )
}
