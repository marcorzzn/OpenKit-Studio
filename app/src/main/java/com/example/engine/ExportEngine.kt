package com.example.engine

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Environment
import com.example.model.ExportFormat
import com.example.model.ExportResolution
import com.example.model.ExportTarget
import com.example.model.KitComponentType
import com.example.model.KitLayer
import com.example.model.KitProject
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object ExportEngine {

  /**
   * Renders the targeted object into a high-resolution Bitmap based on export settings.
   */
  fun renderTargetBitmap(
    context: Context,
    project: KitProject,
    target: ExportTarget,
    resolution: ExportResolution,
    customWidth: Int,
    customHeight: Int,
    activeComponent: KitComponentType,
    selectedLayer: KitLayer?,
    viewportSnapshot: Bitmap?
  ): Bitmap {
    val size = when (resolution) {
      ExportResolution.RES_512 -> 512
      ExportResolution.RES_1024 -> 1024
      ExportResolution.RES_2048 -> 2048
      ExportResolution.RES_4096 -> 4096
      ExportResolution.VIEWPORT_NATIVE -> viewportSnapshot?.width ?: 1024
      ExportResolution.CUSTOM -> customWidth.coerceIn(256, 4096)
    }

    return when (target) {
      ExportTarget.VIEWPORT_RENDER -> {
        if (viewportSnapshot != null) {
          Bitmap.createScaledBitmap(viewportSnapshot, size, size, true)
        } else {
          TextureEngine.renderComponentTexture(project, activeComponent, size, size)
        }
      }

      ExportTarget.SINGLE_LAYER -> {
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        if (selectedLayer != null) {
          TextureEngine.renderLayer(canvas, selectedLayer, size, size, project, activeComponent)
        }
        bmp
      }

      ExportTarget.SHIRT_ONLY, ExportTarget.FLAT_TEXTURE -> {
        TextureEngine.renderComponentTexture(project, KitComponentType.SHIRT, size, size)
      }

      ExportTarget.SHORTS_ONLY -> {
        TextureEngine.renderComponentTexture(project, KitComponentType.SHORTS, size, size)
      }

      ExportTarget.SOCKS_ONLY -> {
        TextureEngine.renderComponentTexture(project, KitComponentType.SOCKS, size, size)
      }

      ExportTarget.ARMBAND_ONLY -> {
        TextureEngine.renderComponentTexture(project, KitComponentType.ARMBAND, size, size)
      }

      ExportTarget.FULL_KIT, ExportTarget.SELECTION -> {
        // Combined master texture sheet (2x2 grid: TopLeft=Shirt, TopRight=Shorts, BottomLeft=Socks, BottomRight=Details)
        val composite = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(composite)
        val half = size / 2

        val shirtBmp = TextureEngine.renderComponentTexture(project, KitComponentType.SHIRT, half, half)
        val shortsBmp = TextureEngine.renderComponentTexture(project, KitComponentType.SHORTS, half, half)
        val socksBmp = TextureEngine.renderComponentTexture(project, KitComponentType.SOCKS, half, half)

        canvas.drawBitmap(shirtBmp, 0f, 0f, null)
        canvas.drawBitmap(shortsBmp, half.toFloat(), 0f, null)
        canvas.drawBitmap(socksBmp, 0f, half.toFloat(), null)

        composite
      }
    }
  }

  /**
   * Encodes a bitmap into the chosen format byte array.
   */
  fun encodeBitmap(bitmap: Bitmap, format: ExportFormat, quality: Int = 95): ByteArray {
    val stream = ByteArrayOutputStream()
    when (format) {
      ExportFormat.PNG -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
      ExportFormat.JPG -> bitmap.compress(Bitmap.CompressFormat.JPEG, quality.coerceIn(50, 100), stream)
      ExportFormat.WEBP -> {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
          bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, quality.coerceIn(50, 100), stream)
        } else {
          @Suppress("DEPRECATION")
          bitmap.compress(Bitmap.CompressFormat.WEBP, quality.coerceIn(50, 100), stream)
        }
      }
      ExportFormat.AVIF -> {
        // Fallback to PNG / WEBP
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
      }
      ExportFormat.SVG, ExportFormat.JSON -> {
        // Handled separately
      }
    }
    return stream.toByteArray()
  }

  /**
   * Generates a structured multi-component ZIP file compatible with game modding & FIFA Editor Tool.
   */
  fun generateStructuredZip(
    context: Context,
    project: KitProject,
    includeHotspots: Boolean = true
  ): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val zipFile = File(context.cacheDir, "openkit_${project.name.replace(" ", "_")}_$timeStamp.zip")
    val zos = ZipOutputStream(FileOutputStream(zipFile))

    // 1. Manifest JSON
    val manifest = JSONObject().apply {
      put("name", project.name)
      put("team", project.teamName)
      put("sport", project.sport.label)
      put("gameVersion", project.gameEdition)
      put("collarTemplate", project.collarTemplate.label)
      put("modelFit", project.modelFit.label)
      put("exportedAt", timeStamp)
      put("hotspotsIncluded", includeHotspots)
    }
    writeZipEntry(zos, "manifest.json", manifest.toString(2).toByteArray())

    // 2. Component Textures
    val shirt2k = TextureEngine.renderComponentTexture(project, KitComponentType.SHIRT, 2048, 2048)
    writeZipEntry(zos, "shirt/shirt_texture_2048.png", encodeBitmap(shirt2k, ExportFormat.PNG))

    val shorts2k = TextureEngine.renderComponentTexture(project, KitComponentType.SHORTS, 2048, 2048)
    writeZipEntry(zos, "shorts/shorts_texture_2048.png", encodeBitmap(shorts2k, ExportFormat.PNG))

    val socks2k = TextureEngine.renderComponentTexture(project, KitComponentType.SOCKS, 2048, 2048)
    writeZipEntry(zos, "socks/socks_texture_2048.png", encodeBitmap(socks2k, ExportFormat.PNG))

    // 3. Individual Layers for Shirt
    project.shirtLayers.forEachIndexed { i, layer ->
      val layerBmp = Bitmap.createBitmap(1024, 1024, Bitmap.Config.ARGB_8888)
      val canvas = Canvas(layerBmp)
      TextureEngine.renderLayer(canvas, layer, 1024, 1024, project, KitComponentType.SHIRT)
      val cleanName = layer.name.replace(" ", "_").lowercase()
      writeZipEntry(zos, "shirt/layers/${i + 1}_$cleanName.png", encodeBitmap(layerBmp, ExportFormat.PNG))
    }

    // 4. Hotspots Data if requested
    if (includeHotspots) {
      val hotspots = JSONObject().apply {
        put("crest", JSONObject().apply { put("u", 0.32); put("v", 0.35); put("scale", 0.12) })
        put("brand", JSONObject().apply { put("u", 0.68); put("v", 0.35); put("scale", 0.10) })
        put("sponsor", JSONObject().apply { put("u", 0.50); put("v", 0.52); put("scale", 0.30) })
        put("back_number", JSONObject().apply { put("u", 0.50); put("v", 0.50); put("scale", 0.28) })
      }
      writeZipEntry(zos, "game_hotspots.json", hotspots.toString(2).toByteArray())
    }

    zos.close()
    return zipFile
  }

  private fun writeZipEntry(zos: ZipOutputStream, entryPath: String, data: ByteArray) {
    zos.putNextEntry(ZipEntry(entryPath))
    zos.write(data)
    zos.closeEntry()
  }

  fun exportProjectToJson(project: KitProject): String {
    val json = JSONObject().apply {
      put("name", project.name)
      put("teamName", project.teamName)
      put("sport", project.sport.name)
      put("gameEdition", project.gameEdition)
      put("modelFit", project.modelFit.name)
      put("collarTemplate", project.collarTemplate.name)
      put("collarColor", project.collarColor)
      put("collarTrimColor", project.collarTrimColor)
      put("globalShininess", project.globalShininess)
      put("globalMetalness", project.globalMetalness)
      put("fabricRoughness", project.fabricRoughness)

      val shirtArray = JSONArray()
      project.shirtLayers.forEach { l ->
        shirtArray.put(JSONObject().apply {
          put("id", l.id)
          put("name", l.name)
          put("type", l.type.name)
          put("primaryColor", l.primaryColor)
          put("secondaryColor", l.secondaryColor)
          put("opacity", l.opacity)
          put("x", l.transform.x)
          put("y", l.transform.y)
          put("scaleX", l.transform.scaleX)
          put("rotation", l.transform.rotation)
        })
      }
      put("shirtLayers", shirtArray)
    }
    return json.toString(2)
  }
}
