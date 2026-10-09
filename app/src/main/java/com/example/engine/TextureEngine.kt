package com.example.engine

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import com.example.model.CompetitionBadge
import com.example.model.KitComponentType
import com.example.model.KitLayer
import com.example.model.KitPatternType
import com.example.model.KitProject
import com.example.model.LayerBlendMode
import com.example.model.LayerMask
import com.example.model.LayerPlacement
import com.example.model.LayerType
import com.example.model.LogoType
import com.example.model.NumberConfig
import com.example.model.PlacementPreset
import com.example.model.PlayerNameConfig
import com.example.model.SportType
import com.example.model.TextFont
import kotlin.math.cos
import kotlin.math.sin

object TextureEngine {

  /**
   * Generates a 2D Texture Bitmap for a given component (e.g. 1024x1024 / 2048x2048).
   */
  fun renderComponentTexture(
    project: KitProject,
    component: KitComponentType,
    width: Int = 1024,
    height: Int = 1024
  ): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Transparent clear background
    canvas.drawColor(0x00000000, PorterDuff.Mode.CLEAR)

    // 1. Render all regular user layers
    val layers = project.getLayersFor(component).filter { it.visible }
    for (layer in layers) {
      renderLayer(canvas, layer, width, height, project, component)
    }

    // 2. Render Numbers & Player Name System (Module 2)
    if (component == KitComponentType.SHIRT) {
      renderPlayerName(canvas, project.playerName, width, height)
      renderShirtNumbers(canvas, project.numbers, width, height)
      renderCompetitionBadges(canvas, project.badges, width, height)
    } else if (component == KitComponentType.SHORTS) {
      renderShortsNumber(canvas, project.numbers, width, height)
    }

    return bitmap
  }

  fun renderLayer(
    canvas: Canvas,
    layer: KitLayer,
    w: Int,
    h: Int,
    project: KitProject,
    component: KitComponentType
  ) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      isFilterBitmap = true
      alpha = (layer.opacity.coerceIn(0f, 1f) * 255).toInt()
      xfermode = when (layer.blendMode) {
        LayerBlendMode.NORMAL -> null
        LayerBlendMode.MULTIPLY -> PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
        LayerBlendMode.OVERLAY -> PorterDuffXfermode(PorterDuff.Mode.OVERLAY)
        LayerBlendMode.SCREEN -> PorterDuffXfermode(PorterDuff.Mode.SCREEN)
        LayerBlendMode.DIFFERENCE -> PorterDuffXfermode(PorterDuff.Mode.DARKEN)
        LayerBlendMode.ADD -> PorterDuffXfermode(PorterDuff.Mode.ADD)
        LayerBlendMode.SUBTRACT -> PorterDuffXfermode(PorterDuff.Mode.DARKEN)
      }
    }

    // Saturation filter if not 100%
    if (layer.transform.saturation != 100f) {
      val cm = ColorMatrix().apply { setSaturation(layer.transform.saturation / 100f) }
      paint.colorFilter = ColorMatrixColorFilter(cm)
    }

    canvas.save()

    // Apply Mask Clipping
    applyMaskClip(canvas, layer.mask, w, h, component)

    // Calculate Center Position
    val effectiveX = if (layer.placementPreset != PlacementPreset.CUSTOM) layer.placementPreset.x else layer.transform.x
    val effectiveY = if (layer.placementPreset != PlacementPreset.CUSTOM) layer.placementPreset.y else layer.transform.y
    val centerX = w / 2f + (effectiveX / 100f) * (w / 2f)
    val centerY = h / 2f + (effectiveY / 100f) * (h / 2f)

    when (layer.type) {
      LayerType.BASE_DESIGN -> {
        paint.color = layer.primaryColor.toInt()
        paint.style = Paint.Style.FILL
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
      }

      LayerType.PATTERN -> {
        renderPattern(canvas, layer, w, h, paint)
      }

      LayerType.GRAPHIC -> {
        drawTransformed(canvas, centerX, centerY, layer) {
          renderGraphic(canvas, layer, w, h, paint)
        }
      }

      LayerType.TEXT -> {
        drawTransformed(canvas, centerX, centerY, layer) {
          renderText(canvas, layer, paint)
        }
      }

      LayerType.LOGO -> {
        drawTransformed(canvas, centerX, centerY, layer) {
          renderLogo(canvas, layer, paint)
        }

        // Mirror / Mirror Center option
        if (layer.mirror || layer.mirrorCenter) {
          val mirrorX = if (layer.mirrorCenter) centerX else w - centerX
          drawTransformed(canvas, mirrorX, centerY, layer, scaleXMult = -1f) {
            renderLogo(canvas, layer, paint)
          }
        }
      }

      LayerType.SLEEVE_CUFFS -> {
        paint.color = layer.primaryColor.toInt()
        paint.style = Paint.Style.FILL
        val cuffH = h * 0.08f
        canvas.drawRect(0f, h * 0.42f, w * 0.25f, h * 0.42f + cuffH, paint)
        canvas.drawRect(w * 0.75f, h * 0.42f, w.toFloat(), h * 0.42f + cuffH, paint)
      }

      LayerType.SIDE_PANELS -> {
        paint.color = layer.primaryColor.toInt()
        paint.style = Paint.Style.FILL
        val panelW = w * 0.08f
        canvas.drawRect(w * 0.22f, h * 0.2f, w * 0.22f + panelW, h * 0.95f, paint)
        canvas.drawRect(w * 0.78f - panelW, h * 0.2f, w * 0.78f, h * 0.95f, paint)
      }

      LayerType.SHOULDER_PANELS -> {
        paint.color = layer.primaryColor.toInt()
        paint.style = Paint.Style.FILL
        val path = Path().apply {
          moveTo(w * 0.2f, 0f); lineTo(w * 0.38f, 0f); lineTo(w * 0.32f, h * 0.22f); lineTo(w * 0.15f, h * 0.22f); close()
          moveTo(w * 0.8f, 0f); lineTo(w * 0.62f, 0f); lineTo(w * 0.68f, h * 0.22f); lineTo(w * 0.85f, h * 0.22f); close()
        }
        canvas.drawPath(path, paint)
      }

      LayerType.GROUP -> {
        // Group container
      }
    }

    // Fabric print subtle sublimation texture overlay
    if (layer.fabricPrint) {
      val weavePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 1f
        color = 0x1AFFFFFF.toInt()
      }
      for (y in 0 until h step 8) {
        canvas.drawLine(0f, y.toFloat(), w.toFloat(), y.toFloat(), weavePaint)
      }
    }

    canvas.restore()
  }

  private inline fun drawTransformed(
    canvas: Canvas,
    cx: Float,
    cy: Float,
    layer: KitLayer,
    scaleXMult: Float = 1f,
    drawBlock: () -> Unit
  ) {
    canvas.save()
    canvas.translate(cx, cy)
    canvas.rotate(layer.transform.rotation)
    val heightScale = 1f + (layer.transform.heightRatio / 100f)
    canvas.scale(layer.transform.scaleX * scaleXMult, layer.transform.scaleY * heightScale)
    drawBlock()
    canvas.restore()
  }

  private fun applyMaskClip(canvas: Canvas, mask: LayerMask, w: Int, h: Int, component: KitComponentType) {
    if (mask == LayerMask.FULL) return

    val clipPath = Path()
    when (mask) {
      LayerMask.FULL -> return
      LayerMask.TORSO_FRONT, LayerMask.TORSO_BACK -> clipPath.addRect(w * 0.25f, 0f, w * 0.75f, h.toFloat(), Path.Direction.CW)
      LayerMask.SLEEVE_LEFT -> clipPath.addRect(0f, 0f, w * 0.25f, h * 0.5f, Path.Direction.CW)
      LayerMask.SLEEVE_RIGHT -> clipPath.addRect(w * 0.75f, 0f, w.toFloat(), h * 0.5f, Path.Direction.CW)
      LayerMask.COLLAR -> clipPath.addCircle(w / 2f, h * 0.12f, w * 0.15f, Path.Direction.CW)
      LayerMask.SIDE_LEFT -> clipPath.addRect(0f, 0f, w * 0.3f, h.toFloat(), Path.Direction.CW)
      LayerMask.SIDE_RIGHT -> clipPath.addRect(w * 0.7f, 0f, w.toFloat(), h.toFloat(), Path.Direction.CW)
      LayerMask.CUFFS -> clipPath.addRect(0f, h * 0.35f, w.toFloat(), h * 0.55f, Path.Direction.CW)
    }
    canvas.clipPath(clipPath)
  }

  private fun renderPattern(canvas: Canvas, layer: KitLayer, w: Int, h: Int, paint: Paint) {
    val col1 = layer.primaryColor.toInt()
    val col2 = layer.secondaryColor.toInt()

    when (layer.patternType) {
      KitPatternType.STRIPES_VERTICAL -> {
        val count = layer.patternFrequency.coerceAtLeast(2)
        val stripeWidth = w.toFloat() / count
        for (i in 0 until count) {
          paint.color = if (i % 2 == 0) col1 else col2
          paint.style = Paint.Style.FILL
          canvas.drawRect(i * stripeWidth, 0f, (i + 1) * stripeWidth, h.toFloat(), paint)
        }
      }

      KitPatternType.STRIPES_HORIZONTAL -> {
        val count = layer.patternFrequency.coerceAtLeast(2)
        val stripeHeight = h.toFloat() / count
        for (i in 0 until count) {
          paint.color = if (i % 2 == 0) col1 else col2
          paint.style = Paint.Style.FILL
          canvas.drawRect(0f, i * stripeHeight, w.toFloat(), (i + 1) * stripeHeight, paint)
        }
      }

      KitPatternType.CHECKERBOARD -> {
        val count = layer.patternFrequency.coerceAtLeast(2)
        val cellW = w.toFloat() / count
        val cellH = h.toFloat() / count
        for (row in 0 until count) {
          for (col in 0 until count) {
            paint.color = if ((row + col) % 2 == 0) col1 else col2
            paint.style = Paint.Style.FILL
            canvas.drawRect(col * cellW, row * cellH, (col + 1) * cellW, (row + 1) * cellH, paint)
          }
        }
      }

      KitPatternType.CHEVRON -> {
        val count = layer.patternFrequency.coerceAtLeast(2)
        val step = h.toFloat() / count
        paint.color = col2
        paint.style = Paint.Style.FILL
        for (i in 0 until count) {
          if (i % 2 == 1) {
            val path = Path().apply {
              moveTo(0f, i * step); lineTo(w / 2f, (i + 0.5f) * step); lineTo(w.toFloat(), i * step)
              lineTo(w.toFloat(), (i + 1) * step); lineTo(w / 2f, (i + 1.5f) * step); lineTo(0f, (i + 1) * step); close()
            }
            canvas.drawPath(path, paint)
          }
        }
      }

      KitPatternType.PINSTRIPES -> {
        val count = (layer.patternFrequency * 2).coerceAtLeast(4)
        val spacing = w.toFloat() / count
        paint.color = col1; paint.style = Paint.Style.FILL; canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
        paint.color = col2; paint.strokeWidth = 4f; paint.style = Paint.Style.STROKE
        for (i in 0 until count) canvas.drawLine(i * spacing, 0f, i * spacing, h.toFloat(), paint)
      }

      KitPatternType.HALVES -> {
        paint.color = col1; paint.style = Paint.Style.FILL; canvas.drawRect(0f, 0f, w / 2f, h.toFloat(), paint)
        paint.color = col2; canvas.drawRect(w / 2f, 0f, w.toFloat(), h.toFloat(), paint)
      }

      KitPatternType.SASH_DIAGONAL -> {
        paint.color = col1; paint.style = Paint.Style.FILL; canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
        paint.color = col2
        val sashW = w * 0.22f
        val path = Path().apply {
          moveTo(0f, 0f); lineTo(sashW, 0f); lineTo(w.toFloat(), h.toFloat()); lineTo(w.toFloat() - sashW, h.toFloat()); close()
        }
        canvas.drawPath(path, paint)
      }

      KitPatternType.GRADIENT_LINEAR -> {
        paint.shader = LinearGradient(0f, 0f, 0f, h.toFloat(), intArrayOf(col1, col2), null, Shader.TileMode.CLAMP)
        paint.style = Paint.Style.FILL
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
        paint.shader = null
      }

      KitPatternType.DIAMONDS -> {
        val count = layer.patternFrequency.coerceAtLeast(4)
        val dW = w.toFloat() / count
        val dH = h.toFloat() / count
        paint.color = col1; paint.style = Paint.Style.FILL; canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
        paint.color = col2
        for (y in 0 until count * 2) {
          for (x in 0 until count * 2) {
            if ((x + y) % 2 == 0) {
              val cx = x * dW / 2
              val cy = y * dH / 2
              val path = Path().apply {
                moveTo(cx, cy - dH / 4); lineTo(cx + dW / 4, cy); lineTo(cx, cy + dH / 4); lineTo(cx - dW / 4, cy); close()
              }
              canvas.drawPath(path, paint)
            }
          }
        }
      }

      KitPatternType.HEX_HONEYCOMB -> {
        paint.color = col1; paint.style = Paint.Style.FILL; canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
        paint.color = col2; paint.style = Paint.Style.STROKE; paint.strokeWidth = 3f
        val r = (w.toFloat() / (layer.patternFrequency * 2)).coerceAtLeast(16f)
        var row = 0
        var curY = 0f
        while (curY < h + r) {
          val offsetX = if (row % 2 == 1) r * 1.5f else 0f
          var curX = -r + offsetX
          while (curX < w + r) {
            drawHexagon(canvas, curX, curY, r, paint)
            curX += r * 3f
          }
          curY += r * 1.732f / 2
          row++
        }
      }

      KitPatternType.WAVES -> {
        paint.color = col1; paint.style = Paint.Style.FILL; canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
        paint.color = col2; paint.style = Paint.Style.STROKE; paint.strokeWidth = 6f
        val rows = layer.patternFrequency.coerceAtLeast(4)
        val step = h.toFloat() / rows
        for (i in 0..rows) {
          val yBase = i * step
          val path = Path().apply {
            moveTo(0f, yBase)
            for (x in 0..w step 40) lineTo(x.toFloat(), (yBase + sin(x * 0.05) * 15f).toFloat())
          }
          canvas.drawPath(path, paint)
        }
      }

      KitPatternType.CAMOUFLAGE -> {
        paint.color = col1; paint.style = Paint.Style.FILL; canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
        paint.color = col2; paint.style = Paint.Style.FILL
        for (i in 0 until 18) {
          val rx = ((i * 137) % w).toFloat()
          val ry = ((i * 249) % h).toFloat()
          canvas.drawRoundRect(rx, ry, rx + w * 0.15f, ry + h * 0.08f, 30f, 30f, paint)
        }
      }

      KitPatternType.ZIG_ZAG -> {
        paint.color = col1; paint.style = Paint.Style.FILL; canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
        paint.color = col2; paint.style = Paint.Style.STROKE; paint.strokeWidth = 8f
        val count = layer.patternFrequency.coerceAtLeast(3)
        val step = h.toFloat() / count
        for (row in 0..count) {
          val y0 = row * step
          val path = Path().apply {
            moveTo(0f, y0)
            for (x in 0..w step 30) {
              val up = (x / 30) % 2 == 1
              lineTo(x.toFloat(), y0 + if (up) -15f else 15f)
            }
          }
          canvas.drawPath(path, paint)
        }
      }

      KitPatternType.CROSS_PATTERN -> {
        paint.color = col1; paint.style = Paint.Style.FILL; canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
        paint.color = col2; paint.style = Paint.Style.FILL
        val barW = w * 0.12f
        canvas.drawRect(w * 0.35f, 0f, w * 0.35f + barW, h.toFloat(), paint)
        canvas.drawRect(0f, h * 0.35f, w.toFloat(), h * 0.35f + barW, paint)
      }

      KitPatternType.DOTS_HALFTONE -> {
        paint.color = col1; paint.style = Paint.Style.FILL; canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
        paint.color = col2; paint.style = Paint.Style.FILL
        val count = layer.patternFrequency.coerceAtLeast(6)
        val step = w.toFloat() / count
        for (r in 0 until count) {
          for (c in 0 until count) {
            val radius = (c.toFloat() / count) * 12f + 2f
            canvas.drawCircle(c * step + step / 2, r * step + step / 2, radius, paint)
          }
        }
      }

      KitPatternType.MOSAIC_TILES -> {
        paint.color = col1; paint.style = Paint.Style.FILL; canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
        paint.color = col2; paint.style = Paint.Style.FILL
        val count = layer.patternFrequency.coerceAtLeast(8)
        val step = w.toFloat() / count
        for (r in 0 until count) {
          for (c in 0 until count) {
            if ((r * 7 + c * 3) % 4 == 0) {
              canvas.drawRect(c * step + 2f, r * step + 2f, (c + 1) * step - 2f, (r + 1) * step - 2f, paint)
            }
          }
        }
      }

      KitPatternType.MARBLE_TEXTURE, KitPatternType.ABSTRACT_BRUSH -> {
        paint.color = col1; paint.style = Paint.Style.FILL; canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
        paint.color = col2; paint.style = Paint.Style.STROKE; paint.strokeWidth = 5f
        for (i in 0 until 12) {
          val yStart = (i * (h / 12f))
          val path = Path().apply {
            moveTo(0f, yStart)
            cubicTo(w * 0.3f, yStart - 40f, w * 0.6f, yStart + 40f, w.toFloat(), yStart)
          }
          canvas.drawPath(path, paint)
        }
      }
    }
  }

  private fun drawHexagon(canvas: Canvas, cx: Float, cy: Float, radius: Float, paint: Paint) {
    val path = Path()
    for (i in 0..5) {
      val angle = Math.toRadians((60 * i - 30).toDouble())
      val x = cx + radius * cos(angle).toFloat()
      val y = cy + radius * sin(angle).toFloat()
      if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    canvas.drawPath(path, paint)
  }

  private fun renderGraphic(canvas: Canvas, layer: KitLayer, w: Int, h: Int, paint: Paint) {
    paint.color = layer.primaryColor.toInt(); paint.style = Paint.Style.FILL
    val path = Path().apply {
      moveTo(-120f, -60f); lineTo(120f, -30f); lineTo(80f, 40f); lineTo(-160f, 10f); close()
    }
    canvas.drawPath(path, paint)

    paint.color = layer.secondaryColor.toInt()
    val path2 = Path().apply {
      moveTo(-100f, -80f); lineTo(140f, -50f); lineTo(120f, -35f); lineTo(-120f, -65f); close()
    }
    canvas.drawPath(path2, paint)

    // Optional border stroke
    if (layer.border > 0f) {
      paint.style = Paint.Style.STROKE
      paint.strokeWidth = layer.border * 2f
      paint.color = layer.strokeColor.toInt()
      canvas.drawPath(path, paint)
    }
  }

  private fun renderText(canvas: Canvas, layer: KitLayer, paint: Paint) {
    paint.textSize = layer.textSize * 2.2f
    paint.textAlign = Paint.Align.CENTER
    paint.typeface = getTypeface(layer.textFont)

    if (layer.hasShadow) {
      paint.setShadowLayer(10f, 4f, 4f, 0x88000000.toInt())
    }

    if (layer.strokeWidth > 0f) {
      paint.style = Paint.Style.STROKE
      paint.strokeWidth = layer.strokeWidth * 3f
      paint.color = layer.strokeColor.toInt()
      canvas.drawText(layer.text, 0f, 0f, paint)
    }

    paint.style = Paint.Style.FILL
    paint.color = layer.primaryColor.toInt()
    canvas.drawText(layer.text, 0f, 0f, paint)
    paint.clearShadowLayer()
  }

  private fun renderLogo(canvas: Canvas, layer: KitLayer, paint: Paint) {
    val col1 = layer.primaryColor.toInt()
    val col2 = layer.secondaryColor.toInt()
    val col3 = layer.tertiaryColor.toInt()

    when (layer.logoType) {
      LogoType.CREST_SHIELD -> {
        paint.style = Paint.Style.FILL; paint.color = col1
        val shield = Path().apply {
          moveTo(0f, -50f); lineTo(45f, -50f); lineTo(45f, 0f)
          cubicTo(45f, 35f, 25f, 55f, 0f, 65f)
          cubicTo(-25f, 55f, -45f, 35f, -45f, 0f); lineTo(-45f, -50f); close()
        }
        canvas.drawPath(shield, paint)
        paint.style = Paint.Style.STROKE; paint.strokeWidth = 6f; paint.color = col3
        canvas.drawPath(shield, paint)
        drawStar(canvas, 0f, -15f, 14f, col3, paint)
      }

      LogoType.CREST_ROUNDEL -> {
        paint.style = Paint.Style.FILL; paint.color = col1; canvas.drawCircle(0f, 0f, 50f, paint)
        paint.style = Paint.Style.STROKE; paint.strokeWidth = 6f; paint.color = col3; canvas.drawCircle(0f, 0f, 46f, paint)
        paint.style = Paint.Style.FILL; paint.color = col2; canvas.drawCircle(0f, 0f, 32f, paint)
        drawStar(canvas, 0f, 0f, 18f, col3, paint)
      }

      LogoType.BRAND_SWOOSH -> {
        paint.style = Paint.Style.FILL; paint.color = col1
        val swoosh = Path().apply {
          moveTo(-45f, 10f); cubicTo(-20f, 25f, 15f, 22f, 45f, -25f)
          cubicTo(20f, 5f, -5f, 12f, -30f, 2f); cubicTo(-38f, -2f, -42f, 4f, -45f, 10f); close()
        }
        canvas.drawPath(swoosh, paint)
      }

      LogoType.BRAND_TRIPLE_STRIPES -> {
        paint.style = Paint.Style.FILL; paint.color = col1
        val p1 = Path().apply { moveTo(12f, -35f); lineTo(24f, -35f); lineTo(49f, 35f); lineTo(37f, 35f); close() }
        val p2 = Path().apply { moveTo(-6f, -15f); lineTo(6f, -15f); lineTo(31f, 35f); lineTo(19f, 35f); close() }
        val p3 = Path().apply { moveTo(-24f, 5f); lineTo(-12f, 5f); lineTo(13f, 35f); lineTo(1f, 35f); close() }
        canvas.drawPath(p1, paint); canvas.drawPath(p2, paint); canvas.drawPath(p3, paint)
      }

      LogoType.SPONSOR_AIRWAYS -> {
        paint.color = col1; paint.style = Paint.Style.FILL; paint.textAlign = Paint.Align.CENTER
        paint.textSize = 28f; paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        canvas.drawText("Fly Better", 0f, -12f, paint)
        paint.textSize = 34f; paint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        canvas.drawText("EMIRATES", 0f, 24f, paint)
      }

      LogoType.BADGE_CHAMPIONS -> {
        paint.style = Paint.Style.FILL; paint.color = col3
        canvas.drawCircle(0f, 0f, 40f, paint)
        paint.color = 0xFF0F172A.toInt()
        paint.style = Paint.Style.STROKE; paint.strokeWidth = 4f
        canvas.drawCircle(0f, 0f, 36f, paint)
        drawStar(canvas, 0f, 0f, 22f, col1, paint)
      }

      else -> {
        // Fallback Star Emblem
        drawStar(canvas, 0f, 0f, 42f, col1, paint)
      }
    }
  }

  // ─── NUMBERS & PLAYER NAME RENDERING (Module 2) ───────────────────────────

  private fun renderPlayerName(canvas: Canvas, config: PlayerNameConfig, w: Int, h: Int) {
    if (!config.enabled || config.name.isBlank()) return
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = config.color.toInt()
      textSize = config.width * 0.7f
      textAlign = Paint.Align.CENTER
      typeface = getTypeface(config.font)
    }

    val cx = w / 2f + (config.posX / 100f) * (w / 2f)
    val cy = h / 2f + (config.posY / 100f) * (h / 2f)

    if (config.arc != 0f) {
      // Arched curved text path
      val arcPath = Path()
      val radius = (w * 0.4f).coerceAtLeast(100f)
      val oval = RectF(cx - radius, cy - 30f, cx + radius, cy + radius * 2)
      val sweep = (config.arc * 1.5f).coerceIn(-120f, 120f)
      arcPath.addArc(oval, 270f - sweep / 2f, sweep)
      canvas.drawTextOnPath(config.name, arcPath, 0f, 0f, paint)
    } else {
      canvas.drawText(config.name, cx, cy, paint)
    }
  }

  private fun renderShirtNumbers(canvas: Canvas, config: NumberConfig, w: Int, h: Int) {
    if (!config.enabled || config.value.isBlank()) return

    // 1. Back Number
    renderNumberText(
      canvas = canvas,
      text = config.value,
      cx = w / 2f,
      cy = h * 0.52f + config.offset * 2f,
      size = config.size * 2.4f,
      config = config
    )

    // 2. Front Number (if enabled)
    if (config.frontNumberEnabled) {
      val fx = w / 2f + (config.frontNumberX / 100f) * (w / 2f)
      val fy = h / 2f + (config.frontNumberY / 100f) * (h / 2f)
      renderNumberText(
        canvas = canvas,
        text = config.value,
        cx = fx,
        cy = fy,
        size = config.frontNumberSize * 2.2f,
        config = config
      )
    }
  }

  private fun renderShortsNumber(canvas: Canvas, config: NumberConfig, w: Int, h: Int) {
    if (!config.enabled || !config.shortsNumberEnabled || config.value.isBlank()) return
    val sx = w / 2f + (config.shortsNumberX / 100f) * (w / 2f)
    val sy = h / 2f + (config.shortsNumberY / 100f) * (h / 2f)
    renderNumberText(
      canvas = canvas,
      text = config.value,
      cx = sx,
      cy = sy,
      size = config.shortsNumberSize * 2.2f,
      config = config
    )
  }

  private fun renderNumberText(canvas: Canvas, text: String, cx: Float, cy: Float, size: Float, config: NumberConfig) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      textSize = size
      textAlign = Paint.Align.CENTER
      typeface = getTypeface(config.font)
    }

    // Border / Outline if enabled
    if (config.borderEnabled && config.borderWidth > 0f) {
      paint.style = Paint.Style.STROKE
      paint.strokeWidth = config.borderWidth * 3.5f
      paint.color = config.borderColor.toInt()
      canvas.drawText(text, cx, cy, paint)
    }

    // Main Fill
    paint.style = Paint.Style.FILL
    paint.color = config.color.toInt()
    canvas.drawText(text, cx, cy, paint)
  }

  private fun renderCompetitionBadges(canvas: Canvas, badges: List<CompetitionBadge>, w: Int, h: Int) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    badges.filter { it.enabled }.forEach { badge ->
      val cx = w / 2f + (badge.posX / 100f) * (w / 2f)
      val cy = h / 2f + (badge.posY / 100f) * (h / 2f)
      canvas.save()
      canvas.translate(cx, cy)
      val scale = badge.size / 50f
      canvas.scale(scale, scale)
      drawStar(canvas, 0f, 0f, 22f, badge.color.toInt(), paint)
      canvas.restore()
    }
  }

  private fun getTypeface(font: TextFont): Typeface = when (font) {
    TextFont.ANTON, TextFont.ARCHIVO_BLACK -> Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
    TextFont.BEBAS_NEUE, TextFont.TEKO, TextFont.BARLOW_CONDENSED, TextFont.OSWALD -> Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
    TextFont.RAJDHANI -> Typeface.create(Typeface.MONOSPACE, Typeface.BOLD_ITALIC)
    TextFont.ATHLETIC_BLOCK -> Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
    TextFont.VINTAGE_SERIF -> Typeface.create(Typeface.SERIF, Typeface.BOLD)
  }

  private fun drawStar(canvas: Canvas, cx: Float, cy: Float, radius: Float, color: Int, paint: Paint) {
    paint.color = color; paint.style = Paint.Style.FILL
    val path = Path()
    val innerR = radius * 0.42f
    for (i in 0 until 10) {
      val r = if (i % 2 == 0) radius else innerR
      val angle = Math.toRadians((i * 36 - 90).toDouble())
      val x = cx + r * cos(angle).toFloat()
      val y = cy + r * sin(angle).toFloat()
      if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    canvas.drawPath(path, paint)
  }
}
