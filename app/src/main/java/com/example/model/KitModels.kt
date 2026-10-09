package com.example.model

import androidx.compose.ui.graphics.Color
import java.util.UUID

enum class SportType(val label: String, val icon: String) {
  FOOTBALL("Football (Soccer)", "⚽"),
  BASKETBALL("Basketball", "🏀"),
  AMERICAN_FOOTBALL("American Football", "🏈"),
  HOCKEY("Hockey", "🏒"),
  CFB_UNIFORM("CFB Uniform", "🎽")
}

enum class ModelFit(val label: String, val id: Int) {
  MODEL_0("Model 0 (Fitted / Tight)", 0),
  MODEL_1("Model 1 (Regular Fit)", 1),
  MODEL_2("Model 2 (Loose Classic)", 2),
  MODEL_3("Model 3 (Hanger / Flat)", 3),
  MODEL_4("Model 4 (Flying Athletic)", 4)
}

enum class CollarTemplate(val id: Int, val label: String, val description: String) {
  COLLAR_0(0, "Collar 0 (Classic Crew)", "Standard rounded athletic ribbed crewneck"),
  COLLAR_1(1, "Collar 1 (Deep V-Neck)", "Sleek aerodynamic deep V cut"),
  COLLAR_2(2, "Collar 2 (Modern Polo)", "Sport polo collar with turn-down flap"),
  COLLAR_3(3, "Collar 3 (Crossed V-Neck)", "Overlapping crossed front collar"),
  COLLAR_4(4, "Collar 4 (Ribbed Stand)", "Mandarin / Korean low stand collar"),
  COLLAR_5(5, "Collar 5 (Button Polo)", "Formal button placket collar"),
  COLLAR_6(6, "Collar 6 (Contrast Insert)", "V-neck with dual-color triangular throat insert"),
  COLLAR_7(7, "Collar 7 (Retro 90s Rib)", "Wide ribbed retro chunky neckline"),
  COLLAR_8(8, "Collar 8 (Asymmetric Split)", "Modern angular split front collar"),
  COLLAR_9(9, "Collar 9 (High Mock Neck)", "Thermal winter high mock collar"),
  COLLAR_10(10, "Collar 10 (Curved Yoke)", "Seamless ergonomic curved collar"),
  COLLAR_11(11, "Collar 11 (Raglan Notch)", "Continuous shoulder raglan notched collar"),
  COLLAR_12(12, "Collar 12 (Double Trim)", "Dual concentric piping sports collar"),
  COLLAR_13(13, "Collar 13 (Open Spread)", "Wide open spread retro collar"),
  COLLAR_14(14, "Collar 14 (Elastic Mesh)", "Perforated breathable mesh collar ring"),
  COLLAR_15(15, "Collar 15 (Square Cut)", "Geometric squared athletic throat cut"),
  COLLAR_16(16, "Collar 16 (Zip Neck)", "Quarter-zip performance mock collar"),
  COLLAR_17(17, "Collar 17 (Crossover Wrap)", "Japanese wrap-style crossover neckline"),
  COLLAR_18(18, "Collar 18 (Slim Band)", "Ultra-thin modern taped band"),
  COLLAR_19(19, "Collar 19 (Two-Tone V)", "Split halves two-tone V-neck"),
  COLLAR_20(20, "Collar 20 (Grandad Collar)", "Buttonless rounded grandad band"),
  COLLAR_21(21, "Collar 21 (Laser Perforated)", "Laser cut air channels collar"),
  COLLAR_22(22, "Collar 22 (Polo Ribbed Tip)", "Knitted polo collar with tipped stripe edge"),
  COLLAR_23(23, "Collar 23 (Hybrid Shield)", "Shield-shaped contoured chest notch"),
  COLLAR_24(24, "Collar 24 (Bonded Thermo)", "Seamless bonded heat-welded collar"),
  COLLAR_25(25, "Collar 25 (Retro Lace-Up)", "Vintage heritage lace eyelet collar"),
  COLLAR_26(26, "Collar 26 (FC24 / FC27 Elite)", "Next-gen authentic pro fit ribbed collar")
}

enum class LightingPreset(val label: String) {
  STUDIO_NEUTRAL("Studio 3-Point"),
  STADIUM_NIGHT("Stadium Floodlights"),
  SUNSET_WARM("Golden Sunset"),
  CYBER_NEON("Cyber Neon")
}

enum class CameraPreset(val label: String, val yaw: Float, val pitch: Float) {
  FRONT("Front", 0f, 0f),
  SIDE("Side (3/4)", 45f, 10f),
  SIDE_RIGHT("Side Right", -45f, 10f),
  LATERAL_LEFT("Lateral Left", 90f, 0f),
  LATERAL_RIGHT("Lateral Right", -90f, 0f),
  BACK("Back", 180f, 0f),
  SIDE_BACK("Side (Back)", 135f, 10f),
  ROTATING("Rotating Turntable", 0f, 0f)
}

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
  CAMOUFLAGE("Digital Camo"),
  ZIG_ZAG("Zig Zag"),
  CROSS_PATTERN("Nordic Cross"),
  DOTS_HALFTONE("Polka Dots / Halftone"),
  MOSAIC_TILES("Mosaic Tiles"),
  MARBLE_TEXTURE("Marble Veins"),
  ABSTRACT_BRUSH("Abstract Grunge Brush")
}

enum class LayerBlendMode(val label: String) {
  NORMAL("Normal"),
  MULTIPLY("Multiply"),
  OVERLAY("Overlay"),
  SCREEN("Screen"),
  DIFFERENCE("Difference"),
  ADD("Add"),
  SUBTRACT("Subtract")
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

enum class LayerPlacement(val label: String) {
  FRONT("Front"),
  BACK("Back"),
  SLEEVE_LEFT("Left Sleeve"),
  SLEEVE_RIGHT("Right Sleeve"),
  COLLAR("Collar"),
  HEM("Bottom Hem")
}

enum class PlacementPreset(val label: String, val x: Float, val y: Float) {
  CUSTOM("Custom Placement", 0f, 0f),
  CHEST_CENTER("Chest Center", 0f, 6f),
  LEFT_CHEST("Left Chest (Crest)", -28f, -18f),
  RIGHT_CHEST("Right Chest (Brand)", 28f, -18f),
  BACK_UPPER("Back Upper (Name)", 0f, -36f),
  BACK_LOWER("Back Lower (Number)", 0f, -6f),
  SLEEVE_L("Left Sleeve (Badge)", -65f, 0f),
  SLEEVE_R("Right Sleeve (Badge)", 65f, 0f)
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
  BADGE_CHAMPIONS("Stars Trophy", "Competition Badges"),
  BADGE_DOMESTIC("Gold Laurel Crest", "Competition Badges"),
  BADGE_RESPECT("Fair Play Badge", "Competition Badges"),
  CUSTOM_UPLOAD("Custom Image", "Uploads")
}

enum class TextFont(val label: String, val family: String) {
  ANTON("Anton (Bold League)", "Anton"),
  BEBAS_NEUE("Bebas Neue (Athletic)", "Bebas Neue"),
  OSWALD("Oswald (Modern Condensed)", "Oswald"),
  ARCHIVO_BLACK("Archivo Black (Heavy Impact)", "Archivo Black"),
  TEKO("Teko (Ultra Tall Number)", "Teko"),
  RAJDHANI("Rajdhani (Cyber Tech)", "Rajdhani"),
  BARLOW_CONDENSED("Barlow Condensed (Sleek)", "Barlow Condensed"),
  ATHLETIC_BLOCK("Classic Athletic Block", "Monospace"),
  VINTAGE_SERIF("Vintage 1960s Serif", "Serif")
}

data class TransformState(
  val x: Float = 0f,
  val y: Float = 0f,
  val scaleX: Float = 1f,
  val scaleY: Float = 1f,
  val isLinked: Boolean = true,
  val rotation: Float = 0f,
  val tiling: Boolean = false,
  val heightRatio: Float = 0f,
  val saturation: Float = 100f
)

data class KitLayer(
  val id: String = UUID.randomUUID().toString(),
  val name: String,
  val type: LayerType,
  val parentId: String? = null,
  val expanded: Boolean = true,
  val visible: Boolean = true,
  val locked: Boolean = false,
  val opacity: Float = 1f,
  val blendMode: LayerBlendMode = LayerBlendMode.NORMAL,
  val transform: TransformState = TransformState(),
  val mask: LayerMask = LayerMask.FULL,
  val placement: LayerPlacement = LayerPlacement.FRONT,
  val placementPreset: PlacementPreset = PlacementPreset.CUSTOM,
  val fabricPrint: Boolean = false,
  val mirror: Boolean = false,
  val mirrorCenter: Boolean = false,
  val border: Float = 0f,
  val blur: Float = 0f,
  // Custom Material
  val customMaterialEnabled: Boolean = false,
  val customShininess: Float = 25f,
  val customMetalness: Float = 0f,
  // Colors
  val primaryColor: Long = 0xFF1E293BL,
  val secondaryColor: Long = 0xFFFFFFFFL,
  val tertiaryColor: Long = 0xFFEAB308L,
  // Pattern Parameters
  val patternType: KitPatternType = KitPatternType.STRIPES_VERTICAL,
  val patternFrequency: Int = 6,
  val patternRatio: Float = 0.5f,
  val patternAngle: Float = 0f,
  // Text Parameters
  val text: String = "OPENKIT",
  val textFont: TextFont = TextFont.ANTON,
  val textSize: Float = 28f,
  val textArch: Float = 0f,
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

data class NumberConfig(
  val enabled: Boolean = true,
  val value: String = "10",
  val font: TextFont = TextFont.ANTON,
  val color: Long = 0xFFFFFFFFL,
  val borderEnabled: Boolean = true,
  val borderColor: Long = 0xFF000000L,
  val borderWidth: Float = 3f,
  val size: Float = 54f,
  val offset: Float = 0f,
  val frontNumberEnabled: Boolean = false,
  val frontNumberX: Float = -24f,
  val frontNumberY: Float = -6f,
  val frontNumberSize: Float = 18f,
  val shortsNumberEnabled: Boolean = true,
  val shortsNumberX: Float = 26f,
  val shortsNumberY: Float = 18f,
  val shortsNumberSize: Float = 18f,
  val customMaterial: Boolean = false,
  val shininess: Float = 25f,
  val metalness: Float = 0f
)

data class PlayerNameConfig(
  val enabled: Boolean = true,
  val name: String = "PLAYER",
  val font: TextFont = TextFont.ANTON,
  val color: Long = 0xFFFFFFFFL,
  val posX: Float = 0f,
  val posY: Float = -36f,
  val width: Float = 50f,
  val height: Float = 50f,
  val arc: Float = 0f
)

data class CompetitionBadge(
  val id: String = UUID.randomUUID().toString(),
  val name: String = "Right Sleeve Badge",
  val enabled: Boolean = true,
  val logoType: LogoType = LogoType.BADGE_CHAMPIONS,
  val placement: LayerPlacement = LayerPlacement.SLEEVE_RIGHT,
  val posX: Float = 60f,
  val posY: Float = 0f,
  val size: Float = 50f,
  val color: Long = 0xFFEAB308L
)

enum class ExportTarget(val label: String) {
  FULL_KIT("Full Kit (All Components)"),
  SHIRT_ONLY("Shirt Only"),
  SHORTS_ONLY("Shorts Only"),
  SOCKS_ONLY("Socks Only"),
  ARMBAND_ONLY("Armband Only"),
  SINGLE_LAYER("Single Active Layer"),
  FLAT_TEXTURE("Flat 2048/4096 UV Texture"),
  VIEWPORT_RENDER("3D Viewport Snapshot"),
  SELECTION("Selected Elements Only")
}

enum class ExportFormat(val extension: String, val mimeType: String) {
  PNG("PNG (with Alpha)", "image/png"),
  JPG("JPG (High Quality)", "image/jpeg"),
  WEBP("WEBP (Lossless/Alpha)", "image/webp"),
  AVIF("AVIF (Next-Gen)", "image/avif"),
  SVG("SVG (Vector)", "image/svg+xml"),
  JSON("JSON (Project Data)", "application/json")
}

enum class ExportResolution(val label: String, val size: Int) {
  RES_512("512 × 512 px (Low)", 512),
  RES_1024("1024 × 1024 px (Standard)", 1024),
  RES_2048("2048 × 2048 px (EAFC 27 Game 2K)", 2048),
  RES_4096("4096 × 4096 px (Ultra HD 4K)", 4096),
  VIEWPORT_NATIVE("Viewport Native Screen Size", 0),
  CUSTOM("Custom Dimensions", -1)
}

data class KitProject(
  val id: String = UUID.randomUUID().toString(),
  val name: String = "FC Inter 26/27 Away",
  val teamName: String = "FC Internazionale",
  val sport: SportType = SportType.FOOTBALL,
  val gameEdition: String = "EAFC 27 / OpenKit",
  val modelFit: ModelFit = ModelFit.MODEL_0,
  val collarTemplate: CollarTemplate = CollarTemplate.COLLAR_1,
  val collarColor: Long = 0xFFFFFFFFL,
  val collarTrimColor: Long = 0xFF0284C7L,
  val globalShininess: Float = 25f,
  val globalMetalness: Float = 10f,
  val fabricRoughness: Float = 40f,
  val lightingPreset: LightingPreset = LightingPreset.STUDIO_NEUTRAL,
  val cameraPreset: CameraPreset = CameraPreset.FRONT,
  val numbers: NumberConfig = NumberConfig(),
  val playerName: PlayerNameConfig = PlayerNameConfig(),
  val badges: List<CompetitionBadge> = listOf(
    CompetitionBadge(name = "Competition (Right Sleeve)", placement = LayerPlacement.SLEEVE_RIGHT, posX = 60f, posY = 0f),
    CompetitionBadge(name = "Competition (Left Sleeve)", placement = LayerPlacement.SLEEVE_LEFT, posX = -60f, posY = 0f),
    CompetitionBadge(name = "Competition (Front)", placement = LayerPlacement.FRONT, posX = 0f, posY = -26f, size = 30f)
  ),
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
