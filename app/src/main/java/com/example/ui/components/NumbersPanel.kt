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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Shield
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
import com.example.model.CompetitionBadge
import com.example.model.KitProject
import com.example.model.LayerPlacement
import com.example.model.NumberConfig
import com.example.model.PlayerNameConfig
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
fun NumbersPanel(
  project: KitProject,
  onUpdateNumbers: (NumberConfig) -> Unit,
  onUpdatePlayerName: (PlayerNameConfig) -> Unit,
  onUpdateBadges: (List<CompetitionBadge>) -> Unit,
  onClose: () -> Unit,
  modifier: Modifier = Modifier
) {
  val numbers = project.numbers
  val playerName = project.playerName

  var colorPickerTarget by remember { mutableStateOf<String?>(null) }
  var isNumberStyleExpanded by remember { mutableStateOf(true) }
  var isPlayerNameExpanded by remember { mutableStateOf(true) }
  var isFrontNumberExpanded by remember { mutableStateOf(false) }
  var isShortsNumberExpanded by remember { mutableStateOf(false) }
  var isBadgesExpanded by remember { mutableStateOf(true) }

  Column(
    modifier = modifier
      .fillMaxHeight()
      .background(StudioPanelBg)
      .border(1.dp, StudioBorder)
      .padding(10.dp)
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text("Squad Numbers & Badges", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text("Official Athletic Typography & Badges", color = TextSecondary, fontSize = 10.sp)
      }
      IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary, modifier = Modifier.size(16.dp))
      }
    }

    HorizontalDivider(color = StudioBorder)

    // 1. Number Style (Globale)
    CollapsibleSection(
      title = "Number Style (Global)",
      isExpanded = isNumberStyleExpanded,
      onToggle = { isNumberStyleExpanded = !isNumberStyleExpanded }
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Enable Numbering", color = TextPrimary, fontSize = 11.sp)
          Switch(
            checked = numbers.enabled,
            onCheckedChange = { onUpdateNumbers(numbers.copy(enabled = it)) },
            colors = SwitchDefaults.colors(checkedThumbColor = AccentGreen, checkedTrackColor = Color(0xFF14532D))
          )
        }

        // Squad Number Input
        OutlinedTextField(
          value = numbers.value,
          onValueChange = { onUpdateNumbers(numbers.copy(value = it)) },
          label = { Text("Squad Number", fontSize = 10.sp) },
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

        // Font selector
        var fontMenuExpanded by remember { mutableStateOf(false) }
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Font Family:", color = TextSecondary, fontSize = 11.sp)
          Box {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(StudioPanelElevated)
                .border(1.dp, StudioBorder, RoundedCornerShape(4.dp))
                .clickable { fontMenuExpanded = true }
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(numbers.font.label, color = AccentGreen, fontSize = 11.sp)
            }
            DropdownMenu(
              expanded = fontMenuExpanded,
              onDismissRequest = { fontMenuExpanded = false },
              modifier = Modifier.background(StudioPanelElevated)
            ) {
              TextFont.values().forEach { f ->
                DropdownMenuItem(
                  text = { Text(f.label, color = TextPrimary, fontSize = 11.sp) },
                  onClick = {
                    onUpdateNumbers(numbers.copy(font = f))
                    onUpdatePlayerName(playerName.copy(font = f))
                    fontMenuExpanded = false
                  }
                )
              }
            }
          }
        }

        // Color & Border Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
              modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color(numbers.color))
                .border(1.dp, StudioBorder, CircleShape)
                .clickable { colorPickerTarget = "number_color" }
            )
            Text("Fill Color", color = TextPrimary, fontSize = 11.sp)
          }

          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
              modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color(numbers.borderColor))
                .border(1.dp, StudioBorder, CircleShape)
                .clickable { colorPickerTarget = "number_border" }
            )
            Text("Border Color", color = TextPrimary, fontSize = 11.sp)
          }
        }

        // Border Width Slider
        Column {
          Text("Border Outline Width: ${numbers.borderWidth.roundToInt()}px", color = TextSecondary, fontSize = 10.sp)
          Slider(
            value = numbers.borderWidth,
            onValueChange = { onUpdateNumbers(numbers.copy(borderWidth = it, borderEnabled = it > 0f)) },
            valueRange = 0f..10f,
            colors = SliderDefaults.colors(thumbColor = AccentGreen, activeTrackColor = AccentGreen)
          )
        }

        // Size & Offset Slider
        Column {
          Text("Number Size: ${numbers.size.roundToInt()}", color = TextSecondary, fontSize = 10.sp)
          Slider(
            value = numbers.size,
            onValueChange = { onUpdateNumbers(numbers.copy(size = it)) },
            valueRange = 20f..80f,
            colors = SliderDefaults.colors(thumbColor = AccentGreen, activeTrackColor = AccentGreen)
          )
        }

        Column {
          Text("Vertical Offset: ${numbers.offset.roundToInt()}", color = TextSecondary, fontSize = 10.sp)
          Slider(
            value = numbers.offset,
            onValueChange = { onUpdateNumbers(numbers.copy(offset = it)) },
            valueRange = -50f..50f,
            colors = SliderDefaults.colors(thumbColor = AccentGreen, activeTrackColor = AccentGreen)
          )
        }
      }
    }

    HorizontalDivider(color = StudioBorder)

    // 2. Player Name Panel
    CollapsibleSection(
      title = "Player Name (Back)",
      isExpanded = isPlayerNameExpanded,
      onToggle = { isPlayerNameExpanded = !isPlayerNameExpanded }
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Show Player Name", color = TextPrimary, fontSize = 11.sp)
          Switch(
            checked = playerName.enabled,
            onCheckedChange = { onUpdatePlayerName(playerName.copy(enabled = it)) },
            colors = SwitchDefaults.colors(checkedThumbColor = AccentGreen, checkedTrackColor = Color(0xFF14532D))
          )
        }

        OutlinedTextField(
          value = playerName.name,
          onValueChange = { onUpdatePlayerName(playerName.copy(name = it)) },
          label = { Text("Name", fontSize = 10.sp) },
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

        // Arc Curvature Slider
        Column {
          Text("Curved Name Arch: ${playerName.arc.roundToInt()}%", color = TextSecondary, fontSize = 10.sp)
          Slider(
            value = playerName.arc,
            onValueChange = { onUpdatePlayerName(playerName.copy(arc = it)) },
            valueRange = -50f..50f,
            colors = SliderDefaults.colors(thumbColor = AccentGreen, activeTrackColor = AccentGreen)
          )
        }

        // Live Font Preview
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF0F1117))
            .border(1.dp, StudioBorder, RoundedCornerShape(6.dp)),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = playerName.name.ifBlank { "PLAYER" },
            color = Color(playerName.color),
            fontSize = 20.sp,
            fontWeight = FontWeight.Black
          )
        }
      }
    }

    HorizontalDivider(color = StudioBorder)

    // 3. Front & Shorts Number Toggles
    CollapsibleSection(
      title = "Front & Shorts Numbers",
      isExpanded = isFrontNumberExpanded,
      onToggle = { isFrontNumberExpanded = !isFrontNumberExpanded }
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Chest Front Number", color = TextPrimary, fontSize = 11.sp)
          Switch(
            checked = numbers.frontNumberEnabled,
            onCheckedChange = { onUpdateNumbers(numbers.copy(frontNumberEnabled = it)) },
            colors = SwitchDefaults.colors(checkedThumbColor = AccentGreen, checkedTrackColor = Color(0xFF14532D))
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Shorts Squad Number", color = TextPrimary, fontSize = 11.sp)
          Switch(
            checked = numbers.shortsNumberEnabled,
            onCheckedChange = { onUpdateNumbers(numbers.copy(shortsNumberEnabled = it)) },
            colors = SwitchDefaults.colors(checkedThumbColor = AccentGreen, checkedTrackColor = Color(0xFF14532D))
          )
        }
      }
    }

    HorizontalDivider(color = StudioBorder)

    // 4. Competition Badges (3 Free Slots)
    CollapsibleSection(
      title = "Competition Badges (Free Positioning)",
      isExpanded = isBadgesExpanded,
      onToggle = { isBadgesExpanded = !isBadgesExpanded }
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        project.badges.forEachIndexed { i, badge ->
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(6.dp))
              .background(StudioPanelElevated)
              .border(1.dp, StudioBorder, RoundedCornerShape(6.dp))
              .padding(8.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  Icon(Icons.Default.Shield, contentDescription = "Badge", tint = AccentGold, modifier = Modifier.size(16.dp))
                  Text(badge.name, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
                Switch(
                  checked = badge.enabled,
                  onCheckedChange = { active ->
                    val updated = project.badges.toMutableList()
                    updated[i] = badge.copy(enabled = active)
                    onUpdateBadges(updated)
                  },
                  colors = SwitchDefaults.colors(checkedThumbColor = AccentGreen, checkedTrackColor = Color(0xFF14532D))
                )
              }

              if (badge.enabled) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text("X: ${badge.posX.roundToInt()}%", color = TextSecondary, fontSize = 10.sp)
                    Slider(
                      value = badge.posX,
                      onValueChange = { px ->
                        val updated = project.badges.toMutableList()
                        updated[i] = badge.copy(posX = px)
                        onUpdateBadges(updated)
                      },
                      valueRange = -80f..80f,
                      colors = SliderDefaults.colors(thumbColor = AccentGreen, activeTrackColor = AccentGreen)
                    )
                  }
                  Column(modifier = Modifier.weight(1f)) {
                    Text("Y: ${badge.posY.roundToInt()}%", color = TextSecondary, fontSize = 10.sp)
                    Slider(
                      value = badge.posY,
                      onValueChange = { py ->
                        val updated = project.badges.toMutableList()
                        updated[i] = badge.copy(posY = py)
                        onUpdateBadges(updated)
                      },
                      valueRange = -80f..80f,
                      colors = SliderDefaults.colors(thumbColor = AccentGreen, activeTrackColor = AccentGreen)
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  // Color picker dialog
  if (colorPickerTarget != null) {
    val initialColor = if (colorPickerTarget == "number_color") numbers.color else numbers.borderColor
    ColorPickerDialog(
      initialColor = initialColor,
      title = "Select Number Color",
      onColorSelected = { col ->
        if (colorPickerTarget == "number_color") {
          onUpdateNumbers(numbers.copy(color = col))
          onUpdatePlayerName(playerName.copy(color = col))
        } else {
          onUpdateNumbers(numbers.copy(borderColor = col))
        }
        colorPickerTarget = null
      },
      onDismiss = { colorPickerTarget = null }
    )
  }
}
