package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.engine.PatternCategory
import com.example.engine.PatternItem
import com.example.engine.PatternLibrary
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioPanelBg
import com.example.ui.theme.StudioPanelElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PatternLibraryDialog(
  onPatternSelected: (PatternItem) -> Unit,
  onDismiss: () -> Unit
) {
  var selectedCategory by remember { mutableStateOf<PatternCategory?>(null) }
  var searchQuery by remember { mutableStateOf("") }
  var sortBy by remember { mutableStateOf("Popular") }
  var sortMenuExpanded by remember { mutableStateOf(false) }
  var currentPage by remember { mutableStateOf(1) }
  val itemsPerPage = 12

  val filteredPatterns = remember(selectedCategory, searchQuery, sortBy) {
    PatternLibrary.filter(selectedCategory, searchQuery, sortBy)
  }

  val totalPages = maxOf(1, (filteredPatterns.size + itemsPerPage - 1) / itemsPerPage)
  val pageItems = filteredPatterns.drop((currentPage - 1) * itemsPerPage).take(itemsPerPage)

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .height(580.dp)
        .clip(RoundedCornerShape(12.dp))
        .border(1.dp, StudioBorder, RoundedCornerShape(12.dp)),
      color = StudioPanelBg
    ) {
      Column(modifier = Modifier.fillMaxSize().padding(14.dp)) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Text("Pattern Library", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(Color(0x3322C55E))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text("ALL UNLOCKED", color = AccentGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
              }
            }
            Text("22 Procedural Categories • 100% Free & Open Source", color = TextSecondary, fontSize = 11.sp)
          }

          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary, modifier = Modifier.size(18.dp))
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal Category Tabs (22 categories)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          CategoryChip(
            label = "All Patterns",
            isSelected = selectedCategory == null,
            onClick = { selectedCategory = null; currentPage = 1 }
          )
          PatternCategory.values().forEach { cat ->
            CategoryChip(
              label = cat.label,
              isSelected = selectedCategory == cat,
              onClick = { selectedCategory = cat; currentPage = 1 }
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search & Sort Bar
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it; currentPage = 1 },
            placeholder = { Text("Filter patterns...", fontSize = 11.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = TextSecondary, modifier = Modifier.size(16.dp)) },
            modifier = Modifier.weight(1f).height(46.dp),
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

          // Sort dropdown
          Box {
            Box(
              modifier = Modifier
                .height(46.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(StudioPanelElevated)
                .border(1.dp, StudioBorder, RoundedCornerShape(6.dp))
                .clickable { sortMenuExpanded = true }
                .padding(horizontal = 10.dp),
              contentAlignment = Alignment.Center
            ) {
              Text("Sort: $sortBy", color = TextPrimary, fontSize = 11.sp)
            }
            DropdownMenu(
              expanded = sortMenuExpanded,
              onDismissRequest = { sortMenuExpanded = false },
              modifier = Modifier.background(StudioPanelElevated)
            ) {
              listOf("Popular", "Latest", "Name").forEach { s ->
                DropdownMenuItem(
                  text = { Text(s, color = TextPrimary, fontSize = 11.sp) },
                  onClick = { sortBy = s; sortMenuExpanded = false; currentPage = 1 }
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Pattern Grid (6 columns on tablet / 4 on mobile)
        LazyVerticalGrid(
          columns = GridCells.Adaptive(minSize = 90.dp),
          modifier = Modifier.weight(1f).fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(pageItems, key = { it.id }) { item ->
            val thumbnail = remember(item.id) { PatternLibrary.generateThumbnail(item) }
            Column(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(StudioPanelElevated)
                .border(1.dp, StudioBorder, RoundedCornerShape(8.dp))
                .clickable {
                  onPatternSelected(item)
                  onDismiss()
                }
                .padding(6.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Image(
                bitmap = thumbnail.asImageBitmap(),
                contentDescription = item.name,
                modifier = Modifier
                  .fillMaxWidth()
                  .aspectRatio(1f)
                  .clip(RoundedCornerShape(4.dp))
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = item.name,
                color = TextPrimary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Footer Pagination
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Showing ${(currentPage - 1) * itemsPerPage + 1} - ${minOf(currentPage * itemsPerPage, filteredPatterns.size)} of ${filteredPatterns.size}", color = TextSecondary, fontSize = 10.sp)

          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            IconButton(
              onClick = { if (currentPage > 1) currentPage-- },
              enabled = currentPage > 1,
              modifier = Modifier.size(28.dp)
            ) {
              Icon(Icons.Default.ChevronLeft, contentDescription = "Prev", tint = if (currentPage > 1) TextPrimary else TextSecondary.copy(alpha = 0.3f))
            }

            Text("$currentPage / $totalPages", color = AccentGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)

            IconButton(
              onClick = { if (currentPage < totalPages) currentPage++ },
              enabled = currentPage < totalPages,
              modifier = Modifier.size(28.dp)
            ) {
              Icon(Icons.Default.ChevronRight, contentDescription = "Next", tint = if (currentPage < totalPages) TextPrimary else TextSecondary.copy(alpha = 0.3f))
            }
          }
        }
      }
    }
  }
}

@Composable
fun CategoryChip(
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(16.dp))
      .background(if (isSelected) AccentGreen else StudioPanelElevated)
      .border(1.dp, if (isSelected) AccentGreen else StudioBorder, RoundedCornerShape(16.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 10.dp, vertical = 5.dp)
  ) {
    Text(
      text = label,
      color = if (isSelected) Color(0xFF052E16) else TextPrimary,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
      fontSize = 11.sp
    )
  }
}
