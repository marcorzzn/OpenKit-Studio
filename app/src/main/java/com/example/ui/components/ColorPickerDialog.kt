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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioPanelBg
import com.example.ui.theme.StudioPanelElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

val ClassicKitColors = listOf(
  0xFF000000L to "Pitch Black",
  0xFF14171EL to "Carbon Dark",
  0xFFFFFFFFL to "Pure White",
  0xFF22C55EL to "Neon Green",
  0xFF15803DL to "Emerald Green",
  0xFF0284C7L to "Sky Blue",
  0xFF1E3A8AL to "Royal Blue",
  0xFF0244B2L to "Nerazzurri Blue",
  0xFF85142AL to "Blaugrana Garnet",
  0xFFDC2626L to "Rossoneri Red",
  0xFFEF4444L to "Bright Crimson",
  0xFFEAB308L to "Galácticos Gold",
  0xFFF59E0BL to "Amber Gold",
  0xFFF97316L to "Valencia Orange",
  0xFF7C3AEDL to "Cyber Violet",
  0xFFEC4899L to "Pink Striker",
  0xFF64748BL to "Steel Silver"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorPickerDialog(
  initialColor: Long,
  title: String = "Select Color",
  onColorSelected: (Long) -> Unit,
  onDismiss: () -> Unit
) {
  var selectedColor by remember { mutableStateOf(initialColor) }
  var hexInput by remember {
    val hex = String.format("%06X", (initialColor and 0x00FFFFFFL))
    mutableStateOf("#$hex")
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = StudioPanelBg,
    title = {
      Text(
        text = title,
        color = TextPrimary,
        fontSize = 18.sp
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Preview Box
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Box(
            modifier = Modifier
              .size(48.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(Color(selectedColor))
              .border(2.dp, StudioBorder, RoundedCornerShape(8.dp))
          )
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Current: " + String.format("#%06X", (selectedColor and 0x00FFFFFFL)),
              color = TextPrimary,
              fontSize = 14.sp
            )
            Text(
              text = "Tap a preset or type HEX",
              color = TextSecondary,
              fontSize = 12.sp
            )
          }
        }

        // Preset Swatches
        Text(
          text = "Kit Palette Presets",
          color = TextSecondary,
          fontSize = 13.sp
        )

        FlowRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          ClassicKitColors.forEach { (colorVal, _) ->
            val isSelected = (selectedColor and 0xFFFFFFFFL) == (colorVal and 0xFFFFFFFFL)
            Box(
              modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color(colorVal))
                .border(
                  width = if (isSelected) 2.5.dp else 1.dp,
                  color = if (isSelected) AccentGreen else StudioBorder,
                  shape = CircleShape
                )
                .clickable {
                  selectedColor = colorVal
                  val hex = String.format("%06X", (colorVal and 0x00FFFFFFL))
                  hexInput = "#$hex"
                },
              contentAlignment = Alignment.Center
            ) {
              if (isSelected) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = "Selected",
                  tint = if (Color(colorVal).red > 0.6f && Color(colorVal).green > 0.6f) Color.Black else Color.White,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }
        }

        // HEX input field
        OutlinedTextField(
          value = hexInput,
          onValueChange = { input ->
            hexInput = input
            val cleanHex = input.removePrefix("#").trim()
            if (cleanHex.length == 6) {
              try {
                val parsed = java.lang.Long.parseLong("FF$cleanHex", 16)
                selectedColor = parsed
              } catch (_: Exception) {}
            }
          },
          label = { Text("HEX Code") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("hex_color_input"),
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
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onColorSelected(selectedColor)
          onDismiss()
        },
        colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
        modifier = Modifier.testTag("color_picker_confirm")
      ) {
        Text("Apply Color", color = Color(0xFF052E16))
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = TextSecondary)
      }
    }
  )
}
