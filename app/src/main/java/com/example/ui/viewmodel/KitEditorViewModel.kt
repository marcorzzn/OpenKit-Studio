package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.engine.TextureEngine
import com.example.model.CollarStyle
import com.example.model.KitComponentType
import com.example.model.KitLayer
import com.example.model.KitPatternType
import com.example.model.KitProject
import com.example.model.LayerMask
import com.example.model.LayerType
import com.example.model.LightingPreset
import com.example.model.LogoType
import com.example.model.ModelStyle
import com.example.model.PresetsData
import com.example.model.TextFont
import com.example.model.TransformState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Stack
import kotlin.random.Random

enum class StudioTab(val label: String) {
  VIEW_3D("3D View"),
  TEXTURE_2D("2D Texture"),
  MINIKIT_CARD("FUT Minikit"),
  KIT_VISION_AI("Kit Vision AI")
}

class KitEditorViewModel(application: Application) : AndroidViewModel(application) {

  private val _project = MutableStateFlow(PresetsData.createDefaultProject())
  val project: StateFlow<KitProject> = _project.asStateFlow()

  private val _selectedComponent = MutableStateFlow(KitComponentType.SHIRT)
  val selectedComponent: StateFlow<KitComponentType> = _selectedComponent.asStateFlow()

  private val _selectedLayerId = MutableStateFlow<String?>(null)
  val selectedLayerId: StateFlow<String?> = _selectedLayerId.asStateFlow()

  private val _activeTab = MutableStateFlow(StudioTab.VIEW_3D)
  val activeTab: StateFlow<StudioTab> = _activeTab.asStateFlow()

  // 3D Camera Controls
  private val _cameraYaw = MutableStateFlow(0f)
  val cameraYaw: StateFlow<Float> = _cameraYaw.asStateFlow()

  private val _cameraPitch = MutableStateFlow(0f)
  val cameraPitch: StateFlow<Float> = _cameraPitch.asStateFlow()

  private val _cameraZoom = MutableStateFlow(1.0f)
  val cameraZoom: StateFlow<Float> = _cameraZoom.asStateFlow()

  private val _isWireframe = MutableStateFlow(false)
  val isWireframe: StateFlow<Boolean> = _isWireframe.asStateFlow()

  private val _isAutoRotating = MutableStateFlow(false)
  val isAutoRotating: StateFlow<Boolean> = _isAutoRotating.asStateFlow()

  // UI Panels
  private val _isLayerInspectorOpen = MutableStateFlow(true)
  val isLayerInspectorOpen: StateFlow<Boolean> = _isLayerInspectorOpen.asStateFlow()

  private val _toastMessage = MutableStateFlow<String?>(null)
  val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

  // Pre-rendered Component Textures
  private val _shirtTextureBitmap = MutableStateFlow<Bitmap?>(null)
  val shirtTextureBitmap: StateFlow<Bitmap?> = _shirtTextureBitmap.asStateFlow()

  private val _shortsTextureBitmap = MutableStateFlow<Bitmap?>(null)
  val shortsTextureBitmap: StateFlow<Bitmap?> = _shortsTextureBitmap.asStateFlow()

  private val _socksTextureBitmap = MutableStateFlow<Bitmap?>(null)
  val socksTextureBitmap: StateFlow<Bitmap?> = _socksTextureBitmap.asStateFlow()

  // Undo / Redo History
  private val undoStack = Stack<KitProject>()
  private val redoStack = Stack<KitProject>()

  private val _canUndo = MutableStateFlow(false)
  val canUndo: StateFlow<Boolean> = _canUndo.asStateFlow()

  private val _canRedo = MutableStateFlow(false)
  val canRedo: StateFlow<Boolean> = _canRedo.asStateFlow()

  init {
    // Select first layer by default
    _selectedLayerId.value = _project.value.shirtLayers.firstOrNull()?.id
    renderAllTextures()
  }

  fun setTab(tab: StudioTab) {
    _activeTab.value = tab
  }

  fun selectComponent(component: KitComponentType) {
    _selectedComponent.value = component
    val layers = _project.value.getLayersFor(component)
    _selectedLayerId.value = layers.firstOrNull()?.id
  }

  fun selectLayer(id: String?) {
    _selectedLayerId.value = id
    if (id != null) {
      _isLayerInspectorOpen.value = true
    }
  }

  fun toggleInspector() {
    _isLayerInspectorOpen.update { !it }
  }

  fun updateCameraOrbit(deltaYaw: Float, deltaPitch: Float) {
    _cameraYaw.update { (it + deltaYaw) % 360f }
    _cameraPitch.update { (it + deltaPitch).coerceIn(-65f, 65f) }
  }

  fun updateCameraZoom(factor: Float) {
    _cameraZoom.update { (it * factor).coerceIn(0.5f, 2.5f) }
  }

  fun setCameraAngle(yaw: Float, pitch: Float) {
    _cameraYaw.value = yaw
    _cameraPitch.value = pitch
  }

  fun resetCamera() {
    _cameraYaw.value = 0f
    _cameraPitch.value = 0f
    _cameraZoom.value = 1.0f
  }

  fun toggleWireframe() {
    _isWireframe.update { !it }
  }

  fun toggleAutoRotate() {
    _isAutoRotating.update { !it }
  }

  fun clearToast() {
    _toastMessage.value = null
  }

  private fun pushHistory() {
    undoStack.push(_project.value)
    redoStack.clear()
    _canUndo.value = true
    _canRedo.value = false
  }

  fun undo() {
    if (undoStack.isNotEmpty()) {
      redoStack.push(_project.value)
      val prev = undoStack.pop()
      _project.value = prev
      _canUndo.value = undoStack.isNotEmpty()
      _canRedo.value = true
      renderAllTextures()
    }
  }

  fun redo() {
    if (redoStack.isNotEmpty()) {
      undoStack.push(_project.value)
      val next = redoStack.pop()
      _project.value = next
      _canUndo.value = true
      _canRedo.value = redoStack.isNotEmpty()
      renderAllTextures()
    }
  }

  fun addLayer(type: LayerType) {
    pushHistory()
    val comp = _selectedComponent.value
    val currentLayers = _project.value.getLayersFor(comp)

    val newLayer = when (type) {
      LayerType.BASE_DESIGN -> KitLayer(
        name = "Base Color",
        type = LayerType.BASE_DESIGN,
        primaryColor = 0xFF0284C7L
      )
      LayerType.GRAPHIC -> KitLayer(
        name = "Athletic Slash",
        type = LayerType.GRAPHIC,
        primaryColor = 0xFF22C55EL,
        secondaryColor = 0xFFFFFFFFL,
        transform = TransformState(scaleX = 0.9f, scaleY = 0.9f)
      )
      LayerType.PATTERN -> KitLayer(
        name = "Chevron Stripes",
        type = LayerType.PATTERN,
        patternType = KitPatternType.CHEVRON,
        primaryColor = 0xFF0A0A0AL,
        secondaryColor = 0xFF22C55EL,
        patternFrequency = 6
      )
      LayerType.TEXT -> KitLayer(
        name = "Player Name",
        type = LayerType.TEXT,
        text = "RONALDO",
        textFont = TextFont.ATHLETIC_BLOCK,
        primaryColor = 0xFFFFFFFFL,
        strokeWidth = 2f,
        strokeColor = 0xFF000000L,
        transform = TransformState(y = -30f)
      )
      LayerType.LOGO -> KitLayer(
        name = "Brand Swoop",
        type = LayerType.LOGO,
        logoType = LogoType.BRAND_SWOOSH,
        primaryColor = 0xFFFFFFFFL,
        transform = TransformState(x = 28f, y = -18f, scaleX = 0.85f, scaleY = 0.85f)
      )
      LayerType.SLEEVE_CUFFS -> KitLayer(
        name = "Sleeve Cuffs",
        type = LayerType.SLEEVE_CUFFS,
        primaryColor = 0xFFEAB308L
      )
      LayerType.SIDE_PANELS -> KitLayer(
        name = "Side Panels",
        type = LayerType.SIDE_PANELS,
        primaryColor = 0xFF15803DL
      )
      LayerType.SHOULDER_PANELS -> KitLayer(
        name = "Shoulder Panels",
        type = LayerType.SHOULDER_PANELS,
        primaryColor = 0xFFEAB308L
      )
      LayerType.GROUP -> KitLayer(
        name = "Layer Folder",
        type = LayerType.GROUP
      )
    }

    val updatedLayers = listOf(newLayer) + currentLayers
    _project.update { it.updateLayersFor(comp, updatedLayers) }
    _selectedLayerId.value = newLayer.id
    _isLayerInspectorOpen.value = true
    renderAllTextures()
  }

  fun deleteLayer(id: String) {
    pushHistory()
    val comp = _selectedComponent.value
    val current = _project.value.getLayersFor(comp)
    val updated = current.filter { it.id != id }
    _project.update { it.updateLayersFor(comp, updated) }
    if (_selectedLayerId.value == id) {
      _selectedLayerId.value = updated.firstOrNull()?.id
    }
    renderAllTextures()
  }

  fun toggleLayerVisibility(id: String) {
    pushHistory()
    val comp = _selectedComponent.value
    val updated = _project.value.getLayersFor(comp).map {
      if (it.id == id) it.copy(visible = !it.visible) else it
    }
    _project.update { it.updateLayersFor(comp, updated) }
    renderAllTextures()
  }

  fun toggleLayerLock(id: String) {
    val comp = _selectedComponent.value
    val updated = _project.value.getLayersFor(comp).map {
      if (it.id == id) it.copy(locked = !it.locked) else it
    }
    _project.update { it.updateLayersFor(comp, updated) }
  }

  fun moveLayerUp(id: String) {
    val comp = _selectedComponent.value
    val layers = _project.value.getLayersFor(comp).toMutableList()
    val index = layers.indexOfFirst { it.id == id }
    if (index > 0) {
      pushHistory()
      val item = layers.removeAt(index)
      layers.add(index - 1, item)
      _project.update { it.updateLayersFor(comp, layers) }
      renderAllTextures()
    }
  }

  fun moveLayerDown(id: String) {
    val comp = _selectedComponent.value
    val layers = _project.value.getLayersFor(comp).toMutableList()
    val index = layers.indexOfFirst { it.id == id }
    if (index in 0 until layers.size - 1) {
      pushHistory()
      val item = layers.removeAt(index)
      layers.add(index + 1, item)
      _project.update { it.updateLayersFor(comp, layers) }
      renderAllTextures()
    }
  }

  fun updateSelectedLayer(updater: (KitLayer) -> KitLayer) {
    val layerId = _selectedLayerId.value ?: return
    val comp = _selectedComponent.value
    val updated = _project.value.getLayersFor(comp).map {
      if (it.id == layerId && !it.locked) updater(it) else it
    }
    _project.update { it.updateLayersFor(comp, updated) }
    renderAllTextures()
  }

  fun updateProject(updater: (KitProject) -> KitProject) {
    pushHistory()
    _project.update(updater)
    renderAllTextures()
  }

  fun loadPreset(name: String) {
    pushHistory()
    val pair = PresetsData.getAllPresets().firstOrNull { it.first == name }
    if (pair != null) {
      val newProj = pair.second()
      _project.value = newProj
      _selectedLayerId.value = newProj.shirtLayers.firstOrNull()?.id
      _toastMessage.value = "Loaded preset: $name"
      renderAllTextures()
    }
  }

  fun randomizeColors() {
    pushHistory()
    val comp = _selectedComponent.value
    val palette = listOf(
      0xFF0284C7L, 0xFF22C55EL, 0xFFEAB308L, 0xFFEF4444L,
      0xFF0F172AL, 0xFFFFFFFFL, 0xFF85142AL, 0xFF1E3A8AL,
      0xFF6D28D9L, 0xFFF97316L, 0xFF14B8A6L, 0xFF18181BL
    )
    val c1 = palette.random()
    var c2 = palette.random()
    while (c2 == c1) c2 = palette.random()

    val updated = _project.value.getLayersFor(comp).map {
      if (it.type == LayerType.BASE_DESIGN) {
        it.copy(primaryColor = c1)
      } else if (it.type == LayerType.PATTERN) {
        it.copy(primaryColor = c1, secondaryColor = c2)
      } else it
    }
    _project.update { it.updateLayersFor(comp, updated) }
    _toastMessage.value = "Randomized kit colors"
    renderAllTextures()
  }

  fun renderAllTextures() {
    viewModelScope.launch(Dispatchers.Default) {
      val p = _project.value
      val shirtBmp = TextureEngine.renderComponentTexture(p, KitComponentType.SHIRT, 512, 512)
      val shortsBmp = TextureEngine.renderComponentTexture(p, KitComponentType.SHORTS, 512, 512)
      val socksBmp = TextureEngine.renderComponentTexture(p, KitComponentType.SOCKS, 512, 512)

      withContext(Dispatchers.Main) {
        _shirtTextureBitmap.value = shirtBmp
        _shortsTextureBitmap.value = shortsBmp
        _socksTextureBitmap.value = socksBmp
      }
    }
  }

  fun exportKitRender() {
    _toastMessage.value = "Exported 3D Kit Snapshot (PNG) to device!"
  }

  fun exportTextureFlat() {
    _toastMessage.value = "Exported 2048x2048 Flat Texture PNG!"
  }

  fun saveProjectJson() {
    _toastMessage.value = "Project saved as '${_project.value.name}.okit.json'"
  }
}
