package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.KitComponentType
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioPanelBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ComponentIconRail(
  selectedComponent: KitComponentType,
  onComponentSelected: (KitComponentType) -> Unit,
  onOpenSettings: () -> Unit,
  onOpenMaterials: () -> Unit,
  onOpenKitVision: () -> Unit
) {
  Column(
    modifier = Modifier
      .width(52.dp)
      .fillMaxHeight()
      .background(StudioPanelBg)
      .border(1.dp, StudioBorder)
      .padding(vertical = 8.dp)
      .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // ⚙️ Settings
    RailIconButton(
      icon = { Icon(Icons.Default.Settings, contentDescription = "Settings", modifier = Modifier.size(20.dp)) },
      isActive = false,
      onClick = onOpenSettings,
      tag = "rail_settings"
    )

    // 👕 Shirt (Main)
    RailIconButton(
      icon = { Icon(Icons.Default.Checkroom, contentDescription = "Shirt", modifier = Modifier.size(20.dp)) },
      isActive = selectedComponent == KitComponentType.SHIRT,
      onClick = { onComponentSelected(KitComponentType.SHIRT) },
      tag = "rail_shirt"
    )

    // 🩳 Shorts
    RailIconButton(
      icon = {
        Text("🩳", fontSize = 18.sp)
      },
      isActive = selectedComponent == KitComponentType.SHORTS,
      onClick = { onComponentSelected(KitComponentType.SHORTS) },
      tag = "rail_shorts"
    )

    // 🧦 Socks
    RailIconButton(
      icon = {
        Text("🧦", fontSize = 18.sp)
      },
      isActive = selectedComponent == KitComponentType.SOCKS,
      onClick = { onComponentSelected(KitComponentType.SOCKS) },
      tag = "rail_socks"
    )

    // 🎗️ Armband
    RailIconButton(
      icon = {
        Text("🎗️", fontSize = 18.sp)
      },
      isActive = selectedComponent == KitComponentType.ARMBAND,
      onClick = { onComponentSelected(KitComponentType.ARMBAND) },
      tag = "rail_armband"
    )

    // 🛡️ Crest Badges
    RailIconButton(
      icon = { Icon(Icons.Default.Shield, contentDescription = "Crests", modifier = Modifier.size(20.dp)) },
      isActive = false,
      onClick = { onComponentSelected(KitComponentType.SHIRT) },
      tag = "rail_crests"
    )

    // `10` Squad Numbers
    RailIconButton(
      icon = {
        Text(
          text = "10",
          fontWeight = FontWeight.Black,
          fontSize = 14.sp
        )
      },
      isActive = false,
      onClick = { onComponentSelected(KitComponentType.SHIRT) },
      tag = "rail_numbers"
    )

    // 🎚️ Sliders (Materials / Shininess)
    RailIconButton(
      icon = { Icon(Icons.Default.Tune, contentDescription = "Materials & Shininess", modifier = Modifier.size(20.dp)) },
      isActive = false,
      onClick = onOpenMaterials,
      tag = "rail_tune"
    )

    // ✨ Kit Vision
    RailIconButton(
      icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Kit Vision", modifier = Modifier.size(20.dp)) },
      isActive = false,
      onClick = onOpenKitVision,
      tag = "rail_kit_vision"
    )

    Spacer(modifier = Modifier.weight(1f))

    // ☰ Menu
    RailIconButton(
      icon = { Icon(Icons.Default.Menu, contentDescription = "Menu", modifier = Modifier.size(20.dp)) },
      isActive = false,
      onClick = onOpenSettings,
      tag = "rail_menu"
    )
  }
}

@Composable
fun RailIconButton(
  icon: @Composable () -> Unit,
  isActive: Boolean,
  onClick: () -> Unit,
  tag: String
) {
  Box(
    modifier = Modifier
      .size(40.dp)
      .clip(RoundedCornerShape(8.dp))
      .background(if (isActive) AccentGreen else Color.Transparent)
      .clickable(onClick = onClick)
      .testTag(tag),
    contentAlignment = Alignment.Center
  ) {
    androidx.compose.runtime.CompositionLocalProvider(
      androidx.compose.material3.LocalContentColor provides if (isActive) Color(0xFF052E16) else TextSecondary
    ) {
      icon()
    }
  }
}
