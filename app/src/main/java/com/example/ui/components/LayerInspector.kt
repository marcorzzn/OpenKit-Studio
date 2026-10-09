package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.KitLayer
import com.example.model.KitPatternType
import com.example.model.LayerBlendMode
import com.example.model.LayerMask
import com.example.model.LayerPlacement
import com.example.model.LayerType
import com.example.model.LogoType
import com.example.model.PlacementPreset
import com.example.model.TextFont
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioPanelBg
import com.example.ui.theme.StudioPanelElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.roundToInt

@Composable
fun LayerInspector(
  layer: KitLayer?,
  onUpdateLayer: ((KitLayer) -> KitLayer) -> Unit,
  onClose: () -> Unit,
  modifier: Modifier = Modifier
) {
  if (layer == null) {
    Box(
      modifier = modifier
        .fillMaxHeight()
        .background(StudioPanelBg)
        .border(1.dp, StudioBorder)
        .padding(16.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "Select a layer to edit properties",
        color = TextSecondary,
        fontSize = 12.sp
      )
    }
    return
  }

  var colorPickerTarget by remember { mutableStateOf<String?>(null) }

  var isLogoPlacementExpanded by remember { mutableStateOf(true) }
  var isTransformExpanded by remember { mutableStateOf(true) }
  var isEffectsExpanded by remember { mutableStateOf(true) }
  var isAppearanceExpanded by remember { mutableStateOf(true) }
  var isMaskExpanded by remember { mutableStateOf(true) }
  var isMaterialExpanded by remember { mutableStateOf(true) }

  Column(
    modifier = modifier
      .fillMaxHeight()
      .background(StudioPanelBg)
      .border(1.dp, StudioBorder)
      .padding(8.dp)
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    // 1. Header: Layer Name & Close
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column(modifier = Modifier.weight(1f)) {
        OutlinedTextField(
          value = layer.name,
          onValueChange = { newName -> onUpdateLayer { it.copy(name = newName) } },
          label = { Text("Layer Name", fontSize = 10.sp) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = StudioPanelElevated,
            unfocusedContainerColor = StudioPanelElevated,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedBorderColor = AccentGreen,
            unfocusedBorderColor = StudioBorder
          )
        )
      }

      Spacer(modifier = Modifier.width(6.dp))

      IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
        Icon(Icons.Default.Close, contentDescription = "Close Inspector", tint = TextSecondary, modifier = Modifier.size(16.dp))
      }
    }

    // Type Badge & Free Positioning Note
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(4.dp))
          .background(StudioPanelElevated)
          .border(1.dp, StudioBorder, RoundedCornerShape(4.dp))
          .padding(horizontal = 6.dp, vertical = 2.dp)
      ) {
        Text(layer.type.label, color = AccentGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
      }

      Text(
        text = "⚡ 100% Free Custom Positioning",
        color = AccentGold,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium
      )
    }

    HorizontalDivider(color = StudioBorder)

    // 2. LOGO / GRAPHIC PLACEMENT SECTION (Modulo 1)
    if (layer.type in listOf(LayerType.LOGO, LayerType.GRAPHIC, LayerType.TEXT)) {
      CollapsibleSection(
        title = "Placement & Presets",
        isExpanded = isLogoPlacementExpanded,
        onToggle = { isLogoPlacementExpanded = !isLogoPlacementExpanded }
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          // Placement Presets Dropdown
          var placementPresetMenu by remember { mutableStateOf(false) }
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Placement Preset:", color = TextSecondary, fontSize = 11.sp)
            Box {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(StudioPanelElevated)
                  .border(1.dp, StudioBorder, RoundedCornerShape(4.dp))
                  .clickable { placementPresetMenu = true }
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(layer.placementPreset.label, color = AccentGreen, fontSize = 11.sp)
              }
              DropdownMenu(expanded = placementPresetMenu, onDismissRequest = { placementPresetMenu = false }) {
                PlacementPreset.values().forEach { pp ->
                  DropdownMenuItem(
                    text = { Text(pp.label, color = TextPrimary, fontSize = 11.sp) },
                    onClick = {
                      onUpdateLayer {
                        it.copy(
                          placementPreset = pp,
                          transform = if (pp != PlacementPreset.CUSTOM) it.transform.copy(x = pp.x, y = pp.y) else it.transform
                        )
                      }
                      placementPresetMenu = false
                    }
                  )
                }
              }
            }
          }

          // Placement Dropdown (Front, Back, Sleeve left, Sleeve right, Collar, Hem)
          var placementZoneMenu by remember { mutableStateOf(false) }
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Placement Zone:", color = TextSecondary, fontSize = 11.sp)
            Box {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(StudioPanelElevated)
                  .border(1.dp, StudioBorder, RoundedCornerShape(4.dp))
                  .clickable { placementZoneMenu = true }
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(layer.placement.label, color = TextPrimary, fontSize = 11.sp)
              }
              DropdownMenu(expanded = placementZoneMenu, onDismissRequest = { placementZoneMenu = false }) {
                LayerPlacement.values().forEach { lp ->
                  DropdownMenuItem(
                    text = { Text(lp.label, color = TextPrimary, fontSize = 11.sp) },
                    onClick = { onUpdateLayer { it.copy(placement = lp) }; placementZoneMenu = false }
                  )
                }
              }
            }
          }

          // Fabric Print Toggle
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Fabric Print (Sublimation Weave)", color = TextPrimary, fontSize = 11.sp)
            Switch(
              checked = layer.fabricPrint,
              onCheckedChange = { fp -> onUpdateLayer { it.copy(fabricPrint = fp) } },
              colors = SwitchDefaults.colors(checkedThumbColor = AccentGreen, checkedTrackColor = Color(0xFF14532D))
            )
          }

          // Mirror & Mirror Center Toggles
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              Text("Mirror", color = TextSecondary, fontSize = 11.sp)
              Switch(
                checked = layer.mirror,
                onCheckedChange = { m -> onUpdateLayer { it.copy(mirror = m) } },
                colors = SwitchDefaults.colors(checkedThumbColor = AccentGreen, checkedTrackColor = Color(0xFF14532D))
              )
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              Text("Mirror Center", color = TextSecondary, fontSize = 11.sp)
              Switch(
                checked = layer.mirrorCenter,
                onCheckedChange = { mc -> onUpdateLayer { it.copy(mirrorCenter = mc) } },
                colors = SwitchDefaults.colors(checkedThumbColor = AccentGreen, checkedTrackColor = Color(0xFF14532D))
              )
            }
          }
        }
      }
      HorizontalDivider(color = StudioBorder)
    }

    // 3. TRANSFORM SECTION
    CollapsibleSection(
      title = "Transformation",
      isExpanded = isTransformExpanded,
      onToggle = { isTransformExpanded = !isTransformExpanded }
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        // X & Y
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text("Position (X): ${layer.transform.x.roundToInt()}%", color = TextSecondary, fontSize = 10.sp)
            Slider(
              value = layer.transform.x,
              onValueChange = { vx -> onUpdateLayer { it.copy(transform = it.transform.copy(x = vx), placementPreset = PlacementPreset.CUSTOM) } },
              valueRange = -80f..80f,
              colors = SliderDefaults.colors(thumbColor = AccentGreen, activeTrackColor = AccentGreen)
            )
          }

          Column(modifier = Modifier.weight(1f)) {
            Text("Position (Y): ${layer.transform.y.roundToInt()}%", color = TextSecondary, fontSize = 10.sp)
            Slider(
              value = layer.transform.y,
              onValueChange = { vy -> onUpdateLayer { it.copy(transform = it.transform.copy(y = vy), placementPreset = PlacementPreset.CUSTOM) } },
              valueRange = -80f..80f,
              colors = SliderDefaults.colors(thumbColor = AccentGreen, activeTrackColor = AccentGreen)
            )
          }
        }

        // Size / Scale
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text("Size: ${(layer.transform.scaleX * 100).roundToInt()}%", color = TextSecondary, fontSize = 10.sp)
            Slider(
              value = layer.transform.scaleX,
              onValueChange = { s ->
                onUpdateLayer {
                  val sy = if (it.transform.isLinked) s else it.transform.scaleY
                  it.copy(transform = it.transform.copy(scaleX = s, scaleY = sy))
                }
              },
              valueRange = 0.2f..2.5f,
              colors = SliderDefaults.colors(thumbColor = AccentGreen, activeTrackColor = AccentGreen)
            )
          }

          IconButton(
            onClick = { onUpdateLayer { it.copy(transform = it.transform.copy(isLinked = !it.transform.isLinked)) } },
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = if (layer.transform.isLinked) Icons.Default.Link else Icons.Default.LinkOff,
              contentDescription = "Link Scale",
              tint = if (layer.transform.isLinked) AccentGreen else TextSecondary,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        // Height Ratio & Saturation
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text("Height Ratio: ${layer.transform.heightRatio.roundToInt()}%", color = TextSecondary, fontSize = 10.sp)
            Slider(
              value = layer.transform.heightRatio,
              onValueChange = { hr -> onUpdateLayer { it.copy(transform = it.transform.copy(heightRatio = hr)) } },
              valueRange = -80f..80f,
              colors = SliderDefaults.colors(thumbColor = AccentGreen, activeTrackColor = AccentGreen)
            )
          }

          Column(modifier = Modifier.weight(1f)) {
            Text("Saturation: ${layer.transform.saturation.roundToInt()}%", color = TextSecondary, fontSize = 10.sp)
            Slider(
              value = layer.transform.saturation,
              onValueChange = { sat -> onUpdateLayer { it.copy(transform = it.transform.copy(saturation = sat)) } },
              valueRange = 0f..200f,
              colors = SliderDefaults.colors(thumbColor = AccentGreen, activeTrackColor = AccentGreen)
            )
          }
        }

        // Rotation (-180 to 180 deg)
        Column {
          Text("Rotation: ${layer.transform.rotation.roundToInt()}°", color = TextSecondary, fontSize = 10.sp)
          Slider(
            value = layer.transform.rotation,
            onValueChange = { rot -> onUpdateLayer { it.copy(transform = it.transform.copy(rotation = rot)) } },
            valueRange = -180f..180f,
            colors = SliderDefaults.colors(thumbColor = AccentGreen, activeTrackColor = AccentGreen)
          )
        }
      }
    }

    HorizontalDivider(color = StudioBorder)

    // 4. EFFECTS SECTION (Border, Blur)
    CollapsibleSection(
      title = "Effects (Border & Blur)",
      isExpanded = isEffectsExpanded,
      onToggle = { isEffectsExpanded = !isEffectsExpanded }
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Column {
          Text("Border Outline: ${layer.border.roundToInt()}", color = TextSecondary, fontSize = 10.sp)
          Slider(
            value = layer.border,
            onValueChange = { b -> onUpdateLayer { it.copy(border = b) } },
            valueRange = 0f..20f,
            colors = SliderDefaults.colors(thumbColor = AccentGreen, activeTrackColor = AccentGreen)
          )
        }

        Column {
          Text("Soft Blur: ${layer.blur.roundToInt()}", color = TextSecondary, fontSize = 10.sp)
          Slider(
            value = layer.blur,
            onValueChange = { bl -> onUpdateLayer { it.copy(blur = bl) } },
            valueRange = 0f..20f,
            colors = SliderDefaults.colors(thumbColor = AccentGreen, activeTrackColor = AccentGreen)
          )
        }
      }
    }

    HorizontalDivider(color = StudioBorder)

    // 5. APPEARANCE & COMPOSITION SECTION
    CollapsibleSection(
      title = "Appearance & Blend",
      isExpanded = isAppearanceExpanded,
      onToggle = { isAppearanceExpanded = !isAppearanceExpanded }
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Color Picker Row + Recolor Button
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(
              modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(layer.primaryColorObj)
                .border(1.5.dp, StudioBorder, CircleShape)
                .clickable { colorPickerTarget = "primary" }
            )
            Text(
              text = "Color: #" + String.format("%06X", (layer.primaryColor and 0x00FFFFFFL)),
              color = TextPrimary,
              fontSize = 11.sp
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(StudioPanelElevated)
              .border(1.dp, StudioBorder, RoundedCornerShape(4.dp))
              .clickable { colorPickerTarget = "primary" }
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text("Recolor", color = AccentGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }
        }

        // Opacity
        Column {
          Text("Opacity: ${(layer.opacity * 100).roundToInt()}%", color = TextSecondary, fontSize = 10.sp)
          Slider(
            value = layer.opacity,
            onValueChange = { op -> onUpdateLayer { it.copy(opacity = op) } },
            valueRange = 0f..1f,
            colors = SliderDefaults.colors(thumbColor = AccentGreen, activeTrackColor = AccentGreen)
          )
        }

        // Blend Mode
        var blendDropdownExpanded by remember { mutableStateOf(false) }
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Blend Mode:", color = TextSecondary, fontSize = 11.sp)
          Box {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(StudioPanelElevated)
                .border(1.dp, StudioBorder, RoundedCornerShape(4.dp))
                .clickable { blendDropdownExpanded = true }
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(layer.blendMode.label, color = TextPrimary, fontSize = 11.sp)
            }
            DropdownMenu(
              expanded = blendDropdownExpanded,
              onDismissRequest = { blendDropdownExpanded = false },
              modifier = Modifier.background(StudioPanelElevated)
            ) {
              LayerBlendMode.values().forEach { mode ->
                DropdownMenuItem(
                  text = { Text(mode.label, color = TextPrimary, fontSize = 11.sp) },
                  onClick = { onUpdateLayer { it.copy(blendMode = mode) }; blendDropdownExpanded = false }
                )
              }
            }
          }
        }
      }
    }

    // 6. MASK SECTION
    HorizontalDivider(color = StudioBorder)
    CollapsibleSection(
      title = "Mask Clipping",
      isExpanded = isMaskExpanded,
      onToggle = { isMaskExpanded = !isMaskExpanded }
    ) {
      var maskDropdownExpanded by remember { mutableStateOf(false) }
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text("Target Zone:", color = TextSecondary, fontSize = 11.sp)
        Box {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(StudioPanelElevated)
              .border(1.dp, StudioBorder, RoundedCornerShape(4.dp))
              .clickable { maskDropdownExpanded = true }
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(layer.mask.label, color = TextPrimary, fontSize = 11.sp)
          }
          DropdownMenu(
            expanded = maskDropdownExpanded,
            onDismissRequest = { maskDropdownExpanded = false },
            modifier = Modifier.background(StudioPanelElevated)
          ) {
            LayerMask.values().forEach { m ->
              DropdownMenuItem(
                text = { Text(m.label, color = TextPrimary, fontSize = 11.sp) },
                onClick = { onUpdateLayer { it.copy(mask = m) }; maskDropdownExpanded = false }
              )
            }
          }
        }
      }
    }

    // 7. MATERIAL SECTION
    HorizontalDivider(color = StudioBorder)
    CollapsibleSection(
      title = "Material & 3D Resa",
      isExpanded = isMaterialExpanded,
      onToggle = { isMaterialExpanded = !isMaterialExpanded }
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Custom Shininess / Metalness", color = TextPrimary, fontSize = 11.sp)
          Switch(
            checked = layer.customMaterialEnabled,
            onCheckedChange = { cme -> onUpdateLayer { it.copy(customMaterialEnabled = cme) } },
            colors = SwitchDefaults.colors(checkedThumbColor = AccentGreen, checkedTrackColor = Color(0xFF14532D))
          )
        }

        if (layer.customMaterialEnabled) {
          Column {
            Text("Shininess: ${layer.customShininess.roundToInt()}%", color = TextSecondary, fontSize = 10.sp)
            Slider(
              value = layer.customShininess,
              onValueChange = { s -> onUpdateLayer { it.copy(customShininess = s) } },
              valueRange = 0f..100f,
              colors = SliderDefaults.colors(thumbColor = AccentGreen, activeTrackColor = AccentGreen)
            )
          }

          Column {
            Text("Metalness: ${layer.customMetalness.roundToInt()}%", color = TextSecondary, fontSize = 10.sp)
            Slider(
              value = layer.customMetalness,
              onValueChange = { m -> onUpdateLayer { it.copy(customMetalness = m) } },
              valueRange = 0f..100f,
              colors = SliderDefaults.colors(thumbColor = AccentGreen, activeTrackColor = AccentGreen)
            )
          }
        }
      }
    }
  }

  // Color picker dialog
  if (colorPickerTarget != null) {
    ColorPickerDialog(
      initialColor = layer.primaryColor,
      title = "Select Layer Color",
      onColorSelected = { col ->
        onUpdateLayer { it.copy(primaryColor = col) }
        colorPickerTarget = null
      },
      onDismiss = { colorPickerTarget = null }
    )
  }
}

@Composable
fun CollapsibleSection(
  title: String,
  isExpanded: Boolean,
  onToggle: () -> Unit,
  content: @Composable () -> Unit
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = onToggle)
        .padding(vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = title,
        color = TextPrimary,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp
      )
      Icon(
        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
        contentDescription = "Toggle Section",
        tint = TextSecondary,
        modifier = Modifier.size(16.dp)
      )
    }

    if (isExpanded) {
      Spacer(modifier = Modifier.height(4.dp))
      content()
    }
  }
}
