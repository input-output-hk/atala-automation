package io.iohk.atala.automation

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Get logger for the specified class.
 */
inline fun <reified T : Any> loggerFor(): Logger = LoggerFactory.getLogger(T::class.java)
