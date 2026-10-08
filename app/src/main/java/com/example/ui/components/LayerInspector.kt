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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import com.example.model.LayerType
import com.example.model.LogoType
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

  var colorPickerTarget by remember { mutableStateOf<String?>(null) } // "primary", "secondary", "stroke"

  // Collapsible section toggles
  var isTransformExpanded by remember { mutableStateOf(true) }
  var isAppearanceExpanded by remember { mutableStateOf(true) }
  var isPatternExpanded by remember { mutableStateOf(true) }
  var isTextExpanded by remember { mutableStateOf(true) }
  var isLogoExpanded by remember { mutableStateOf(true) }
  var isMaskExpanded by remember { mutableStateOf(true) }
  var isMaterialExpanded by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxHeight()
      .background(StudioPanelBg)
      .border(1.dp, StudioBorder)
      .padding(8.dp)
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    // 1. Header: Layer Name & Type
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
          modifier = Modifier
            .fillMaxWidth()
            .testTag("inspector_layer_name"),
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
        text = "⚡ Free Unlimited Positioning",
        color = AccentGold,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium
      )
    }

    HorizontalDivider(color = StudioBorder)

    // 2. TRANSFORM SECTION
    CollapsibleSection(
      title = "Transform",
      isExpanded = isTransformExpanded,
      onToggle = { isTransformExpanded = !isTransformExpanded }
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        // X & Y Offsets
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text("X Position: ${layer.transform.x.roundToInt()}%", color = TextSecondary, fontSize = 10.sp)
            Slider(
              value = layer.transform.x,
              onValueChange = { vx -> onUpdateLayer { it.copy(transform = it.transform.copy(x = vx)) } },
              valueRange = -80f..80f,
              colors = SliderDefaults.colors(thumbColor = AccentGreen, activeTrackColor = AccentGreen),
              modifier = Modifier.testTag("slider_transform_x")
            )
          }

          Column(modifier = Modifier.weight(1f)) {
            Text("Y Position: ${layer.transform.y.roundToInt()}%", color = TextSecondary, fontSize = 10.sp)
            Slider(
              value = layer.transform.y,
              onValueChange = { vy -> onUpdateLayer { it.copy(transform = it.transform.copy(y = vy)) } },
              valueRange = -80f..80f,
              colors = SliderDefaults.colors(thumbColor = AccentGreen, activeTrackColor = AccentGreen),
              modifier = Modifier.testTag("slider_transform_y")
            )
          }
        }

        // Scale & Link
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text("Scale: ${(layer.transform.scaleX * 100).roundToInt()}%", color = TextSecondary, fontSize = 10.sp)
            Slider(
              value = layer.transform.scaleX,
              onValueChange = { s ->
                onUpdateLayer {
                  val sy = if (it.transform.isLinked) s else it.transform.scaleY
                  it.copy(transform = it.transform.copy(scaleX = s, scaleY = sy))
                }
              },
              valueRange = 0.2f..2.5f,
              colors = SliderDefaults.colors(thumbColor = AccentGreen, activeTrackColor = AccentGreen),
              modifier = Modifier.testTag("slider_transform_scale")
            )
          }

          IconButton(
            onClick = {
              onUpdateLayer { it.copy(transform = it.transform.copy(isLinked = !it.transform.isLinked)) }
            },
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

        // Rotation (0 - 360 deg)
        Column {
          Text("Rotation: ${layer.transform.rotation.roundToInt()}°", color = TextSecondary, fontSize = 10.sp)
          Slider(
            value = layer.transform.rotation,
            onValueChange = { rot -> onUpdateLayer { it.copy(transform = it.transform.copy(rotation = rot)) } },
            valueRange = 0f..360f,
            colors = SliderDefaults.colors(thumbColor = AccentGreen, activeTrackColor = AccentGreen),
            modifier = Modifier.testTag("slider_transform_rotation")
          )
        }
      }
    }

    HorizontalDivider(color = StudioBorder)

    // 3. APPEARANCE SECTION
    CollapsibleSection(
      title = "Appearance",
      isExpanded = isAppearanceExpanded,
      onToggle = { isAppearanceExpanded = !isAppearanceExpanded }
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Color Picker Row
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
              text = "Primary: #" + String.format("%06X", (layer.primaryColor and 0x00FFFFFFL)),
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
            Text("Edit Color", color = AccentGreen, fontSize = 10.sp)
          }
        }

        // Secondary Color if Pattern or Graphic
        if (layer.type in listOf(LayerType.PATTERN, LayerType.GRAPHIC, LayerType.LOGO)) {
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
                  .background(layer.secondaryColorObj)
                  .border(1.5.dp, StudioBorder, CircleShape)
                  .clickable { colorPickerTarget = "secondary" }
              )
              Text(
                text = "Secondary: #" + String.format("%06X", (layer.secondaryColor and 0x00FFFFFFL)),
                color = TextPrimary,
                fontSize = 11.sp
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(StudioPanelElevated)
                .border(1.dp, StudioBorder, RoundedCornerShape(4.dp))
                .clickable { colorPickerTarget = "secondary" }
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text("Edit Color", color = AccentGreen, fontSize = 10.sp)
            }
          }
        }

        // Opacity Slider
        Column {
          Text("Opacity: ${(layer.opacity * 100).roundToInt()}%", color = TextSecondary, fontSize = 10.sp)
          Slider(
            value = layer.opacity,
            onValueChange = { op -> onUpdateLayer { it.copy(opacity = op) } },
            valueRange = 0f..1f,
            colors = SliderDefaults.colors(thumbColor = AccentGreen, activeTrackColor = AccentGreen)
          )
        }

        // Blend Mode Selector
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
                  onClick = {
                    onUpdateLayer { it.copy(blendMode = mode) }
                    blendDropdownExpanded = false
                  }
                )
              }
            }
          }
        }
      }
    }

    // 4. PATTERN SPECIFIC CONTROLS
    if (layer.type == LayerType.PATTERN) {
      HorizontalDivider(color = StudioBorder)
      CollapsibleSection(
        title = "Pattern Configuration",
        isExpanded = isPatternExpanded,
        onToggle = { isPatternExpanded = !isPatternExpanded }
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          var patternDropdownExpanded by remember { mutableStateOf(false) }
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Pattern Type:", color = TextSecondary, fontSize = 11.sp)
            Box {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(StudioPanelElevated)
                  .border(1.dp, StudioBorder, RoundedCornerShape(4.dp))
                  .clickable { patternDropdownExpanded = true }
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(layer.patternType.label, color = AccentGreen, fontSize = 11.sp)
              }
              DropdownMenu(
                expanded = patternDropdownExpanded,
                onDismissRequest = { patternDropdownExpanded = false },
                modifier = Modifier.background(StudioPanelElevated)
              ) {
                KitPatternType.values().forEach { pt ->
                  DropdownMenuItem(
                    text = { Text(pt.label, color = TextPrimary, fontSize = 11.sp) },
                    onClick = {
                      onUpdateLayer { it.copy(patternType = pt) }
                      patternDropdownExpanded = false
                    }
                  )
                }
              }
            }
          }

          // Density / Frequency Slider
          Column {
            Text("Frequency / Stripe Count: ${layer.patternFrequency}", color = TextSecondary, fontSize = 10.sp)
            Slider(
              value = layer.patternFrequency.toFloat(),
              onValueChange = { f -> onUpdateLayer { it.copy(patternFrequency = f.roundToInt()) } },
              valueRange = 2f..20f,
              steps = 18,
              colors = SliderDefaults.colors(thumbColor = AccentGreen, activeTrackColor = AccentGreen)
            )
          }
        }
      }
    }

    // 5. TEXT SPECIFIC CONTROLS
    if (layer.type == LayerType.TEXT) {
      HorizontalDivider(color = StudioBorder)
      CollapsibleSection(
        title = "Typography & Squad Numbers",
        isExpanded = isTextExpanded,
        onToggle = { isTextExpanded = !isTextExpanded }
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = layer.text,
            onValueChange = { t -> onUpdateLayer { it.copy(text = t) } },
            label = { Text("Text / Number") },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("text_input_field"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = StudioPanelElevated,
              unfocusedContainerColor = StudioPanelElevated,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary,
              focusedBorderColor = AccentGreen,
              unfocusedBorderColor = StudioBorder
            )
          )

          // Quick Number Chips
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf("7", "9", "10", "11", "23").forEach { num ->
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(StudioPanelElevated)
                  .border(1.dp, StudioBorder, RoundedCornerShape(4.dp))
                  .clickable { onUpdateLayer { it.copy(text = num, textSize = 54f) } }
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(num, color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
              }
            }
          }

          // Font Selector
          var fontDropdownExpanded by remember { mutableStateOf(false) }
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Font Style:", color = TextSecondary, fontSize = 11.sp)
            Box {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(StudioPanelElevated)
                  .border(1.dp, StudioBorder, RoundedCornerShape(4.dp))
                  .clickable { fontDropdownExpanded = true }
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(layer.textFont.label, color = TextPrimary, fontSize = 11.sp)
              }
              DropdownMenu(
                expanded = fontDropdownExpanded,
                onDismissRequest = { fontDropdownExpanded = false },
                modifier = Modifier.background(StudioPanelElevated)
              ) {
                TextFont.values().forEach { f ->
                  DropdownMenuItem(
                    text = { Text(f.label, color = TextPrimary, fontSize = 11.sp) },
                    onClick = {
                      onUpdateLayer { it.copy(textFont = f) }
                      fontDropdownExpanded = false
                    }
                  )
                }
              }
            }
          }

          // Outline Stroke
          Column {
            Text("Outline Stroke: ${layer.strokeWidth.roundToInt()}px", color = TextSecondary, fontSize = 10.sp)
            Slider(
              value = layer.strokeWidth,
              onValueChange = { sw -> onUpdateLayer { it.copy(strokeWidth = sw) } },
              valueRange = 0f..8f,
              colors = SliderDefaults.colors(thumbColor = AccentGreen, activeTrackColor = AccentGreen)
            )
          }
        }
      }
    }

    // 6. LOGO SPECIFIC CONTROLS
    if (layer.type == LayerType.LOGO) {
      HorizontalDivider(color = StudioBorder)
      CollapsibleSection(
        title = "Logo & Sponsor Library",
        isExpanded = isLogoExpanded,
        onToggle = { isLogoExpanded = !isLogoExpanded }
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          var logoDropdownExpanded by remember { mutableStateOf(false) }
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Emblem:", color = TextSecondary, fontSize = 11.sp)
            Box {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(StudioPanelElevated)
                  .border(1.dp, StudioBorder, RoundedCornerShape(4.dp))
                  .clickable { logoDropdownExpanded = true }
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(layer.logoType.label, color = AccentGreen, fontSize = 11.sp)
              }
              DropdownMenu(
                expanded = logoDropdownExpanded,
                onDismissRequest = { logoDropdownExpanded = false },
                modifier = Modifier.background(StudioPanelElevated)
              ) {
                LogoType.values().forEach { lt ->
                  DropdownMenuItem(
                    text = { Text("${lt.category}: ${lt.label}", color = TextPrimary, fontSize = 11.sp) },
                    onClick = {
                      onUpdateLayer { it.copy(logoType = lt) }
                      logoDropdownExpanded = false
                    }
                  )
                }
              }
            }
          }
        }
      }
    }

    // 7. MASK SECTION
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
                onClick = {
                  onUpdateLayer { it.copy(mask = m) }
                  maskDropdownExpanded = false
                }
              )
            }
          }
        }
      }
    }

    // 8. MATERIAL SECTION
    HorizontalDivider(color = StudioBorder)
    CollapsibleSection(
      title = "Material & 3D Resa",
      isExpanded = isMaterialExpanded,
      onToggle = { isMaterialExpanded = !isMaterialExpanded }
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Column {
          Text("Shininess / Gloss: ${layer.shininess.roundToInt()}%", color = TextSecondary, fontSize = 10.sp)
          Slider(
            value = layer.shininess,
            onValueChange = { s -> onUpdateLayer { it.copy(shininess = s) } },
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(thumbColor = AccentGreen, activeTrackColor = AccentGreen)
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Follow Body Warp", color = TextSecondary, fontSize = 11.sp)
          Switch(
            checked = layer.followBodyShape,
            onCheckedChange = { f -> onUpdateLayer { it.copy(followBodyShape = f) } },
            colors = SwitchDefaults.colors(checkedThumbColor = AccentGreen, checkedTrackColor = Color(0xFF14532D))
          )
        }
      }
    }
  }

  // Color picker dialog
  if (colorPickerTarget != null) {
    val initialColor = when (colorPickerTarget) {
      "secondary" -> layer.secondaryColor
      "stroke" -> layer.strokeColor
      else -> layer.primaryColor
    }
    ColorPickerDialog(
      initialColor = initialColor,
      title = "Select ${colorPickerTarget?.replaceFirstChar { it.uppercase() }} Color",
      onColorSelected = { col ->
        onUpdateLayer {
          when (colorPickerTarget) {
            "secondary" -> it.copy(secondaryColor = col)
            "stroke" -> it.copy(strokeColor = col)
            else -> it.copy(primaryColor = col)
          }
        }
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
