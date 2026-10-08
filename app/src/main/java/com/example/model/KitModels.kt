package com.example.model

import androidx.compose.ui.graphics.Color
import java.util.UUID

enum class KitComponentType(val label: String, val iconName: String) {
  SHIRT("Shirt", "shirt"),
  SHORTS("Shorts", "shorts"),
  SOCKS("Socks", "socks"),
  ARMBAND("Armband", "armband")
}

enum class LayerType(val label: String, val description: String) {
  BASE_DESIGN("Base design", "Primary jersey body color"),
  GRAPHIC("Graphic", "Geometric shapes, accents & overlays"),
  PATTERN("Pattern", "Stripes, checks, chevrons & gradients"),
  TEXT("Txt", "Player name, squad numbers & custom text"),
  LOGO("Logo", "Club crests, brand marks & chest sponsors"),
  SLEEVE_CUFFS("Sleeve cuffs", "Contrasting sleeve trim bands"),
  SIDE_PANELS("Side panels", "Vertical flank inserts & mesh breathing strips"),
  SHOULDER_PANELS("Shoulder panels", "Shoulder stripes & raglan cuts"),
  GROUP("Group", "Layer folder container")
}

enum class KitPatternType(val label: String) {
  STRIPES_VERTICAL("Vertical Stripes"),
  STRIPES_HORIZONTAL("Hoops (Horizontal)"),
  CHECKERBOARD("Checkerboard"),
  CHEVRON("Chevron V-Stripes"),
  PINSTRIPES("Thin Pinstripes"),
  HALVES("Two-Tone Halves"),
  SASH_DIAGONAL("Diagonal Sash"),
  GRADIENT_LINEAR("Linear Gradient"),
  DIAMONDS("Argyle Diamonds"),
  HEX_HONEYCOMB("Hex Honeycomb"),
  WAVES("Wave Ripples"),
  CAMOUFLAGE("Digital Camo")
}

enum class LayerBlendMode(val label: String) {
  NORMAL("Normal"),
  MULTIPLY("Multiply"),
  OVERLAY("Overlay"),
  SCREEN("Screen"),
  DIFFERENCE("Difference")
}

enum class LayerMask(val label: String) {
  FULL("Whole Component"),
  TORSO_FRONT("Torso Front"),
  TORSO_BACK("Torso Back"),
  SLEEVE_LEFT("Left Sleeve"),
  SLEEVE_RIGHT("Right Sleeve"),
  COLLAR("Collar Ring"),
  SIDE_LEFT("Left Flank"),
  SIDE_RIGHT("Right Flank"),
  CUFFS("Sleeve Cuffs")
}

enum class LogoType(val label: String, val category: String) {
  CREST_SHIELD("Shield Crest", "Crests"),
  CREST_ROUNDEL("Roundel Crest", "Crests"),
  CREST_STAR("Star Emblem", "Crests"),
  CREST_EAGLE("Imperial Eagle", "Crests"),
  CREST_LION("Lion Rampant", "Crests"),
  BRAND_SWOOSH("Dynamic Swoop", "Brand Marks"),
  BRAND_TRIPLE_STRIPES("Triple Stripes", "Brand Marks"),
  BRAND_CHEVRON("Dual Chevron", "Brand Marks"),
  BRAND_DIAMOND("Athletic Diamond", "Brand Marks"),
  BRAND_CAT("Feline Pounce", "Brand Marks"),
  SPONSOR_AIRWAYS("FLY EMIRATES", "Sponsors"),
  SPONSOR_TECH("CYBER TECH", "Sponsors"),
  SPONSOR_STREAMING("SPOTIFY", "Sponsors"),
  SPONSOR_AUTOMOTIVE("PIRELLI TIRES", "Sponsors"),
  SPONSOR_TELECOM("VODAFONE", "Sponsors"),
  SPONSOR_CRYPTO("BITCOIN VAULT", "Sponsors"),
  CUSTOM_UPLOAD("Custom Image", "Uploads")
}

enum class TextFont(val label: String) {
  ATHLETIC_BLOCK("Athletic Block"),
  MODERN_SANS("Modern Sans"),
  VINTAGE_SERIF("Vintage Serif"),
  STENCIL_FUTURISTIC("Cyber Stencil"),
  SCRIPT_CALLIGRAPHY("Script Cursive")
}

enum class CollarStyle(val label: String) {
  CREW_NECK("Crew Neck"),
  V_NECK("V-Neck Athletic"),
  POLO_BUTTON("Polo Button"),
  RIBBED_STAND("Ribbed Stand")
}

enum class ModelStyle(val label: String) {
  STANDARD("Standard Mannequin"),
  FITTED("Fitted Athletic"),
  HANGER("Flat Hanger"),
  FLAT_UV("2D Texture Map")
}

enum class LightingPreset(val label: String) {
  STUDIO_NEUTRAL("Studio 3-Point"),
  STADIUM_NIGHT("Stadium Floodlights"),
  SUNSET_WARM("Golden Sunset"),
  CYBER_NEON("Cyber Neon")
}

data class TransformState(
  val x: Float = 0f, // -100 to 100 percentage offset
  val y: Float = 0f, // -100 to 100 percentage offset
  val scaleX: Float = 1f,
  val scaleY: Float = 1f,
  val isLinked: Boolean = true,
  val rotation: Float = 0f, // degrees
  val tiling: Boolean = false
)

data class KitLayer(
  val id: String = UUID.randomUUID().toString(),
  val name: String,
  val type: LayerType,
  val visible: Boolean = true,
  val locked: Boolean = false,
  val opacity: Float = 1f,
  val blendMode: LayerBlendMode = LayerBlendMode.NORMAL,
  val transform: TransformState = TransformState(),
  val mask: LayerMask = LayerMask.FULL,
  // Colors (ARGB Long for serialization compatibility)
  val primaryColor: Long = 0xFF1E293BL,
  val secondaryColor: Long = 0xFFFFFFFFL,
  val tertiaryColor: Long = 0xFFEAB308L,
  // Pattern Parameters
  val patternType: KitPatternType = KitPatternType.STRIPES_VERTICAL,
  val patternFrequency: Int = 6, // 2 to 24
  val patternRatio: Float = 0.5f,
  val patternAngle: Float = 0f,
  // Text Parameters
  val text: String = "OPENKIT",
  val textFont: TextFont = TextFont.ATHLETIC_BLOCK,
  val textSize: Float = 28f,
  val textArch: Float = 0f, // -50 to 50
  val strokeWidth: Float = 0f,
  val strokeColor: Long = 0xFF000000L,
  val hasShadow: Boolean = false,
  // Logo Parameters
  val logoType: LogoType = LogoType.CREST_SHIELD,
  val customImagePath: String? = null,
  // Material / Resa
  val shininess: Float = 20f,
  val metalness: Float = 0f,
  val followBodyShape: Boolean = true
) {
  val primaryColorObj: Color get() = Color(primaryColor)
  val secondaryColorObj: Color get() = Color(secondaryColor)
  val tertiaryColorObj: Color get() = Color(tertiaryColor)
  val strokeColorObj: Color get() = Color(strokeColor)
}

data class KitProject(
  val id: String = UUID.randomUUID().toString(),
  val name: String = "FC Inter 26/27 Away",
  val teamName: String = "FC OpenKit",
  val gameEdition: String = "EAFC 27 / OpenKit",
  val collarStyle: CollarStyle = CollarStyle.V_NECK,
  val collarColor: Long = 0xFFFFFFFFL,
  val collarTrimColor: Long = 0xFF0284C7L,
  val globalShininess: Float = 25f,
  val globalMetalness: Float = 10f,
  val fabricRoughness: Float = 40f,
  val modelStyle: ModelStyle = ModelStyle.STANDARD,
  val lightingPreset: LightingPreset = LightingPreset.STUDIO_NEUTRAL,
  val shirtLayers: List<KitLayer> = emptyList(),
  val shortsLayers: List<KitLayer> = emptyList(),
  val socksLayers: List<KitLayer> = emptyList(),
  val armbandLayers: List<KitLayer> = emptyList()
) {
  fun getLayersFor(component: KitComponentType): List<KitLayer> = when (component) {
    KitComponentType.SHIRT -> shirtLayers
    KitComponentType.SHORTS -> shortsLayers
    KitComponentType.SOCKS -> socksLayers
    KitComponentType.ARMBAND -> armbandLayers
  }

  fun updateLayersFor(component: KitComponentType, newLayers: List<KitLayer>): KitProject = when (component) {
    KitComponentType.SHIRT -> copy(shirtLayers = newLayers)
    KitComponentType.SHORTS -> copy(shortsLayers = newLayers)
    KitComponentType.SOCKS -> copy(socksLayers = newLayers)
    KitComponentType.ARMBAND -> copy(armbandLayers = newLayers)
  }
}
