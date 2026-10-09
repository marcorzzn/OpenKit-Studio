package com.example.engine

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import com.example.model.CollarTemplate
import com.example.model.KitProject
import com.example.model.LightingPreset
import com.example.model.ModelFit
import com.example.model.SportType
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

data class Vec3(val x: Float, val y: Float, val z: Float) {
  operator fun plus(o: Vec3) = Vec3(x + o.x, y + o.y, z + o.z)
  operator fun minus(o: Vec3) = Vec3(x - o.x, y - o.y, z - o.z)
  operator fun times(s: Float) = Vec3(x * s, y * s, z * s)

  fun length(): Float = sqrt(x * x + y * y + z * z)

  fun normalized(): Vec3 {
    val len = length()
    return if (len > 0.0001f) Vec3(x / len, y / len, z / len) else Vec3(0f, 1f, 0f)
  }

  fun dot(o: Vec3): Float = x * o.x + y * o.y + z * o.z

  fun cross(o: Vec3): Vec3 = Vec3(
    y * o.z - z * o.y,
    z * o.x - x * o.z,
    x * o.y - y * o.x
  )
}

enum class MeshPart {
  TORSO_FRONT,
  TORSO_BACK,
  TORSO_FLANK,
  COLLAR,
  COLLAR_TRIM,
  SLEEVE_LEFT,
  SLEEVE_RIGHT,
  CUFF_LEFT,
  CUFF_RIGHT,
  SHORTS_PELVIS,
  SHORTS_LEFT,
  SHORTS_RIGHT,
  SOCK_LEFT,
  SOCK_RIGHT,
  BOOT_LEFT,
  BOOT_RIGHT
}

data class Poly3D(
  val vertices: List<Vec3>,
  val uv: List<Pair<Float, Float>>,
  val part: MeshPart,
  val baseColor: Long = 0xFFFFFFFFL
)

class Kit3DRenderer {

  private val modelPolygons = mutableListOf<Poly3D>()

  init {
    buildMannequinModel()
  }

  private fun buildMannequinModel() {
    modelPolygons.clear()

    // 1. Torso Mesh (Slices from neck +140 to waist 0)
    val torsoY = listOf(140f, 105f, 70f, 35f, 0f)
    val torsoR = listOf(36f, 44f, 42f, 39f, 40f)
    val numSegments = 16

    for (i in 0 until torsoY.size - 1) {
      val yTop = torsoY[i]
      val yBottom = torsoY[i + 1]
      val rTop = torsoR[i]
      val rBottom = torsoR[i + 1]

      for (seg in 0 until numSegments) {
        val a1 = (seg * 2 * PI / numSegments).toFloat()
        val a2 = ((seg + 1) * 2 * PI / numSegments).toFloat()

        val x1Top = cos(a1) * rTop * 1.25f
        val z1Top = sin(a1) * rTop * 0.85f
        val x2Top = cos(a2) * rTop * 1.25f
        val z2Top = sin(a2) * rTop * 0.85f

        val x1Bot = cos(a1) * rBottom * 1.25f
        val z1Bot = sin(a1) * rBottom * 0.85f
        val x2Bot = cos(a2) * rBottom * 1.25f
        val z2Bot = sin(a2) * rBottom * 0.85f

        val midZ = (z1Top + z2Top + z1Bot + z2Bot) / 4f
        val part = when {
          midZ > 10f -> MeshPart.TORSO_FRONT
          midZ < -10f -> MeshPart.TORSO_BACK
          else -> MeshPart.TORSO_FLANK
        }

        val u1 = seg.toFloat() / numSegments
        val u2 = (seg + 1).toFloat() / numSegments
        val v1 = i.toFloat() / (torsoY.size - 1)
        val v2 = (i + 1).toFloat() / (torsoY.size - 1)

        modelPolygons.add(
          Poly3D(
            vertices = listOf(
              Vec3(x1Top, yTop, z1Top),
              Vec3(x2Top, yTop, z2Top),
              Vec3(x2Bot, yBottom, z2Bot),
              Vec3(x1Bot, yBottom, z1Bot)
            ),
            uv = listOf(u1 to v1, u2 to v1, u2 to v2, u1 to v2),
            part = part
          )
        )
      }
    }

    // 2. Collar Ring (12 radial segments)
    for (seg in 0 until 12) {
      val a1 = (seg * 2 * PI / 12).toFloat()
      val a2 = ((seg + 1) * 2 * PI / 12).toFloat()
      val rInner = 20f
      val rOuter = 26f
      val y = 142f
      modelPolygons.add(
        Poly3D(
          vertices = listOf(
            Vec3(cos(a1) * rInner * 1.1f, y + 6f, sin(a1) * rInner),
            Vec3(cos(a2) * rInner * 1.1f, y + 6f, sin(a2) * rInner),
            Vec3(cos(a2) * rOuter * 1.1f, y, sin(a2) * rOuter),
            Vec3(cos(a1) * rOuter * 1.1f, y, sin(a1) * rOuter)
          ),
          uv = listOf(0f to 0f, 1f to 0f, 1f to 1f, 0f to 1f),
          part = MeshPart.COLLAR
        )
      )
    }

    // 3. Sleeves
    buildSleeve(isLeft = true)
    buildSleeve(isLeft = false)

    // 4. Shorts
    buildShorts()

    // 5. Socks & Boots
    buildSocksAndBoots()
  }

  private fun buildSleeve(isLeft: Boolean) {
    val sign = if (isLeft) -1f else 1f
    val shoulderX = sign * 48f
    val shoulderY = 120f
    val shoulderZ = 0f

    val cuffX = sign * 78f
    val cuffY = 65f
    val cuffZ = 6f

    val numSegments = 8
    val rShoulder = 18f
    val rCuff = 14f

    for (seg in 0 until numSegments) {
      val a1 = (seg * 2 * PI / numSegments).toFloat()
      val a2 = ((seg + 1) * 2 * PI / numSegments).toFloat()

      val v1 = Vec3(shoulderX + cos(a1) * rShoulder, shoulderY + sin(a1) * rShoulder * 0.8f, shoulderZ + sin(a1) * rShoulder)
      val v2 = Vec3(shoulderX + cos(a2) * rShoulder, shoulderY + sin(a2) * rShoulder * 0.8f, shoulderZ + sin(a2) * rShoulder)
      val v3 = Vec3(cuffX + cos(a2) * rCuff, cuffY + sin(a2) * rCuff * 0.8f, cuffZ + sin(a2) * rCuff)
      val v4 = Vec3(cuffX + cos(a1) * rCuff, cuffY + sin(a1) * rCuff * 0.8f, cuffZ + sin(a1) * rCuff)

      val part = if (isLeft) MeshPart.SLEEVE_LEFT else MeshPart.SLEEVE_RIGHT
      modelPolygons.add(
        Poly3D(
          vertices = if (isLeft) listOf(v1, v2, v3, v4) else listOf(v2, v1, v4, v3),
          uv = listOf(0.1f to 0.1f, 0.4f to 0.1f, 0.4f to 0.5f, 0.1f to 0.5f),
          part = part
        )
      )

      val cuffPart = if (isLeft) MeshPart.CUFF_LEFT else MeshPart.CUFF_RIGHT
      val cuffEdge1 = v4 + Vec3(sign * 4f, -4f, 0f)
      val cuffEdge2 = v3 + Vec3(sign * 4f, -4f, 0f)
      modelPolygons.add(
        Poly3D(
          vertices = listOf(v4, v3, cuffEdge2, cuffEdge1),
          uv = listOf(0f to 0f, 1f to 0f, 1f to 1f, 0f to 1f),
          part = cuffPart
        )
      )
    }
  }

  private fun buildShorts() {
    val topY = 0f
    val midY = -25f
    val botY = -60f

    for (seg in 0 until 12) {
      val a1 = (seg * 2 * PI / 12).toFloat()
      val a2 = ((seg + 1) * 2 * PI / 12).toFloat()

      val r1 = 41f
      val r2 = 38f
      val v1 = Vec3(cos(a1) * r1 * 1.2f, topY, sin(a1) * r1 * 0.85f)
      val v2 = Vec3(cos(a2) * r1 * 1.2f, topY, sin(a2) * r1 * 0.85f)
      val v3 = Vec3(cos(a2) * r2 * 1.2f, midY, sin(a2) * r2 * 0.85f)
      val v4 = Vec3(cos(a1) * r2 * 1.2f, midY, sin(a1) * r2 * 0.85f)

      modelPolygons.add(
        Poly3D(
          vertices = listOf(v1, v2, v3, v4),
          uv = listOf(0f to 0f, 1f to 0f, 1f to 0.5f, 0f to 0.5f),
          part = MeshPart.SHORTS_PELVIS
        )
      )
    }

    buildShortsLeg(centerX = -19f, isLeft = true, startY = midY, endY = botY)
    buildShortsLeg(centerX = 19f, isLeft = false, startY = midY, endY = botY)
  }

  private fun buildShortsLeg(centerX: Float, isLeft: Boolean, startY: Float, endY: Float) {
    val numSeg = 10
    val rTop = 20f
    val rBot = 19f

    for (seg in 0 until numSeg) {
      val a1 = (seg * 2 * PI / numSeg).toFloat()
      val a2 = ((seg + 1) * 2 * PI / numSeg).toFloat()

      val v1 = Vec3(centerX + cos(a1) * rTop, startY, sin(a1) * rTop)
      val v2 = Vec3(centerX + cos(a2) * rTop, startY, sin(a2) * rTop)
      val v3 = Vec3(centerX + cos(a2) * rBot, endY, sin(a2) * rBot)
      val v4 = Vec3(centerX + cos(a1) * rBot, endY, sin(a1) * rBot)

      val part = if (isLeft) MeshPart.SHORTS_LEFT else MeshPart.SHORTS_RIGHT
      modelPolygons.add(
        Poly3D(
          vertices = listOf(v1, v2, v3, v4),
          uv = listOf(0.1f to 0.2f, 0.4f to 0.2f, 0.4f to 0.7f, 0.1f to 0.7f),
          part = part
        )
      )
    }
  }

  private fun buildSocksAndBoots() {
    buildSockAndBoot(centerX = -19f, isLeft = true)
    buildSockAndBoot(centerX = 19f, isLeft = false)
  }

  private fun buildSockAndBoot(centerX: Float, isLeft: Boolean) {
    val topY = -70f
    val botY = -150f
    val numSeg = 8
    val rSock = 12f

    for (seg in 0 until numSeg) {
      val a1 = (seg * 2 * PI / numSeg).toFloat()
      val a2 = ((seg + 1) * 2 * PI / numSeg).toFloat()

      val v1 = Vec3(centerX + cos(a1) * rSock, topY, sin(a1) * rSock)
      val v2 = Vec3(centerX + cos(a2) * rSock, topY, sin(a2) * rSock)
      val v3 = Vec3(centerX + cos(a2) * (rSock * 0.9f), botY, sin(a2) * (rSock * 0.9f))
      val v4 = Vec3(centerX + cos(a1) * (rSock * 0.9f), botY, sin(a1) * (rSock * 0.9f))

      val part = if (isLeft) MeshPart.SOCK_LEFT else MeshPart.SOCK_RIGHT
      modelPolygons.add(
        Poly3D(
          vertices = listOf(v1, v2, v3, v4),
          uv = listOf(0f to 0f, 1f to 0f, 1f to 1f, 0f to 1f),
          part = part
        )
      )
    }

    val bootY = -150f
    val soleY = -165f
    val bPart = if (isLeft) MeshPart.BOOT_LEFT else MeshPart.BOOT_RIGHT
    val bootV1 = Vec3(centerX - 10f, bootY, -8f)
    val bootV2 = Vec3(centerX + 10f, bootY, -8f)
    val bootV3 = Vec3(centerX + 8f, soleY, 26f)
    val bootV4 = Vec3(centerX - 8f, soleY, 26f)

    modelPolygons.add(
      Poly3D(
        vertices = listOf(bootV1, bootV2, bootV3, bootV4),
        uv = listOf(0f to 0f, 1f to 0f, 1f to 1f, 0f to 1f),
        part = bPart,
        baseColor = 0xFF1E293BL
      )
    )
  }

  fun render(
    drawScope: DrawScope,
    project: KitProject,
    shirtBitmap: Bitmap?,
    shortsBitmap: Bitmap?,
    socksBitmap: Bitmap?,
    yawDegrees: Float,
    pitchDegrees: Float,
    zoom: Float = 1.0f,
    showWireframe: Boolean = false,
    lightingPreset: LightingPreset = LightingPreset.STUDIO_NEUTRAL,
    modelFit: ModelFit = ModelFit.MODEL_0,
    collarTemplate: CollarTemplate = CollarTemplate.COLLAR_1,
    sportType: SportType = SportType.FOOTBALL
  ) {
    val w = drawScope.size.width
    val h = drawScope.size.height
    val centerX = w / 2f
    val centerY = h / 2f + 25f

    val radYaw = Math.toRadians(yawDegrees.toDouble()).toFloat()
    val radPitch = Math.toRadians(pitchDegrees.toDouble()).toFloat()

    val cosY = cos(radYaw)
    val sinY = sin(radYaw)
    val cosP = cos(radPitch)
    val sinP = sin(radPitch)

    val lightDir = when (lightingPreset) {
      LightingPreset.STUDIO_NEUTRAL -> Vec3(0.5f, 0.7f, 0.6f).normalized()
      LightingPreset.STADIUM_NIGHT -> Vec3(0.1f, 0.9f, 0.3f).normalized()
      LightingPreset.SUNSET_WARM -> Vec3(0.8f, 0.4f, 0.3f).normalized()
      LightingPreset.CYBER_NEON -> Vec3(-0.5f, 0.6f, 0.7f).normalized()
    }

    val cameraDistance = 450f
    val fov = 420f * zoom

    data class TransformedPoly(
      val screenPoints: List<Offset>,
      val avgZ: Float,
      val normal: Vec3,
      val part: MeshPart,
      val uv: List<Pair<Float, Float>>,
      val baseColor: Long
    )

    val transformedList = mutableListOf<TransformedPoly>()

    for (poly in modelPolygons) {
      // If Basketball, hide sleeve polygons (sleeveless tank jersey)
      if (sportType == SportType.BASKETBALL && (poly.part == MeshPart.SLEEVE_LEFT || poly.part == MeshPart.SLEEVE_RIGHT)) {
        continue
      }

      val transformedVerts = poly.vertices.map { v ->
        var px = v.x
        var py = v.y
        var pz = v.z

        // ModelFit adjustments (Model 0 - Model 4)
        when (modelFit) {
          ModelFit.MODEL_0 -> { // Fitted / tight
            if (py > 60f) px *= 1.08f
            if (py in 0f..60f) px *= 0.92f
          }
          ModelFit.MODEL_1 -> {} // Regular
          ModelFit.MODEL_2 -> { // Loose classic
            px *= 1.15f
            pz *= 1.12f
          }
          ModelFit.MODEL_3 -> { // Hanger
            pz *= 0.30f
          }
          ModelFit.MODEL_4 -> { // Flying
            px *= 1.06f
            if (py < 0f) py += 5f
          }
        }

        // Collar Template procedural geometry shaping
        if (poly.part == MeshPart.COLLAR) {
          when (collarTemplate) {
            CollarTemplate.COLLAR_1, CollarTemplate.COLLAR_3 -> { // V-Neck
              if (pz > 0f) py -= 8f
            }
            CollarTemplate.COLLAR_2, CollarTemplate.COLLAR_5 -> { // Polo collar
              if (py > 140f) px *= 1.12f
            }
            CollarTemplate.COLLAR_4, CollarTemplate.COLLAR_9 -> { // Stand / Mock
              py += 5f
            }
            else -> {}
          }
        }

        // Yaw & Pitch rotation
        val x1 = px * cosY + pz * sinY
        val z1 = -px * sinY + pz * cosY

        val y2 = py * cosP - z1 * sinP
        val z2 = py * sinP + z1 * cosP

        Vec3(x1, y2, z2)
      }

      val e1 = transformedVerts[1] - transformedVerts[0]
      val e2 = transformedVerts[2] - transformedVerts[0]
      val normal = e1.cross(e2).normalized()

      if (normal.z <= -0.2f) continue

      val screenPoints = transformedVerts.map { v ->
        val scale = fov / (cameraDistance + v.z)
        val sx = centerX + v.x * scale
        val sy = centerY - v.y * scale
        Offset(sx, sy)
      }

      val avgZ = transformedVerts.map { it.z }.average().toFloat()

      transformedList.add(
        TransformedPoly(
          screenPoints = screenPoints,
          avgZ = avgZ,
          normal = normal,
          part = poly.part,
          uv = poly.uv,
          baseColor = poly.baseColor
        )
      )
    }

    transformedList.sortBy { it.avgZ }

    val fillPaint = Paint().apply {
      isAntiAlias = true
      style = PaintingStyle.Fill
    }

    val strokePaint = Paint().apply {
      isAntiAlias = true
      style = PaintingStyle.Stroke
      strokeWidth = 1f
      color = Color(0x30FFFFFF)
    }

    drawScope.drawIntoCanvas { canvas ->
      for (poly in transformedList) {
        val path = Path().apply {
          moveTo(poly.screenPoints[0].x, poly.screenPoints[0].y)
          for (i in 1 until poly.screenPoints.size) {
            lineTo(poly.screenPoints[i].x, poly.screenPoints[i].y)
          }
          close()
        }

        val dot = max(0f, poly.normal.dot(lightDir))
        val ambient = when (lightingPreset) {
          LightingPreset.STUDIO_NEUTRAL -> 0.40f
          LightingPreset.STADIUM_NIGHT -> 0.32f
          LightingPreset.SUNSET_WARM -> 0.45f
          LightingPreset.CYBER_NEON -> 0.35f
        }
        val lightFactor = (ambient + dot * 0.60f).coerceIn(0.1f, 1.25f)

        val viewDir = Vec3(0f, 0f, 1f)
        val halfVec = (lightDir + viewDir).normalized()
        val specAngle = max(0f, poly.normal.dot(halfVec))
        val shininessExp = (project.globalShininess * 0.4f).coerceAtLeast(4f)
        val specFactor = Math.pow(specAngle.toDouble(), shininessExp.toDouble()).toFloat() * (project.globalShininess / 100f)

        val sampledColor = sampleColorForPart(
          poly.part,
          poly.uv,
          project,
          shirtBitmap,
          shortsBitmap,
          socksBitmap,
          poly.baseColor
        )

        val shadedColor = applyLightToColor(sampledColor, lightFactor, specFactor)
        fillPaint.color = shadedColor
        canvas.drawPath(path, fillPaint)

        if (project.fabricRoughness > 15f && poly.part in listOf(MeshPart.TORSO_FRONT, MeshPart.TORSO_BACK)) {
          canvas.drawPath(path, strokePaint)
        }

        if (showWireframe) {
          canvas.drawPath(path, strokePaint)
        }
      }
    }
  }

  private fun sampleColorForPart(
    part: MeshPart,
    uv: List<Pair<Float, Float>>,
    project: KitProject,
    shirtBmp: Bitmap?,
    shortsBmp: Bitmap?,
    socksBmp: Bitmap?,
    fallbackColor: Long
  ): Color {
    val avgU = (uv.map { it.first }.average().toFloat()).coerceIn(0f, 1f)
    val avgV = (uv.map { it.second }.average().toFloat()).coerceIn(0f, 1f)

    return when (part) {
      MeshPart.TORSO_FRONT, MeshPart.TORSO_BACK, MeshPart.TORSO_FLANK,
      MeshPart.SLEEVE_LEFT, MeshPart.SLEEVE_RIGHT -> {
        if (shirtBmp != null && !shirtBmp.isRecycled) {
          sampleBitmapSafe(shirtBmp, avgU, avgV)
        } else {
          val primary = project.shirtLayers.firstOrNull { it.type == com.example.model.LayerType.BASE_DESIGN }?.primaryColor ?: 0xFF0284C7L
          Color(primary)
        }
      }

      MeshPart.COLLAR, MeshPart.COLLAR_TRIM -> Color(project.collarColor)

      MeshPart.CUFF_LEFT, MeshPart.CUFF_RIGHT -> {
        val cuffLayer = project.shirtLayers.firstOrNull { it.type == com.example.model.LayerType.SLEEVE_CUFFS }
        if (cuffLayer != null) Color(cuffLayer.primaryColor) else Color(project.collarTrimColor)
      }

      MeshPart.SHORTS_PELVIS, MeshPart.SHORTS_LEFT, MeshPart.SHORTS_RIGHT -> {
        if (shortsBmp != null && !shortsBmp.isRecycled) {
          sampleBitmapSafe(shortsBmp, avgU, avgV)
        } else {
          val primary = project.shortsLayers.firstOrNull { it.type == com.example.model.LayerType.BASE_DESIGN }?.primaryColor ?: 0xFF0F172AL
          Color(primary)
        }
      }

      MeshPart.SOCK_LEFT, MeshPart.SOCK_RIGHT -> {
        if (socksBmp != null && !socksBmp.isRecycled) {
          sampleBitmapSafe(socksBmp, avgU, avgV)
        } else {
          val primary = project.socksLayers.firstOrNull { it.type == com.example.model.LayerType.BASE_DESIGN }?.primaryColor ?: 0xFF1E293BL
          Color(primary)
        }
      }

      MeshPart.BOOT_LEFT, MeshPart.BOOT_RIGHT -> Color(fallbackColor)
    }
  }

  private fun sampleBitmapSafe(bitmap: Bitmap, u: Float, v: Float): Color {
    val x = (u * (bitmap.width - 1)).toInt().coerceIn(0, bitmap.width - 1)
    val y = (v * (bitmap.height - 1)).toInt().coerceIn(0, bitmap.height - 1)
    val pixel = bitmap.getPixel(x, y)
    return Color(pixel)
  }

  private fun applyLightToColor(base: Color, lightFactor: Float, specFactor: Float): Color {
    val r = (base.red * lightFactor + specFactor).coerceIn(0f, 1f)
    val g = (base.green * lightFactor + specFactor).coerceIn(0f, 1f)
    val b = (base.blue * lightFactor + specFactor).coerceIn(0f, 1f)
    return Color(red = r, green = g, blue = b, alpha = 1f)
  }
}
