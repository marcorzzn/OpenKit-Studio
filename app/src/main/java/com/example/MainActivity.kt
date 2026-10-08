package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.KitComponentType
import com.example.model.KitLayer
import com.example.ui.components.CompetitionBanner
import com.example.ui.components.ComponentIconRail
import com.example.ui.components.KitVisionDialog
import com.example.ui.components.LayerInspector
import com.example.ui.components.LayerPanel
import com.example.ui.components.MinikitView
import com.example.ui.components.SettingsDialog
import com.example.ui.components.Texture2DView
import com.example.ui.components.TopNavBar
import com.example.ui.components.Viewport3D
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.StudioBg
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioPanelBg
import com.example.ui.theme.StudioPanelElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.KitEditorViewModel
import com.example.ui.viewmodel.StudioTab

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme(darkTheme = true) {
        val viewModel: KitEditorViewModel = viewModel()
        OpenKitStudioScreen(viewModel)
      }
    }
  }
}

enum class MobileSheetMode {
  NONE,
  LAYERS,
  INSPECTOR
}

@Composable
fun OpenKitStudioScreen(viewModel: KitEditorViewModel) {
  val project by viewModel.project.collectAsStateWithLifecycle()
  val selectedComponent by viewModel.selectedComponent.collectAsStateWithLifecycle()
  val selectedLayerId by viewModel.selectedLayerId.collectAsStateWithLifecycle()
  val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()

  val cameraYaw by viewModel.cameraYaw.collectAsStateWithLifecycle()
  val cameraPitch by viewModel.cameraPitch.collectAsStateWithLifecycle()
  val cameraZoom by viewModel.cameraZoom.collectAsStateWithLifecycle()
  val isWireframe by viewModel.isWireframe.collectAsStateWithLifecycle()
  val isAutoRotating by viewModel.isAutoRotating.collectAsStateWithLifecycle()
  val isLayerInspectorOpen by viewModel.isLayerInspectorOpen.collectAsStateWithLifecycle()

  val canUndo by viewModel.canUndo.collectAsStateWithLifecycle()
  val canRedo by viewModel.canRedo.collectAsStateWithLifecycle()
  val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

  val shirtBmp by viewModel.shirtTextureBitmap.collectAsStateWithLifecycle()
  val shortsBmp by viewModel.shortsTextureBitmap.collectAsStateWithLifecycle()
  val socksBmp by viewModel.socksTextureBitmap.collectAsStateWithLifecycle()

  val snackbarHostState = remember { SnackbarHostState() }
  LaunchedEffect(toastMessage) {
    toastMessage?.let {
      snackbarHostState.showSnackbar(it)
      viewModel.clearToast()
    }
  }

  var isKitVisionDialogOpen by remember { mutableStateOf(false) }
  var isSettingsDialogOpen by remember { mutableStateOf(false) }
  var mobileSheetMode by remember { mutableStateOf(MobileSheetMode.NONE) }

  val currentComponentLayers = project.getLayersFor(selectedComponent)
  val selectedLayer = currentComponentLayers.firstOrNull { it.id == selectedLayerId }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = StudioBg,
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      Column {
        TopNavBar(
          projectName = project.name,
          activeTab = activeTab,
          canUndo = canUndo,
          canRedo = canRedo,
          onTabSelected = { tab ->
            if (tab == StudioTab.KIT_VISION_AI) {
              isKitVisionDialogOpen = true
            } else {
              viewModel.setTab(tab)
            }
          },
          onUndo = viewModel::undo,
          onRedo = viewModel::redo,
          onRandomize = viewModel::randomizeColors,
          onSave = viewModel::saveProjectJson,
          onExport = viewModel::exportKitRender,
          onLoadPreset = viewModel::loadPreset
        )

        CompetitionBanner(
          onVoteClicked = { isKitVisionDialogOpen = true }
        )
      }
    }
  ) { innerPadding ->
    BoxWithConstraints(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      val isWideLayout = maxWidth >= 760.dp

      if (isWideLayout) {
        // Desktop / Tablet 4-Zone EAFC Layout
        Row(modifier = Modifier.fillMaxSize()) {
          // 1. Icon Rail
          ComponentIconRail(
            selectedComponent = selectedComponent,
            onComponentSelected = viewModel::selectComponent,
            onOpenSettings = { isSettingsDialogOpen = true },
            onOpenMaterials = { isSettingsDialogOpen = true },
            onOpenKitVision = { isKitVisionDialogOpen = true }
          )

          // 2. Left Layer Panel
          LayerPanel(
            selectedComponent = selectedComponent,
            layers = currentComponentLayers,
            selectedLayerId = selectedLayerId,
            onSelectLayer = viewModel::selectLayer,
            onAddLayer = viewModel::addLayer,
            onDeleteLayer = viewModel::deleteLayer,
            onToggleVisibility = viewModel::toggleLayerVisibility,
            onToggleLock = viewModel::toggleLayerLock,
            onMoveUp = viewModel::moveLayerUp,
            onMoveDown = viewModel::moveLayerDown,
            onKitVisionClick = { isKitVisionDialogOpen = true },
            onSaveClick = viewModel::saveProjectJson,
            onLoadClick = { isSettingsDialogOpen = true },
            modifier = Modifier.width(260.dp)
          )

          // 3. Center Viewport
          Box(modifier = Modifier.weight(1f)) {
            when (activeTab) {
              StudioTab.VIEW_3D -> {
                Viewport3D(
                  project = project,
                  selectedComponent = selectedComponent,
                  shirtBitmap = shirtBmp,
                  shortsBitmap = shortsBmp,
                  socksBitmap = socksBmp,
                  yaw = cameraYaw,
                  pitch = cameraPitch,
                  zoom = cameraZoom,
                  isWireframe = isWireframe,
                  isAutoRotating = isAutoRotating,
                  onOrbit = viewModel::updateCameraOrbit,
                  onZoom = viewModel::updateCameraZoom,
                  onSetCameraAngle = viewModel::setCameraAngle,
                  onResetCamera = viewModel::resetCamera,
                  onToggleWireframe = viewModel::toggleWireframe,
                  onToggleAutoRotate = viewModel::toggleAutoRotate,
                  onSelectComponent = viewModel::selectComponent,
                  onExportPng = viewModel::exportKitRender,
                  onExportTexture = viewModel::exportTextureFlat,
                  onMinikitClick = { viewModel.setTab(StudioTab.MINIKIT_CARD) }
                )
              }
              StudioTab.TEXTURE_2D -> {
                val currentBmp = when (selectedComponent) {
                  KitComponentType.SHIRT -> shirtBmp
                  KitComponentType.SHORTS -> shortsBmp
                  KitComponentType.SOCKS -> socksBmp
                  KitComponentType.ARMBAND -> shirtBmp
                }
                Texture2DView(
                  project = project,
                  selectedComponent = selectedComponent,
                  textureBitmap = currentBmp,
                  onExportFlatTexture = viewModel::exportTextureFlat
                )
              }
              StudioTab.MINIKIT_CARD -> {
                MinikitView(
                  project = project,
                  onExportMinikit = viewModel::exportKitRender
                )
              }
              StudioTab.KIT_VISION_AI -> {
                // Handled in modal dialog
              }
            }
          }

          // 4. Right Layer Inspector
          AnimatedVisibility(visible = isLayerInspectorOpen) {
            LayerInspector(
              layer = selectedLayer,
              onUpdateLayer = viewModel::updateSelectedLayer,
              onClose = viewModel::toggleInspector,
              modifier = Modifier.width(280.dp)
            )
          }
        }
      } else {
        // Mobile Handheld Vertical Layout
        Column(modifier = Modifier.fillMaxSize()) {
          // Center Viewport (Takes majority of screen)
          Box(modifier = Modifier.weight(1f)) {
            when (activeTab) {
              StudioTab.VIEW_3D -> {
                Viewport3D(
                  project = project,
                  selectedComponent = selectedComponent,
                  shirtBitmap = shirtBmp,
                  shortsBitmap = shortsBmp,
                  socksBitmap = socksBmp,
                  yaw = cameraYaw,
                  pitch = cameraPitch,
                  zoom = cameraZoom,
                  isWireframe = isWireframe,
                  isAutoRotating = isAutoRotating,
                  onOrbit = viewModel::updateCameraOrbit,
                  onZoom = viewModel::updateCameraZoom,
                  onSetCameraAngle = viewModel::setCameraAngle,
                  onResetCamera = viewModel::resetCamera,
                  onToggleWireframe = viewModel::toggleWireframe,
                  onToggleAutoRotate = viewModel::toggleAutoRotate,
                  onSelectComponent = viewModel::selectComponent,
                  onExportPng = viewModel::exportKitRender,
                  onExportTexture = viewModel::exportTextureFlat,
                  onMinikitClick = { viewModel.setTab(StudioTab.MINIKIT_CARD) }
                )
              }
              StudioTab.TEXTURE_2D -> {
                val currentBmp = when (selectedComponent) {
                  KitComponentType.SHIRT -> shirtBmp
                  KitComponentType.SHORTS -> shortsBmp
                  KitComponentType.SOCKS -> socksBmp
                  KitComponentType.ARMBAND -> shirtBmp
                }
                Texture2DView(
                  project = project,
                  selectedComponent = selectedComponent,
                  textureBitmap = currentBmp,
                  onExportFlatTexture = viewModel::exportTextureFlat
                )
              }
              StudioTab.MINIKIT_CARD -> {
                MinikitView(
                  project = project,
                  onExportMinikit = viewModel::exportKitRender
                )
              }
              StudioTab.KIT_VISION_AI -> {}
            }

            // Mobile Bottom Sheet Drawer if active
            if (mobileSheetMode != MobileSheetMode.NONE) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .fillMaxHeight(0.55f)
                  .align(Alignment.BottomCenter)
                  .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                  .background(StudioPanelBg)
                  .border(1.dp, StudioBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
              ) {
                if (mobileSheetMode == MobileSheetMode.LAYERS) {
                  LayerPanel(
                    selectedComponent = selectedComponent,
                    layers = currentComponentLayers,
                    selectedLayerId = selectedLayerId,
                    onSelectLayer = { id ->
                      viewModel.selectLayer(id)
                      mobileSheetMode = MobileSheetMode.INSPECTOR
                    },
                    onAddLayer = viewModel::addLayer,
                    onDeleteLayer = viewModel::deleteLayer,
                    onToggleVisibility = viewModel::toggleLayerVisibility,
                    onToggleLock = viewModel::toggleLayerLock,
                    onMoveUp = viewModel::moveLayerUp,
                    onMoveDown = viewModel::moveLayerDown,
                    onKitVisionClick = { isKitVisionDialogOpen = true },
                    onSaveClick = viewModel::saveProjectJson,
                    onLoadClick = { isSettingsDialogOpen = true },
                    modifier = Modifier.fillMaxSize()
                  )
                } else if (mobileSheetMode == MobileSheetMode.INSPECTOR) {
                  LayerInspector(
                    layer = selectedLayer,
                    onUpdateLayer = viewModel::updateSelectedLayer,
                    onClose = { mobileSheetMode = MobileSheetMode.NONE },
                    modifier = Modifier.fillMaxSize()
                  )
                }
              }
            }
          }

          // Bottom Bar for Mobile: Switch between 3D, Layers, Inspector, Kit Vision
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .background(StudioPanelBg)
              .border(1.dp, StudioBorder)
              .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            MobileNavButton(
              title = "3D View",
              icon = Icons.Default.ViewInAr,
              isActive = activeTab == StudioTab.VIEW_3D && mobileSheetMode == MobileSheetMode.NONE,
              onClick = {
                viewModel.setTab(StudioTab.VIEW_3D)
                mobileSheetMode = MobileSheetMode.NONE
              }
            )

            MobileNavButton(
              title = "Layers (${currentComponentLayers.size})",
              icon = Icons.Default.Layers,
              isActive = mobileSheetMode == MobileSheetMode.LAYERS,
              onClick = {
                mobileSheetMode = if (mobileSheetMode == MobileSheetMode.LAYERS) MobileSheetMode.NONE else MobileSheetMode.LAYERS
              }
            )

            MobileNavButton(
              title = "Inspector",
              icon = Icons.Default.Tune,
              isActive = mobileSheetMode == MobileSheetMode.INSPECTOR,
              onClick = {
                mobileSheetMode = if (mobileSheetMode == MobileSheetMode.INSPECTOR) MobileSheetMode.NONE else MobileSheetMode.INSPECTOR
              }
            )

            MobileNavButton(
              title = "Kit Vision",
              icon = Icons.Default.AutoAwesome,
              isActive = false,
              onClick = { isKitVisionDialogOpen = true }
            )
          }
        }
      }
    }
  }

  // Dialogs
  if (isKitVisionDialogOpen) {
    KitVisionDialog(
      onApplyPreset = { presetName ->
        viewModel.loadPreset(presetName)
      },
      onDismiss = { isKitVisionDialogOpen = false }
    )
  }

  if (isSettingsDialogOpen) {
    SettingsDialog(
      project = project,
      onUpdateProject = viewModel::updateProject,
      onDismiss = { isSettingsDialogOpen = false }
    )
  }
}

@Composable
fun MobileNavButton(
  title: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  isActive: Boolean,
  onClick: () -> Unit
) {
  Column(
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 8.dp, vertical = 4.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Icon(
      imageVector = icon,
      contentDescription = title,
      tint = if (isActive) AccentGreen else TextSecondary,
      modifier = Modifier.size(18.dp)
    )
    Text(
      text = title,
      color = if (isActive) AccentGreen else TextSecondary,
      fontSize = 10.sp,
      fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
    )
  }
}
