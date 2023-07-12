package io.iohk.atala.automation

import org.junit.Assert
import org.junit.Test

internal class LoggerTest {
    @Test
    fun `Should instantiate new logger`() {
        val logger = loggerFor<LoggerTest>()
        Assert.assertNotNull("Logger should not be null", logger)
    }
}
