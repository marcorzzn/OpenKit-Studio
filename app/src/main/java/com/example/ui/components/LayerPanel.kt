package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CollarTemplate
import com.example.model.KitComponentType
import com.example.model.KitLayer
import com.example.model.LayerBlendMode
import com.example.model.LayerType
import com.example.model.ModelFit
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioPanelBg
import com.example.ui.theme.StudioPanelElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.roundToInt

@Composable
fun LayerPanel(
  selectedComponent: KitComponentType,
  layers: List<KitLayer>,
  selectedLayerId: String?,
  currentCollar: CollarTemplate,
  currentModelFit: ModelFit,
  onSelectLayer: (String) -> Unit,
  onAddLayer: (LayerType) -> Unit,
  onDeleteLayer: (String) -> Unit,
  onToggleVisibility: (String) -> Unit,
  onToggleLock: (String) -> Unit,
  onMoveUp: (String) -> Unit,
  onMoveDown: (String) -> Unit,
  onUpdateLayer: ((KitLayer) -> KitLayer) -> Unit,
  onSelectCollar: (CollarTemplate) -> Unit,
  onSelectModelFit: (ModelFit) -> Unit,
  onKitVisionClick: () -> Unit,
  onSaveClick: () -> Unit,
  onLoadClick: () -> Unit,
  onOpenPatternLibrary: () -> Unit,
  modifier: Modifier = Modifier
) {
  var collarMenuExpanded by remember { mutableStateOf(false) }
  var modelMenuExpanded by remember { mutableStateOf(false) }
  var inlinePopupLayerId by remember { mutableStateOf<String?>(null) }

  Column(
    modifier = modifier
      .fillMaxHeight()
      .background(StudioPanelBg)
      .border(1.dp, StudioBorder)
      .padding(8.dp)
  ) {
    // 1. "Kit Vision" Top Card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(
          Brush.horizontalGradient(
            listOf(Color(0xFF15803D), Color(0xFF22C55E))
          )
        )
        .clickable(onClick = onKitVisionClick)
        .padding(10.dp)
        .testTag("kit_vision_card")
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text(
              text = "Kit Vision",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0x33000000))
                .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
              Text("AI ONE-CLICK", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
          }
          Text(
            text = "Generate pattern & styling instantly",
            color = Color(0xFFDCFCE7),
            fontSize = 10.sp
          )
        }

        Box(
          modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(Color(0x33000000)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = "Kit Vision Magic",
            tint = Color.White,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // 2. Model & Collar Template Dropdowns (Modulo 1 & 2)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      // Model Fit Selector (Model 0 - Model 4)
      Box(modifier = Modifier.weight(1f)) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(StudioPanelElevated)
            .border(1.dp, StudioBorder, RoundedCornerShape(6.dp))
            .clickable { modelMenuExpanded = true }
            .padding(horizontal = 6.dp, vertical = 5.dp)
        ) {
          Column {
            Text("FIT SILHOUETTE", color = TextSecondary, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            Text("Model ${currentModelFit.id}", color = AccentGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
          }
        }
        DropdownMenu(
          expanded = modelMenuExpanded,
          onDismissRequest = { modelMenuExpanded = false },
          modifier = Modifier.background(StudioPanelElevated)
        ) {
          ModelFit.values().forEach { mf ->
            DropdownMenuItem(
              text = { Text(mf.label, color = TextPrimary, fontSize = 11.sp) },
              onClick = {
                onSelectModelFit(mf)
                modelMenuExpanded = false
              }
            )
          }
        }
      }

      // Collar Template Selector (Collar 0 - Collar 26)
      Box(modifier = Modifier.weight(1.2f)) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(StudioPanelElevated)
            .border(1.dp, StudioBorder, RoundedCornerShape(6.dp))
            .clickable { collarMenuExpanded = true }
            .padding(horizontal = 6.dp, vertical = 5.dp)
        ) {
          Column {
            Text("COLLAR MESH", color = TextSecondary, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            Text("Collar ${currentCollar.id}", color = AccentGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
          }
        }
        DropdownMenu(
          expanded = collarMenuExpanded,
          onDismissRequest = { collarMenuExpanded = false },
          modifier = Modifier.background(StudioPanelElevated)
        ) {
          CollarTemplate.values().forEach { ct ->
            DropdownMenuItem(
              text = { Text(ct.label, color = TextPrimary, fontSize = 11.sp) },
              onClick = {
                onSelectCollar(ct)
                collarMenuExpanded = false
              }
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Component Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp, vertical = 2.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = "${selectedComponent.label} Layers",
        color = TextPrimary,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp
      )
      Text(
        text = "${layers.size} active",
        color = TextSecondary,
        fontSize = 11.sp
      )
    }

    Spacer(modifier = Modifier.height(4.dp))

    // 3. Layer Tree List
    LazyColumn(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      itemsIndexed(layers, key = { _, layer -> layer.id }) { index, layer ->
        val isSelected = layer.id == selectedLayerId
        val isPopupOpen = layer.id == inlinePopupLayerId
        val isGroup = layer.type == LayerType.GROUP

        Column(modifier = Modifier.fillMaxWidth()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(6.dp))
              .background(if (isSelected) StudioPanelElevated else Color(0xFF16171B))
              .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) AccentGreen else StudioBorder,
                shape = RoundedCornerShape(6.dp)
              )
              .clickable {
                onSelectLayer(layer.id)
                inlinePopupLayerId = if (inlinePopupLayerId == layer.id) null else layer.id
              }
              .padding(horizontal = 6.dp, vertical = 4.dp)
              .testTag("layer_item_${layer.id}")
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Group expand arrow or drag handle
              if (isGroup) {
                Icon(
                  imageVector = if (layer.expanded) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                  contentDescription = "Expand Group",
                  tint = AccentGreen,
                  modifier = Modifier
                    .size(16.dp)
                    .clickable { onUpdateLayer { it.copy(expanded = !it.expanded) } }
                )
              } else {
                Column {
                  if (index > 0) {
                    Icon(
                      imageVector = Icons.Default.KeyboardArrowUp,
                      contentDescription = "Move Up",
                      tint = TextSecondary,
                      modifier = Modifier.size(12.dp).clickable { onMoveUp(layer.id) }
                    )
                  }
                  if (index < layers.size - 1) {
                    Icon(
                      imageVector = Icons.Default.KeyboardArrowDown,
                      contentDescription = "Move Down",
                      tint = TextSecondary,
                      modifier = Modifier.size(12.dp).clickable { onMoveDown(layer.id) }
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.width(4.dp))

              // Visibility Eye Toggle
              IconButton(onClick = { onToggleVisibility(layer.id) }, modifier = Modifier.size(24.dp)) {
                Icon(
                  imageVector = if (layer.visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                  contentDescription = "Visibility",
                  tint = if (layer.visible) AccentGreen else TextSecondary.copy(alpha = 0.4f),
                  modifier = Modifier.size(14.dp)
                )
              }

              // Lock Toggle
              IconButton(onClick = { onToggleLock(layer.id) }, modifier = Modifier.size(24.dp)) {
                Icon(
                  imageVector = if (layer.locked) Icons.Default.Lock else Icons.Default.LockOpen,
                  contentDescription = "Lock",
                  tint = if (layer.locked) AccentGold else TextSecondary.copy(alpha = 0.4f),
                  modifier = Modifier.size(14.dp)
                )
              }

              // Color Chip
              Box(
                modifier = Modifier
                  .size(12.dp)
                  .clip(CircleShape)
                  .background(layer.primaryColorObj)
                  .border(1.dp, StudioBorder, CircleShape)
              )

              Spacer(modifier = Modifier.width(6.dp))

              // Name
              Text(
                text = layer.name,
                color = if (layer.visible) TextPrimary else TextSecondary,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1,
                modifier = Modifier.weight(1f)
              )

              // Delete (×)
              IconButton(onClick = { onDeleteLayer(layer.id) }, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Delete", tint = TextSecondary, modifier = Modifier.size(14.dp))
              }
            }
          }

          // 4. Inline Layer Popup (Modulo 1 - Screenshot 2 & 3)
          if (isPopupOpen) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF23252E))
                .border(1.dp, StudioBorder, RoundedCornerShape(8.dp))
                .padding(10.dp)
            ) {
              Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Opacity slider
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text("Opacity", color = TextSecondary, fontSize = 11.sp)
                  Text("${(layer.opacity * 100).roundToInt()}%", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Slider(
                  value = layer.opacity,
                  onValueChange = { op -> onUpdateLayer { it.copy(opacity = op) } },
                  valueRange = 0f..1f,
                  colors = SliderDefaults.colors(thumbColor = AccentGreen, activeTrackColor = AccentGreen)
                )

                // Blend mode dropdown
                var blendOpen by remember { mutableStateOf(false) }
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text("Blend mode", color = TextSecondary, fontSize = 11.sp)
                  Box {
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(StudioPanelElevated)
                        .border(1.dp, StudioBorder, RoundedCornerShape(4.dp))
                        .clickable { blendOpen = true }
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                      Text(layer.blendMode.label, color = AccentGreen, fontSize = 10.sp)
                    }
                    DropdownMenu(expanded = blendOpen, onDismissRequest = { blendOpen = false }) {
                      LayerBlendMode.values().forEach { bm ->
                        DropdownMenuItem(
                          text = { Text(bm.label, color = TextPrimary, fontSize = 11.sp) },
                          onClick = { onUpdateLayer { it.copy(blendMode = bm) }; blendOpen = false }
                        )
                      }
                    }
                  }
                }

                // Custom Shininess / Metalness Toggle
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text("Custom shininess / metalness", color = TextSecondary, fontSize = 10.sp)
                  Switch(
                    checked = layer.customMaterialEnabled,
                    onCheckedChange = { en -> onUpdateLayer { it.copy(customMaterialEnabled = en) } },
                    colors = SwitchDefaults.colors(checkedThumbColor = AccentGreen, checkedTrackColor = Color(0xFF14532D))
                  )
                }

                // Expanded Shininess & Metalness Sliders
                if (layer.customMaterialEnabled) {
                  Column {
                    Text("Shininess: ${layer.customShininess.roundToInt()}%", color = TextSecondary, fontSize = 10.sp)
                    Slider(
                      value = layer.customShininess,
                      onValueChange = { sh -> onUpdateLayer { it.copy(customShininess = sh) } },
                      valueRange = 0f..100f,
                      colors = SliderDefaults.colors(thumbColor = AccentGreen, activeTrackColor = AccentGreen)
                    )
                  }
                  Column {
                    Text("Metalness: ${layer.customMetalness.roundToInt()}%", color = TextSecondary, fontSize = 10.sp)
                    Slider(
                      value = layer.customMetalness,
                      onValueChange = { met -> onUpdateLayer { it.copy(customMetalness = met) } },
                      valueRange = 0f..100f,
                      colors = SliderDefaults.colors(thumbColor = AccentGreen, activeTrackColor = AccentGreen)
                    )
                  }
                }
              }
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = StudioBorder)
        Spacer(modifier = Modifier.height(8.dp))

        // 5. Section "ADD LAYER" with 9 buttons (2 columns)
        Text(
          text = "ADD LAYER",
          color = AccentGreen,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp,
          modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        val addButtons = listOf(
          LayerType.BASE_DESIGN to Icons.Default.FormatPaint,
          LayerType.GRAPHIC to Icons.Default.Image,
          LayerType.PATTERN to Icons.Default.GridOn,
          LayerType.TEXT to Icons.Default.TextFields,
          LayerType.LOGO to Icons.Default.Shield,
          LayerType.SLEEVE_CUFFS to Icons.Default.Add,
          LayerType.SIDE_PANELS to Icons.Default.Add,
          LayerType.SHOULDER_PANELS to Icons.Default.Add,
          LayerType.GROUP to Icons.Default.Folder
        )

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          addButtons.forEach { (type, icon) ->
            AddLayerButton(
              title = type.label,
              icon = icon,
              onClick = {
                if (type == LayerType.PATTERN) {
                  onOpenPatternLibrary()
                } else {
                  onAddLayer(type)
                }
              }
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ADD LAYER FROM SAVED / TEMPLATES
        Button(
          onClick = onLoadClick,
          colors = ButtonDefaults.buttonColors(containerColor = StudioPanelElevated),
          modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .border(1.dp, StudioBorder, RoundedCornerShape(6.dp))
        ) {
          Text("ADD LAYER FROM SAVED / TEMPLATES", color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    // Bottom Footer (Save / Load)
    Spacer(modifier = Modifier.height(6.dp))
    HorizontalDivider(color = StudioBorder)
    Spacer(modifier = Modifier.height(6.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Button(
        onClick = onSaveClick,
        colors = ButtonDefaults.buttonColors(containerColor = StudioPanelElevated),
        modifier = Modifier
          .weight(1f)
          .height(34.dp)
          .border(1.dp, StudioBorder, RoundedCornerShape(6.dp))
          .testTag("save_button")
      ) {
        Text("Save", color = TextPrimary, fontSize = 11.sp)
      }

      Button(
        onClick = onLoadClick,
        colors = ButtonDefaults.buttonColors(containerColor = StudioPanelElevated),
        modifier = Modifier
          .weight(1f)
          .height(34.dp)
          .border(1.dp, StudioBorder, RoundedCornerShape(6.dp))
          .testTag("load_button")
      ) {
        Text("Load", color = TextPrimary, fontSize = 11.sp)
      }
    }
  }
}

@Composable
fun AddLayerButton(
  title: String,
  icon: ImageVector,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(6.dp))
      .background(StudioPanelElevated)
      .border(1.dp, StudioBorder, RoundedCornerShape(6.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 8.dp, vertical = 6.dp)
      .testTag("add_layer_${title.lowercase().replace(" ", "_")}")
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Icon(
        imageVector = icon,
        contentDescription = title,
        tint = AccentGreen,
        modifier = Modifier.size(14.dp)
      )
      Text(
        text = title,
        color = TextPrimary,
        fontSize = 11.sp
      )
    }
  }
}
