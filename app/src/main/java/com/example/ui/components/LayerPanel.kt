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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.model.KitComponentType
import com.example.model.KitLayer
import com.example.model.LayerType
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioPanelBg
import com.example.ui.theme.StudioPanelElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LayerPanel(
  selectedComponent: KitComponentType,
  layers: List<KitLayer>,
  selectedLayerId: String?,
  onSelectLayer: (String) -> Unit,
  onAddLayer: (LayerType) -> Unit,
  onDeleteLayer: (String) -> Unit,
  onToggleVisibility: (String) -> Unit,
  onToggleLock: (String) -> Unit,
  onMoveUp: (String) -> Unit,
  onMoveDown: (String) -> Unit,
  onKitVisionClick: () -> Unit,
  onSaveClick: () -> Unit,
  onLoadClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxHeight()
      .background(StudioPanelBg)
      .border(1.dp, StudioBorder)
      .padding(8.dp)
  ) {
    // 1. "Kit Vision" Top Card (green gradient)
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

    Spacer(modifier = Modifier.height(10.dp))

    // Component Title & Layer Count
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
        text = "${layers.size} total",
        color = TextSecondary,
        fontSize = 11.sp
      )
    }

    Spacer(modifier = Modifier.height(4.dp))

    // 2. Layer List (Scrollable Stack)
    LazyColumn(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      itemsIndexed(layers, key = { _, layer -> layer.id }) { index, layer ->
        val isSelected = layer.id == selectedLayerId

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
            .clickable { onSelectLayer(layer.id) }
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .testTag("layer_item_${layer.id}")
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Drag / Reorder Buttons
            Column {
              if (index > 0) {
                Icon(
                  imageVector = Icons.Default.KeyboardArrowUp,
                  contentDescription = "Move Up",
                  tint = TextSecondary,
                  modifier = Modifier
                    .size(14.dp)
                    .clickable { onMoveUp(layer.id) }
                )
              }
              if (index < layers.size - 1) {
                Icon(
                  imageVector = Icons.Default.KeyboardArrowDown,
                  contentDescription = "Move Down",
                  tint = TextSecondary,
                  modifier = Modifier
                    .size(14.dp)
                    .clickable { onMoveDown(layer.id) }
                )
              }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Eye Visibility Toggle
            IconButton(
              onClick = { onToggleVisibility(layer.id) },
              modifier = Modifier.size(24.dp)
            ) {
              Icon(
                imageVector = if (layer.visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                contentDescription = "Toggle Visibility",
                tint = if (layer.visible) AccentGreen else TextSecondary.copy(alpha = 0.4f),
                modifier = Modifier.size(14.dp)
              )
            }

            // Lock Toggle
            IconButton(
              onClick = { onToggleLock(layer.id) },
              modifier = Modifier.size(24.dp)
            ) {
              Icon(
                imageVector = if (layer.locked) Icons.Default.Lock else Icons.Default.LockOpen,
                contentDescription = "Toggle Lock",
                tint = if (layer.locked) Color(0xFFF59E0B) else TextSecondary.copy(alpha = 0.4f),
                modifier = Modifier.size(14.dp)
              )
            }

            // Color Chip preview
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
            IconButton(
              onClick = { onDeleteLayer(layer.id) },
              modifier = Modifier.size(24.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Delete Layer",
                tint = TextSecondary,
                modifier = Modifier.size(14.dp)
              )
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = StudioBorder)
        Spacer(modifier = Modifier.height(8.dp))

        // 3. Section "ADD LAYER" with 9 buttons matching Master Prompt
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
              onClick = { onAddLayer(type) }
            )
          }
        }
      }
    }

    // 4. Bottom Footer (Save / Load)
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
