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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PresetsData
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioPanelBg
import com.example.ui.theme.StudioPanelElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun KitVisionDialog(
  onApplyPreset: (String) -> Unit,
  onDismiss: () -> Unit
) {
  var promptInput by remember { mutableStateOf("Neon futuristic soccer jersey with geometric hex pattern and gold shield crest") }
  var selectedStyle by remember { mutableStateOf("Cyber Neon Kit") }

  val aiStyles = listOf(
    "Cyber Neon Kit" to "Futuristic dark carbon with vibrant neon green accents",
    "Nerazzurri Stripes" to "Iconic Milano black & electric blue vertical stripes",
    "Blaugrana Classic" to "Garnet and deep royal blue heritage stripes",
    "Rossoneri Strike" to "Black and crimson red dynamic speed stripes",
    "Galácticos Pure White" to "Crisp royal white with pristine gold trims",
    "Celeste Sash" to "Sky blue body with bold diagonal white sash",
    "Bianconeri Bold" to "Classic monochrome zebra stripes with gold stars",
    "Azzurri Renaissance" to "Mediterranean royal blue with intricate gold pattern"
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = StudioPanelBg,
    title = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Brush.horizontalGradient(listOf(Color(0xFF15803D), Color(0xFF22C55E)))),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.AutoAwesome, contentDescription = "Kit Vision", tint = Color.White, modifier = Modifier.size(18.dp))
        }

        Column {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Kit Vision AI Styler", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0x3322C55E))
                .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
              Text("OPENKIT", color = AccentGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
          }
          Text("One-Click Procedural Kit Generator", color = TextSecondary, fontSize = 11.sp)
        }
      }
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        OutlinedTextField(
          value = promptInput,
          onValueChange = { promptInput = it },
          label = { Text("AI Style Prompt") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("kit_vision_prompt_input"),
          maxLines = 3,
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = StudioPanelElevated,
            unfocusedContainerColor = StudioPanelElevated,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedBorderColor = AccentGreen,
            unfocusedBorderColor = StudioBorder
          )
        )

        Text("Select One-Click Generator Style:", color = TextSecondary, fontSize = 12.sp)

        FlowRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          aiStyles.forEach { (styleName, desc) ->
            val isSelected = selectedStyle == styleName
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSelected) Color(0x3322C55E) else StudioPanelElevated)
                .border(
                  width = if (isSelected) 1.5.dp else 1.dp,
                  color = if (isSelected) AccentGreen else StudioBorder,
                  shape = RoundedCornerShape(6.dp)
                )
                .clickable {
                  selectedStyle = styleName
                  promptInput = desc
                }
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                if (isSelected) {
                  Icon(Icons.Default.Check, contentDescription = "Selected", tint = AccentGreen, modifier = Modifier.size(12.dp))
                }
                Text(
                  text = styleName,
                  color = if (isSelected) AccentGreen else TextPrimary,
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onApplyPreset(selectedStyle)
          onDismiss()
        },
        colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
        modifier = Modifier.testTag("apply_kit_vision_button")
      ) {
        Icon(Icons.Default.AutoAwesome, contentDescription = "Apply", tint = Color(0xFF052E16), modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.size(4.dp))
        Text("Generate Kit", color = Color(0xFF052E16), fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = TextSecondary)
      }
    }
  )
}
