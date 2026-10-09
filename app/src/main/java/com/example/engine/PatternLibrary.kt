package com.example.engine

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import com.example.model.KitPatternType
import kotlin.math.cos
import kotlin.math.sin

enum class PatternCategory(val label: String) {
  OFFICIAL("Official"),
  GEOMETRIC("Geometric"),
  STRIPES("Stripes"),
  GRUNGE("Grunge"),
  CULTURAL("Cultural"),
  ABSTRACT("Abstract"),
  NATURE("Nature"),
  ZIG_ZAG("Zig Zag"),
  MULTICOLOR("Multicolor"),
  CHECKERS("Checkers"),
  GRID("Grid"),
  WAVES("Waves"),
  ICON("Icon"),
  MOSAIC("Mosaic"),
  CAMOUFLAGE("Camouflage"),
  CHEVRONS("Chevrons"),
  DOTS("Dots"),
  ANIMAL("Animal"),
  TYPOGRAPHY("Typography"),
  MARBLE("Marble"),
  ARCHITECTURE("Architecture"),
  MAP("Map")
}

data class PatternItem(
  val id: String,
  val name: String,
  val category: PatternCategory,
  val patternType: KitPatternType,
  val frequency: Int = 6,
  val primaryColor: Long = 0xFF14171EL,
  val secondaryColor: Long = 0xFF22C55EL,
  val angle: Float = 0f
)

object PatternLibrary {

  val allPatterns: List<PatternItem> by lazy {
    val list = mutableListOf<PatternItem>()

    // 1. Official
    list.add(PatternItem("off_1", "Heritage Diagonal Ribbon", PatternCategory.OFFICIAL, KitPatternType.SASH_DIAGONAL, 1, 0xFF1E3A8AL, 0xFFFFFFFFL))
    list.add(PatternItem("off_2", "Classic League Pinstripes", PatternCategory.OFFICIAL, KitPatternType.PINSTRIPES, 10, 0xFF0F172AL, 0xFFEAB308L))
    list.add(PatternItem("off_3", "Champions Dual Halves", PatternCategory.OFFICIAL, KitPatternType.HALVES, 2, 0xFF85142AL, 0xFF0244B2L))
    list.add(PatternItem("off_4", "Vertical Standard 7-Bar", PatternCategory.OFFICIAL, KitPatternType.STRIPES_VERTICAL, 7, 0xFF0A0A0AL, 0xFFDC2626L))

    // 2. Geometric
    list.add(PatternItem("geo_1", "Cyber Hex Cells", PatternCategory.GEOMETRIC, KitPatternType.HEX_HONEYCOMB, 8, 0xFF14171EL, 0xFF22C55EL))
    list.add(PatternItem("geo_2", "Argyle Diamonds", PatternCategory.GEOMETRIC, KitPatternType.DIAMONDS, 10, 0xFF1E293BL, 0xFF06B6D4L))
    list.add(PatternItem("geo_3", "Isometric Cubes", PatternCategory.GEOMETRIC, KitPatternType.HEX_HONEYCOMB, 14, 0xFF18181BL, 0xFFF59E0BL))

    // 3. Stripes
    list.add(PatternItem("str_1", "Milano Vertical Bold", PatternCategory.STRIPES, KitPatternType.STRIPES_VERTICAL, 5, 0xFF000000L, 0xFF0244B2L))
    list.add(PatternItem("str_2", "Horizontal Rugby Hoops", PatternCategory.STRIPES, KitPatternType.STRIPES_HORIZONTAL, 6, 0xFF15803DL, 0xFFFFFFFFL))
    list.add(PatternItem("str_3", "Fine Density Pinstripe", PatternCategory.STRIPES, KitPatternType.PINSTRIPES, 16, 0xFF111827L, 0xFF9CA3AFL))

    // 4. Chevrons & Zig Zag
    list.add(PatternItem("chv_1", "Classic V-Chevron", PatternCategory.CHEVRONS, KitPatternType.CHEVRON, 5, 0xFF0F172AL, 0xFFFFFFFFL))
    list.add(PatternItem("chv_2", "Sharp Dual Angle", PatternCategory.CHEVRONS, KitPatternType.CHEVRON, 8, 0xFF85142AL, 0xFFEAB308L))
    list.add(PatternItem("zz_1", "Dynamic Lightning ZigZag", PatternCategory.ZIG_ZAG, KitPatternType.ZIG_ZAG, 8, 0xFF0A0A0AL, 0xFFEF4444L))
    list.add(PatternItem("zz_2", "Rhythmic Wave Chevron", PatternCategory.ZIG_ZAG, KitPatternType.ZIG_ZAG, 12, 0xFF0284C7L, 0xFFFFFFFFL))

    // 5. Waves & Nature
    list.add(PatternItem("wav_1", "Aero Ripple Waves", PatternCategory.WAVES, KitPatternType.WAVES, 6, 0xFF0F172AL, 0xFF06B6D4L))
    list.add(PatternItem("wav_2", "Oceanic Flow", PatternCategory.WAVES, KitPatternType.WAVES, 10, 0xFF1D4ED8L, 0xFF67E8F9L))
    list.add(PatternItem("nat_1", "Digital Flora Camo", PatternCategory.NATURE, KitPatternType.CAMOUFLAGE, 5, 0xFF14532DL, 0xFF22C55EL))

    // 6. Checkers & Mosaic
    list.add(PatternItem("chk_1", "Zagreb National Checkers", PatternCategory.CHECKERS, KitPatternType.CHECKERBOARD, 8, 0xFFDC2626L, 0xFFFFFFFFL))
    list.add(PatternItem("chk_2", "Subtle Tone-on-Tone Grid", PatternCategory.GRID, KitPatternType.CHECKERBOARD, 12, 0xFF18181BL, 0xFF27272AL))
    list.add(PatternItem("mos_1", "Mediterranean Mosaic", PatternCategory.MOSAIC, KitPatternType.MOSAIC_TILES, 10, 0xFF1E3A8AL, 0xFFFBBF24L))

    // 7. Camouflage & Grunge
    list.add(PatternItem("cam_1", "Urban Night Camo", PatternCategory.CAMOUFLAGE, KitPatternType.CAMOUFLAGE, 6, 0xFF18181BL, 0xFF3F3F46L))
    list.add(PatternItem("cam_2", "Desert Military Storm", PatternCategory.CAMOUFLAGE, KitPatternType.CAMOUFLAGE, 8, 0xFF78350FL, 0xFFFDE68AL))
    list.add(PatternItem("gru_1", "Spray Paint Splatter", PatternCategory.GRUNGE, KitPatternType.ABSTRACT_BRUSH, 8, 0xFF09090BL, 0xFFE11D48L))

    // 8. Dots & Marble & Cultural
    list.add(PatternItem("dot_1", "Halftone Gradient Dots", PatternCategory.DOTS, KitPatternType.DOTS_HALFTONE, 14, 0xFF0F172AL, 0xFF38BDF8L))
    list.add(PatternItem("mar_1", "Carrara Marble Vein", PatternCategory.MARBLE, KitPatternType.MARBLE_TEXTURE, 8, 0xFFF8FAFCL, 0xFF64748BL))
    list.add(PatternItem("cul_1", "Nordic Cross Symmetrical", PatternCategory.CULTURAL, KitPatternType.CROSS_PATTERN, 4, 0xFF1D4ED8L, 0xFFFACC15L))

    // Populate remaining categories with distinctive procedural templates
    for (cat in PatternCategory.values()) {
      if (list.none { it.category == cat }) {
        list.add(PatternItem("gen_${cat.name.lowercase()}_1", "${cat.label} Flow Matrix", cat, KitPatternType.GRADIENT_LINEAR, 6, 0xFF111827L, 0xFF22C55EL))
        list.add(PatternItem("gen_${cat.name.lowercase()}_2", "${cat.label} Kinetic Stripe", cat, KitPatternType.STRIPES_VERTICAL, 8, 0xFF1E293BL, 0xFFF59E0BL))
      }
    }

    list
  }

  fun filter(category: PatternCategory?, query: String, sort: String): List<PatternItem> {
    var result = allPatterns.filter { item ->
      (category == null || item.category == category) &&
      (query.isBlank() || item.name.contains(query, ignoreCase = true) || item.category.label.contains(query, ignoreCase = true))
    }

    result = when (sort) {
      "Name" -> result.sortedBy { it.name }
      "Latest" -> result.reversed()
      else -> result // Popular
    }
    return result
  }

  /**
   * Generates a fast 120x120 thumbnail bitmap for the modal grid.
   */
  fun generateThumbnail(item: PatternItem): Bitmap {
    val bmp = Bitmap.createBitmap(120, 120, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Background
    paint.color = item.primaryColor.toInt()
    paint.style = Paint.Style.FILL
    canvas.drawRect(0f, 0f, 120f, 120f, paint)

    val col2 = item.secondaryColor.toInt()

    when (item.patternType) {
      KitPatternType.STRIPES_VERTICAL -> {
        val count = item.frequency.coerceAtLeast(3)
        val w = 120f / count
        paint.color = col2
        for (i in 0 until count step 2) {
          canvas.drawRect(i * w, 0f, (i + 1) * w, 120f, paint)
        }
      }
      KitPatternType.STRIPES_HORIZONTAL -> {
        val count = item.frequency.coerceAtLeast(3)
        val h = 120f / count
        paint.color = col2
        for (i in 0 until count step 2) {
          canvas.drawRect(0f, i * h, 120f, (i + 1) * h, paint)
        }
      }
      KitPatternType.CHECKERBOARD -> {
        val count = 4
        val w = 120f / count
        paint.color = col2
        for (r in 0 until count) {
          for (c in 0 until count) {
            if ((r + c) % 2 == 1) canvas.drawRect(c * w, r * w, (c + 1) * w, (r + 1) * w, paint)
          }
        }
      }
      KitPatternType.CHEVRON -> {
        paint.color = col2
        val step = 120f / 4
        for (i in 0 until 4) {
          val path = Path().apply {
            moveTo(0f, i * step); lineTo(60f, (i + 0.5f) * step); lineTo(120f, i * step)
            lineTo(120f, (i + 0.8f) * step); lineTo(60f, (i + 1.3f) * step); lineTo(0f, (i + 0.8f) * step); close()
          }
          canvas.drawPath(path, paint)
        }
      }
      KitPatternType.SASH_DIAGONAL -> {
        paint.color = col2
        val path = Path().apply {
          moveTo(0f, 0f); lineTo(25f, 0f); lineTo(120f, 120f); lineTo(95f, 120f); close()
        }
        canvas.drawPath(path, paint)
      }
      KitPatternType.HEX_HONEYCOMB -> {
        paint.color = col2
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        for (y in 10..110 step 28) {
          for (x in 10..110 step 32) {
            canvas.drawCircle(x.toFloat(), y.toFloat(), 10f, paint)
          }
        }
      }
      KitPatternType.WAVES -> {
        paint.color = col2
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f
        for (y in 20..100 step 25) {
          val path = Path().apply {
            moveTo(0f, y.toFloat())
            quadTo(30f, (y - 12).toFloat(), 60f, y.toFloat())
            quadTo(90f, (y + 12).toFloat(), 120f, y.toFloat())
          }
          canvas.drawPath(path, paint)
        }
      }
      else -> {
        paint.shader = LinearGradient(0f, 0f, 120f, 120f, intArrayOf(item.primaryColor.toInt(), col2), null, Shader.TileMode.CLAMP)
        paint.style = Paint.Style.FILL
        canvas.drawRect(0f, 0f, 120f, 120f, paint)
      }
    }

    return bmp
  }
}
