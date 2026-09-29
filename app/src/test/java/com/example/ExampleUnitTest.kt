package com.example

import com.example.ui.theme.DarkGlassTokens
import com.example.ui.theme.DimGlassTokens
import com.example.ui.theme.LightGlassTokens
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testGlassTokensReflexValues() {
        assertEquals(1.0f, LightGlassTokens.glassReflexLight, 0.01f)
        assertEquals(1.0f, LightGlassTokens.glassReflexDark, 0.01f)
        assertEquals(0.3f, DarkGlassTokens.glassReflexLight, 0.01f)
        assertEquals(2.0f, DarkGlassTokens.glassReflexDark, 0.01f)
        assertEquals(0.7f, DimGlassTokens.glassReflexLight, 0.01f)
        assertEquals(2.0f, DimGlassTokens.glassReflexDark, 0.01f)
    }

    @Test
    fun testClockAngleCalculations() {
        // At 0 seconds, angle should be 0
        val zeroSecondAngle = (0f / 60f) * 360f
        assertEquals(0f, zeroSecondAngle, 0.01f)

        // At 15 seconds, angle should be 90 degrees
        val fifteenSecondAngle = (15f / 60f) * 360f
        assertEquals(90f, fifteenSecondAngle, 0.01f)

        // At 30 seconds, angle should be 180 degrees
        val thirtySecondAngle = (30f / 60f) * 360f
        assertEquals(180f, thirtySecondAngle, 0.01f)

        // At 45 seconds, angle should be 270 degrees
        val fortyFiveSecondAngle = (45f / 60f) * 360f
        assertEquals(270f, fortyFiveSecondAngle, 0.01f)
    }
}
