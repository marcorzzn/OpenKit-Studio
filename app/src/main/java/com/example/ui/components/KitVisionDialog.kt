package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioPanelBg
import com.example.ui.theme.StudioPanelElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class KitVisionTab(val label: String) {
  GENERATE("Generate"),
  KIT_GALLERY("Kit Gallery"),
  HISTORY("History"),
  TOKENS("Tokens")
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun KitVisionDialog(
  onApplyPreset: (String) -> Unit,
  onDismiss: () -> Unit
) {
  var activeTab by remember { mutableStateOf(KitVisionTab.GENERATE) }
  var blueprintType by remember { mutableStateOf("Preset") }
  var selectedBlueprint by remember { mutableStateOf("Match Photo") }
  var additionalContext by remember { mutableStateOf("") }
  var selectedModel by remember { mutableStateOf("Nano Banana Pro (Google)") }
  var selectedResolution by remember { mutableStateOf("1024×1024") }
  var modelMenuOpen by remember { mutableStateOf(false) }
  var resMenuOpen by remember { mutableStateOf(false) }

  val blueprints = listOf(
    "Match Photo" to "Player wearing the kit in a live high-intensity match action shot",
    "Studio Showcase" to "Clean dramatic studio lighting with mannequin pedestal display",
    "Stadium Wide" to "Wide angle floodlights with filled stadium grandstand backdrop",
    "Close-up Detail" to "Extreme close-up macro texture showing fabric knit and embroidery badge",
    "Locker Room" to "Authentic dressing room hanger presentation with boots and kit bag"
  )

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .fillMaxHeight(0.85f)
        .clip(RoundedCornerShape(14.dp))
        .border(1.dp, StudioBorder, RoundedCornerShape(14.dp)),
      color = StudioPanelBg
    ) {
      Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Top Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
              Text("Kit Vision AI Studio", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
              Text("Photorealistic AI Render Generator", color = TextSecondary, fontSize = 11.sp)
            }
          }

          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Token badge
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x33EF4444))
                .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Text("● Unlimited Free (BYOK)", color = AccentGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
              Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary, modifier = Modifier.size(16.dp))
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal Tabs
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          KitVisionTab.values().forEach { tab ->
            val isActive = activeTab == tab
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (isActive) Color(0x3322C55E) else StudioPanelElevated)
                .border(1.dp, if (isActive) AccentGreen else StudioBorder, RoundedCornerShape(6.dp))
                .clickable { activeTab = tab }
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Text(
                text = tab.label,
                color = if (isActive) AccentGreen else TextPrimary,
                fontSize = 11.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (activeTab) {
          KitVisionTab.GENERATE -> {
            Column(
              modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              // Info Banner
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFF1E2638))
                  .border(1.dp, Color(0xFF2E3E60), RoundedCornerShape(8.dp))
                  .padding(10.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  Icon(Icons.Default.Info, contentDescription = "Info", tint = Color(0xFF60A5FA), modifier = Modifier.size(16.dp))
                  Text(
                    "Kit Vision AI renders photorealistic shots of your kit based on current layers and 3D textures.",
                    color = Color(0xFFBFDBFE),
                    fontSize = 10.sp
                  )
                }
              }

              // BLUEPRINT Selection
              Text("BLUEPRINT PRESET", color = AccentGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
              Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                blueprints.forEach { (title, desc) ->
                  val isSelected = selectedBlueprint == title
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(8.dp))
                      .background(if (isSelected) Color(0x2222C55E) else StudioPanelElevated)
                      .border(1.dp, if (isSelected) AccentGreen else StudioBorder, RoundedCornerShape(8.dp))
                      .clickable { selectedBlueprint = title }
                      .padding(10.dp)
                  ) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column(modifier = Modifier.weight(1f)) {
                        Text(title, color = if (isSelected) AccentGreen else TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(desc, color = TextSecondary, fontSize = 10.sp)
                      }
                      if (isSelected) {
                        Icon(Icons.Default.Check, contentDescription = "Active", tint = AccentGreen, modifier = Modifier.size(16.dp))
                      }
                    }
                  }
                }
              }

              // ADDITIONAL CONTEXT
              Text("ADDITIONAL CONTEXT (OPTIONAL)", color = AccentGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
              OutlinedTextField(
                value = additionalContext,
                onValueChange = { if (it.length <= 500) additionalContext = it },
                placeholder = { Text("e.g. rainy day night match, dramatic floodlights, retro 90s film grain...", fontSize = 11.sp) },
                modifier = Modifier.fillMaxWidth(),
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
              Text("${additionalContext.length} / 500 characters", color = TextSecondary, fontSize = 9.sp, modifier = Modifier.align(Alignment.End))

              // MODEL & RESOLUTION
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                // Model dropdown
                Column(modifier = Modifier.weight(1f)) {
                  Text("AI MODEL", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                  Box {
                    Box(
                      modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(StudioPanelElevated)
                        .border(1.dp, StudioBorder, RoundedCornerShape(6.dp))
                        .clickable { modelMenuOpen = true }
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                      Text(selectedModel, color = TextPrimary, fontSize = 11.sp, maxLines = 1)
                    }
                    DropdownMenu(expanded = modelMenuOpen, onDismissRequest = { modelMenuOpen = false }) {
                      listOf("Nano Banana Pro (Google)", "Imagen 3 Pro", "Gemini 2.5 Flash Image").forEach { m ->
                        DropdownMenuItem(
                          text = { Text(m, color = TextPrimary, fontSize = 11.sp) },
                          onClick = { selectedModel = m; modelMenuOpen = false }
                        )
                      }
                    }
                  }
                }

                // Resolution dropdown
                Column(modifier = Modifier.weight(1f)) {
                  Text("RESOLUTION", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                  Box {
                    Box(
                      modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(StudioPanelElevated)
                        .border(1.dp, StudioBorder, RoundedCornerShape(6.dp))
                        .clickable { resMenuOpen = true }
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                      Text(selectedResolution, color = TextPrimary, fontSize = 11.sp)
                    }
                    DropdownMenu(expanded = resMenuOpen, onDismissRequest = { resMenuOpen = false }) {
                      listOf("512×512", "1024×1024", "2048×2048").forEach { r ->
                        DropdownMenuItem(
                          text = { Text(r, color = TextPrimary, fontSize = 11.sp) },
                          onClick = { selectedResolution = r; resMenuOpen = false }
                        )
                      }
                    }
                  }
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Primary Generate Button
              Button(
                onClick = {
                  onApplyPreset("Cyber Neon Kit")
                  onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(44.dp)
                  .testTag("apply_kit_vision_button")
              ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = "Generate", tint = Color(0xFF052E16), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Generate AI Kit Render", color = Color(0xFF052E16), fontWeight = FontWeight.Bold, fontSize = 12.sp)
              }
            }
          }

          KitVisionTab.KIT_GALLERY -> {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
              Text("Community Kit Gallery Showcase (Fase 8)", color = TextSecondary, fontSize = 12.sp)
            }
          }

          KitVisionTab.HISTORY -> {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
              Text("Your generated renders will appear here.", color = TextSecondary, fontSize = 12.sp)
            }
          }

          KitVisionTab.TOKENS -> {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("OpenKit Studio is 100% Free & Open Source", color = AccentGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("No paywalls • Bring Your Own API Key (BYOK)", color = TextSecondary, fontSize = 11.sp)
              }
            }
          }
        }
      }
    }
  }
}
