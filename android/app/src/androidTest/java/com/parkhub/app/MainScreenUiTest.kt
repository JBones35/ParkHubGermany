package com.parkhub.app

import android.Manifest
import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainScreenUiTest {

    private lateinit var device: UiDevice
    private lateinit var packageName: String

    @Before
    fun setup() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        device = UiDevice.getInstance(instrumentation)
        packageName = context.packageName

        grantLocationPermission()

        val intent = context.packageManager.getLaunchIntentForPackage(packageName)!!.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)
        device.wait(Until.hasObject(By.pkg(packageName).depth(0)), 5_000)
        dismissLocationDialogIfShown()
        device.waitForIdle()
    }

    @After
    fun tearDown() {
        device.pressHome()
    }

    @Test
    fun startZeigtSucheMitPflichtfeldHinweis() {
        assertTextVisible("Suchen")
        assertTextVisible("Flotte")
        assertTextVisible("Ort")
    }

    @Test
    fun bottomNavigationWechseltZurFlotte() {
        clickText("Flotte")

        assertTextVisible("Alle")
        assertTextVisible("Frei")
        assertTextVisible("Besetzt")
        assertTextVisible("In Wartung")
    }

    @Test
    fun flotteZeigtFahrzeugUndFahrerTabs() {
        clickText("Flotte")

        assertTextContainsVisible("Fahrzeuge")
        assertTextContainsVisible("Fahrer")
    }

    @Test
    fun fahrerTabZeigtFahrerFilter() {
        clickText("Flotte")
        clickTextContains("Fahrer")

        assertTextVisible("Alle")
        assertTextVisible("Frei")
        assertTextVisible("Eingesetzt")
        assertTextVisible("Abwesend")
    }

    private fun grantLocationPermission() {
        val automation = InstrumentationRegistry.getInstrumentation().uiAutomation
        runCatching { automation.grantRuntimePermission(packageName, Manifest.permission.ACCESS_FINE_LOCATION) }
        runCatching { automation.grantRuntimePermission(packageName, Manifest.permission.ACCESS_COARSE_LOCATION) }
    }

    private fun dismissLocationDialogIfShown() {
        val allowButton = By.text("While using the app")
        if (device.wait(Until.hasObject(allowButton), 1_000)) {
            device.findObject(allowButton).click()
            device.waitForIdle()
        }
    }

    private fun clickText(text: String) {
        val selector = By.text(text)
        device.wait(Until.hasObject(selector), 5_000)
        val node = device.findObject(selector)
        assertNotNull("Expected clickable text: $text", node)
        node.click()
        device.waitForIdle()
    }

    private fun clickTextContains(text: String) {
        val selector = By.textContains(text)
        device.wait(Until.hasObject(selector), 5_000)
        val node = device.findObject(selector)
        assertNotNull("Expected clickable text containing: $text", node)
        node.click()
        device.waitForIdle()
    }

    private fun assertTextVisible(text: String) {
        val selector = By.text(text)
        device.wait(Until.hasObject(selector), 5_000)
        assertNotNull("Expected visible text: $text", device.findObject(selector))
    }

    private fun assertTextContainsVisible(text: String) {
        val selector = By.textContains(text)
        device.wait(Until.hasObject(selector), 5_000)
        assertNotNull("Expected visible text containing: $text", device.findObject(selector))
    }
}
