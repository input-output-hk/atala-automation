package io.iohk.atala.automation.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Logger {
    /**
     * Get logger for the specified class.
     *
     * @return logger instance
     */
    inline fun <reified T : Any> get(): Logger = LoggerFactory.getLogger(T::class.java)
}
