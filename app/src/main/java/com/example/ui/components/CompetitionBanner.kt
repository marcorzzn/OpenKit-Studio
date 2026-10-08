package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioPanelElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CompetitionBanner(
  onVoteClicked: () -> Unit
) {
  var isDismissed by remember { mutableStateOf(false) }

  if (isDismissed) return

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(38.dp)
      .background(Color(0xFF191B21))
      .border(1.dp, StudioBorder)
      .padding(horizontal = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.weight(1f)
    ) {
      Box(
        modifier = Modifier
          .size(24.dp)
          .clip(CircleShape)
          .background(Color(0xFF3730A3)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.EmojiEvents,
          contentDescription = "Trophy",
          tint = Color(0xFFFDE047),
          modifier = Modifier.size(14.dp)
        )
      }

      Text(
        text = "OpenKit Community Cup • Round of 16 Kits",
        color = TextPrimary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        maxLines = 1
      )
    }

    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(AccentGreen)
          .clickable(onClick = onVoteClicked)
          .padding(horizontal = 10.dp, vertical = 3.dp)
          .testTag("vote_now_button")
      ) {
        Text(
          text = "Vote Now",
          color = Color(0xFF052E16),
          fontWeight = FontWeight.Bold,
          fontSize = 10.sp
        )
      }

      IconButton(
        onClick = { isDismissed = true },
        modifier = Modifier.size(20.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Close,
          contentDescription = "Dismiss Banner",
          tint = TextSecondary,
          modifier = Modifier.size(14.dp)
        )
      }
    }
  }
}
