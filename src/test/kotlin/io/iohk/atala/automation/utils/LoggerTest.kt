package io.iohk.atala.automation.utils

import org.junit.Assert
import org.junit.Test

internal class LoggerTest {
    @Test
    fun `Should instantiate new logger`() {
        val logger = Logger.get<LoggerTest>()
        Assert.assertNotNull("Logger should not be null", logger)
    }
}
