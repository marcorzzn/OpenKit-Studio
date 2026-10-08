package com.example.engine

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import com.example.model.CollarStyle
import com.example.model.KitComponentType
import com.example.model.KitLayer
import com.example.model.KitPatternType
import com.example.model.KitProject
import com.example.model.LayerBlendMode
import com.example.model.LayerMask
import com.example.model.LayerType
import com.example.model.LogoType
import com.example.model.TextFont
import kotlin.math.cos
import kotlin.math.sin

object TextureEngine {

  /**
   * Generates a 2D Texture Bitmap for a given component (e.g. 1024x1024).
   */
  fun renderComponentTexture(
    project: KitProject,
    component: KitComponentType,
    width: Int = 1024,
    height: Int = 1024
  ): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Base background fill
    canvas.drawColor(0x00000000, PorterDuff.Mode.CLEAR)

    val layers = project.getLayersFor(component).filter { it.visible }

    for (layer in layers) {
      renderLayer(canvas, layer, width, height, project, component)
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
      }
    }

    canvas.save()

    // Apply Mask Clipping if necessary
    applyMaskClip(canvas, layer.mask, w, h, component)

    // Apply Transforms
    val centerX = w / 2f + (layer.transform.x / 100f) * (w / 2f)
    val centerY = h / 2f + (layer.transform.y / 100f) * (h / 2f)

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
        canvas.translate(centerX, centerY)
        canvas.rotate(layer.transform.rotation)
        canvas.scale(layer.transform.scaleX, layer.transform.scaleY)
        renderGraphic(canvas, layer, w, h, paint)
      }

      LayerType.TEXT -> {
        canvas.translate(centerX, centerY)
        canvas.rotate(layer.transform.rotation)
        canvas.scale(layer.transform.scaleX, layer.transform.scaleY)
        renderText(canvas, layer, paint)
      }

      LayerType.LOGO -> {
        canvas.translate(centerX, centerY)
        canvas.rotate(layer.transform.rotation)
        canvas.scale(layer.transform.scaleX, layer.transform.scaleY)
        renderLogo(canvas, layer, paint)
      }

      LayerType.SLEEVE_CUFFS -> {
        paint.color = layer.primaryColor.toInt()
        paint.style = Paint.Style.FILL
        // Draw cuffs at sleeve boundaries
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
          moveTo(w * 0.2f, 0f)
          lineTo(w * 0.38f, 0f)
          lineTo(w * 0.32f, h * 0.22f)
          lineTo(w * 0.15f, h * 0.22f)
          close()
          moveTo(w * 0.8f, 0f)
          lineTo(w * 0.62f, 0f)
          lineTo(w * 0.68f, h * 0.22f)
          lineTo(w * 0.85f, h * 0.22f)
          close()
        }
        canvas.drawPath(path, paint)
      }

      LayerType.GROUP -> {
        // Folder container for grouping
      }
    }

    canvas.restore()
  }

  private fun applyMaskClip(canvas: Canvas, mask: LayerMask, w: Int, h: Int, component: KitComponentType) {
    if (mask == LayerMask.FULL) return

    val clipPath = Path()
    when (mask) {
      LayerMask.TORSO_FRONT -> {
        clipPath.addRect(w * 0.25f, 0f, w * 0.75f, h.toFloat(), Path.Direction.CW)
      }
      LayerMask.TORSO_BACK -> {
        clipPath.addRect(w * 0.25f, 0f, w * 0.75f, h.toFloat(), Path.Direction.CW)
      }
      LayerMask.SLEEVE_LEFT -> {
        clipPath.addRect(0f, 0f, w * 0.25f, h * 0.5f, Path.Direction.CW)
      }
      LayerMask.SLEEVE_RIGHT -> {
        clipPath.addRect(w * 0.75f, 0f, w.toFloat(), h * 0.5f, Path.Direction.CW)
      }
      LayerMask.COLLAR -> {
        clipPath.addCircle(w / 2f, h * 0.12f, w * 0.15f, Path.Direction.CW)
      }
      LayerMask.SIDE_LEFT -> {
        clipPath.addRect(0f, 0f, w * 0.3f, h.toFloat(), Path.Direction.CW)
      }
      LayerMask.SIDE_RIGHT -> {
        clipPath.addRect(w * 0.7f, 0f, w.toFloat(), h.toFloat(), Path.Direction.CW)
      }
      LayerMask.CUFFS -> {
        clipPath.addRect(0f, h * 0.35f, w.toFloat(), h * 0.55f, Path.Direction.CW)
      }
      else -> return
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
              moveTo(0f, i * step)
              lineTo(w / 2f, (i + 0.5f) * step)
              lineTo(w.toFloat(), i * step)
              lineTo(w.toFloat(), (i + 1) * step)
              lineTo(w / 2f, (i + 1.5f) * step)
              lineTo(0f, (i + 1) * step)
              close()
            }
            canvas.drawPath(path, paint)
          }
        }
      }

      KitPatternType.PINSTRIPES -> {
        val count = (layer.patternFrequency * 2).coerceAtLeast(4)
        val spacing = w.toFloat() / count
        paint.color = col1
        paint.style = Paint.Style.FILL
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)

        paint.color = col2
        paint.strokeWidth = 4f
        paint.style = Paint.Style.STROKE
        for (i in 0 until count) {
          canvas.drawLine(i * spacing, 0f, i * spacing, h.toFloat(), paint)
        }
      }

      KitPatternType.HALVES -> {
        paint.color = col1
        paint.style = Paint.Style.FILL
        canvas.drawRect(0f, 0f, w / 2f, h.toFloat(), paint)
        paint.color = col2
        canvas.drawRect(w / 2f, 0f, w.toFloat(), h.toFloat(), paint)
      }

      KitPatternType.SASH_DIAGONAL -> {
        paint.color = col1
        paint.style = Paint.Style.FILL
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)

        paint.color = col2
        val sashW = w * 0.22f
        val path = Path().apply {
          moveTo(0f, 0f)
          lineTo(sashW, 0f)
          lineTo(w.toFloat(), h.toFloat())
          lineTo(w.toFloat() - sashW, h.toFloat())
          close()
        }
        canvas.drawPath(path, paint)
      }

      KitPatternType.GRADIENT_LINEAR -> {
        val shader = LinearGradient(
          0f, 0f, 0f, h.toFloat(),
          intArrayOf(col1, col2),
          null,
          Shader.TileMode.CLAMP
        )
        paint.shader = shader
        paint.style = Paint.Style.FILL
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
        paint.shader = null
      }

      KitPatternType.DIAMONDS -> {
        val count = layer.patternFrequency.coerceAtLeast(4)
        val dW = w.toFloat() / count
        val dH = h.toFloat() / count
        paint.color = col1
        paint.style = Paint.Style.FILL
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)

        paint.color = col2
        for (y in 0 until count * 2) {
          for (x in 0 until count * 2) {
            if ((x + y) % 2 == 0) {
              val cx = x * dW / 2
              val cy = y * dH / 2
              val path = Path().apply {
                moveTo(cx, cy - dH / 4)
                lineTo(cx + dW / 4, cy)
                lineTo(cx, cy + dH / 4)
                lineTo(cx - dW / 4, cy)
                close()
              }
              canvas.drawPath(path, paint)
            }
          }
        }
      }

      KitPatternType.HEX_HONEYCOMB -> {
        paint.color = col1
        paint.style = Paint.Style.FILL
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)

        paint.color = col2
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f
        val r = (w.toFloat() / (layer.patternFrequency * 2)).coerceAtLeast(16f)
        val hStep = r * 1.5f
        val vStep = r * 1.732f

        var row = 0
        var curY = 0f
        while (curY < h + r) {
          val offsetX = if (row % 2 == 1) hStep else 0f
          var curX = -r + offsetX
          while (curX < w + r) {
            drawHexagon(canvas, curX, curY, r, paint)
            curX += hStep * 2
          }
          curY += vStep / 2
          row++
        }
      }

      KitPatternType.WAVES -> {
        paint.color = col1
        paint.style = Paint.Style.FILL
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)

        paint.color = col2
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 6f
        val rows = layer.patternFrequency.coerceAtLeast(4)
        val step = h.toFloat() / rows
        for (i in 0..rows) {
          val yBase = i * step
          val path = Path().apply {
            moveTo(0f, yBase)
            for (x in 0..w step 40) {
              val waveY = yBase + sin(x * 0.05) * 15f
              lineTo(x.toFloat(), waveY.toFloat())
            }
          }
          canvas.drawPath(path, paint)
        }
      }

      KitPatternType.CAMOUFLAGE -> {
        paint.color = col1
        paint.style = Paint.Style.FILL
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)

        paint.color = col2
        paint.style = Paint.Style.FILL
        for (i in 0 until 18) {
          val rx = ((i * 137) % w).toFloat()
          val ry = ((i * 249) % h).toFloat()
          val rw = (w * 0.15f)
          val rh = (h * 0.08f)
          canvas.drawRoundRect(rx, ry, rx + rw, ry + rh, 30f, 30f, paint)
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
    paint.color = layer.primaryColor.toInt()
    paint.style = Paint.Style.FILL
    // Modern athletic geometric chevron slash
    val path = Path().apply {
      moveTo(-120f, -60f)
      lineTo(120f, -30f)
      lineTo(80f, 40f)
      lineTo(-160f, 10f)
      close()
    }
    canvas.drawPath(path, paint)

    paint.color = layer.secondaryColor.toInt()
    val path2 = Path().apply {
      moveTo(-100f, -80f)
      lineTo(140f, -50f)
      lineTo(120f, -35f)
      lineTo(-120f, -65f)
      close()
    }
    canvas.drawPath(path2, paint)
  }

  private fun renderText(canvas: Canvas, layer: KitLayer, paint: Paint) {
    paint.textSize = layer.textSize * 2.2f
    paint.textAlign = Paint.Align.CENTER
    paint.typeface = when (layer.textFont) {
      TextFont.ATHLETIC_BLOCK -> Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
      TextFont.MODERN_SANS -> Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
      TextFont.VINTAGE_SERIF -> Typeface.create(Typeface.SERIF, Typeface.BOLD)
      TextFont.STENCIL_FUTURISTIC -> Typeface.create(Typeface.MONOSPACE, Typeface.BOLD_ITALIC)
      TextFont.SCRIPT_CALLIGRAPHY -> Typeface.create(Typeface.SERIF, Typeface.ITALIC)
    }

    if (layer.hasShadow) {
      paint.setShadowLayer(10f, 4f, 4f, 0x88000000.toInt())
    }

    // Outline stroke if specified
    if (layer.strokeWidth > 0f) {
      paint.style = Paint.Style.STROKE
      paint.strokeWidth = layer.strokeWidth * 3f
      paint.color = layer.strokeColor.toInt()
      canvas.drawText(layer.text, 0f, 0f, paint)
    }

    // Main text fill
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
        // European Football Shield
        paint.style = Paint.Style.FILL
        paint.color = col1
        val shield = Path().apply {
          moveTo(0f, -50f)
          lineTo(45f, -50f)
          lineTo(45f, 0f)
          cubicTo(45f, 35f, 25f, 55f, 0f, 65f)
          cubicTo(-25f, 55f, -45f, 35f, -45f, 0f)
          lineTo(-45f, -50f)
          close()
        }
        canvas.drawPath(shield, paint)

        // Shield border
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 6f
        paint.color = col3
        canvas.drawPath(shield, paint)

        // Inner shield division
        paint.style = Paint.Style.FILL
        paint.color = col2
        val innerQuarter = Path().apply {
          moveTo(0f, -45f)
          lineTo(40f, -45f)
          lineTo(40f, 0f)
          lineTo(0f, 0f)
          close()
          moveTo(-40f, 0f)
          lineTo(0f, 0f)
          cubicTo(0f, 30f, -15f, 45f, -40f, 0f)
          close()
        }
        canvas.drawPath(innerQuarter, paint)

        // Center Gold Star
        drawStar(canvas, 0f, -15f, 14f, col3, paint)
      }

      LogoType.CREST_ROUNDEL -> {
        // Modern Roundel Crest
        paint.style = Paint.Style.FILL
        paint.color = col1
        canvas.drawCircle(0f, 0f, 50f, paint)

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 6f
        paint.color = col3
        canvas.drawCircle(0f, 0f, 46f, paint)

        paint.style = Paint.Style.FILL
        paint.color = col2
        canvas.drawCircle(0f, 0f, 32f, paint)

        // Inner soccer ball / cross motif
        paint.color = col3
        drawStar(canvas, 0f, 0f, 18f, col3, paint)
      }

      LogoType.CREST_STAR -> {
        drawStar(canvas, 0f, 0f, 48f, col1, paint)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4f
        paint.color = col2
        drawStar(canvas, 0f, 0f, 48f, col2, paint)
      }

      LogoType.CREST_EAGLE -> {
        paint.style = Paint.Style.FILL
        paint.color = col1
        val eagle = Path().apply {
          moveTo(0f, -40f)
          lineTo(18f, -25f)
          lineTo(50f, -40f)
          lineTo(35f, 0f)
          lineTo(45f, 25f)
          lineTo(20f, 15f)
          lineTo(0f, 45f)
          lineTo(-20f, 15f)
          lineTo(-45f, 25f)
          lineTo(-35f, 0f)
          lineTo(-50f, -40f)
          lineTo(-18f, -25f)
          close()
        }
        canvas.drawPath(eagle, paint)
      }

      LogoType.CREST_LION -> {
        paint.style = Paint.Style.FILL
        paint.color = col1
        // Rampant heraldic crest silhouette
        val lion = Path().apply {
          moveTo(-10f, -40f)
          lineTo(15f, -45f)
          lineTo(25f, -30f)
          lineTo(45f, -25f)
          lineTo(30f, -10f)
          lineTo(40f, 10f)
          lineTo(20f, 30f)
          lineTo(10f, 45f)
          lineTo(-15f, 45f)
          lineTo(-10f, 25f)
          lineTo(-30f, 15f)
          lineTo(-20f, -5f)
          lineTo(-35f, -20f)
          close()
        }
        canvas.drawPath(lion, paint)
      }

      LogoType.BRAND_SWOOSH -> {
        paint.style = Paint.Style.FILL
        paint.color = col1
        val swoosh = Path().apply {
          moveTo(-45f, 10f)
          cubicTo(-20f, 25f, 15f, 22f, 45f, -25f)
          cubicTo(20f, 5f, -5f, 12f, -30f, 2f)
          cubicTo(-38f, -2f, -42f, 4f, -45f, 10f)
          close()
        }
        canvas.drawPath(swoosh, paint)
      }

      LogoType.BRAND_TRIPLE_STRIPES -> {
        paint.style = Paint.Style.FILL
        paint.color = col1
        val wBar = 12f
        val gap = 16f
        // 3 angled diagonal stripes of descending height
        val p1 = Path().apply {
          moveTo(gap - 4f, -35f); lineTo(gap + wBar - 4f, -35f); lineTo(gap + wBar - 4f + 25f, 35f); lineTo(gap - 4f + 25f, 35f); close()
        }
        val p2 = Path().apply {
          moveTo(-wBar / 2, -15f); lineTo(wBar / 2, -15f); lineTo(wBar / 2 + 25f, 35f); lineTo(-wBar / 2 + 25f, 35f); close()
        }
        val p3 = Path().apply {
          moveTo(-gap - wBar + 4f, 5f); lineTo(-gap + 4f, 5f); lineTo(-gap + 4f + 25f, 35f); lineTo(-gap - wBar + 4f + 25f, 35f); close()
        }
        canvas.drawPath(p1, paint)
        canvas.drawPath(p2, paint)
        canvas.drawPath(p3, paint)
      }

      LogoType.BRAND_CHEVRON -> {
        paint.style = Paint.Style.FILL
        paint.color = col1
        val c1 = Path().apply {
          moveTo(-35f, -25f); lineTo(0f, 0f); lineTo(35f, -25f); lineTo(35f, -10f); lineTo(0f, 15f); lineTo(-35f, -10f); close()
        }
        val c2 = Path().apply {
          moveTo(-35f, 0f); lineTo(0f, 25f); lineTo(35f, 0f); lineTo(35f, 15f); lineTo(0f, 40f); lineTo(-35f, 15f); close()
        }
        canvas.drawPath(c1, paint)
        canvas.drawPath(c2, paint)
      }

      LogoType.BRAND_DIAMOND -> {
        paint.style = Paint.Style.FILL
        paint.color = col1
        val d1 = Path().apply {
          moveTo(0f, -40f); lineTo(45f, 0f); lineTo(0f, 40f); lineTo(-45f, 0f); close()
        }
        canvas.drawPath(d1, paint)
        paint.color = col2
        val d2 = Path().apply {
          moveTo(0f, -22f); lineTo(26f, 0f); lineTo(0f, 22f); lineTo(-26f, 0f); close()
        }
        canvas.drawPath(d2, paint)
      }

      LogoType.BRAND_CAT -> {
        paint.style = Paint.Style.FILL
        paint.color = col1
        val cat = Path().apply {
          moveTo(-35f, 20f)
          cubicTo(-25f, 10f, -10f, -15f, 20f, -30f)
          lineTo(35f, -35f)
          lineTo(30f, -20f)
          cubicTo(35f, -10f, 40f, 5f, 25f, 15f)
          lineTo(5f, 5f)
          cubicTo(-10f, 20f, -20f, 30f, -35f, 20f)
          close()
        }
        canvas.drawPath(cat, paint)
      }

      LogoType.SPONSOR_AIRWAYS -> {
        paint.color = col1
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 28f
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        canvas.drawText("Fly Better", 0f, -12f, paint)
        paint.textSize = 34f
        paint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        canvas.drawText("EMIRATES", 0f, 24f, paint)
      }

      LogoType.SPONSOR_TECH -> {
        paint.color = col1
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 36f
        paint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        canvas.drawText("[ CYBERTECH ]", 0f, 10f, paint)
      }

      LogoType.SPONSOR_STREAMING -> {
        paint.color = col1
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 34f
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        canvas.drawText("Spotify", 0f, 12f, paint)
      }

      LogoType.SPONSOR_AUTOMOTIVE -> {
        paint.color = col1
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 38f
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC)
        canvas.drawText("PIRELLI", 0f, 12f, paint)
      }

      LogoType.SPONSOR_TELECOM -> {
        paint.color = col1
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 34f
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        canvas.drawText("vodafone", 0f, 12f, paint)
      }

      LogoType.SPONSOR_CRYPTO -> {
        paint.color = col1
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 32f
        paint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        canvas.drawText("⚡ BITCOIN", 0f, 12f, paint)
      }

      LogoType.CUSTOM_UPLOAD -> {
        paint.color = col1
        paint.style = Paint.Style.FILL
        canvas.drawRoundRect(-50f, -30f, 50f, 30f, 12f, 12f, paint)
        paint.color = col2
        paint.textSize = 20f
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("CUSTOM", 0f, 8f, paint)
      }
    }
  }

  private fun drawStar(canvas: Canvas, cx: Float, cy: Float, radius: Float, color: Int, paint: Paint) {
    paint.color = color
    paint.style = Paint.Style.FILL
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
