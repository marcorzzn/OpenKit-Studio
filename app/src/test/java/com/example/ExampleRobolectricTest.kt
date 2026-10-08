package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("OpenKit Studio", appName)
  }

  @Test
  fun `verify presets generated correctly`() {
    val presets = com.example.model.PresetsData.getAllPresets()
    assertTrue(presets.isNotEmpty())
    val defaultProject = com.example.model.PresetsData.createDefaultProject()
    assertNotNull(defaultProject)
    assertTrue(defaultProject.shirtLayers.isNotEmpty())
  }
}
