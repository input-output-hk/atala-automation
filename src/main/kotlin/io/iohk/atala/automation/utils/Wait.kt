package io.iohk.atala.automation.utils

import org.awaitility.Awaitility
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

object Wait {
    /**
     * Blocks the execution until a condition is met.
     *
     * Usage example:
     * ```
     * Utils.waitUntil() {
     *     customRequest().statusCode == HttpStatus.SC_OK
     * }
     * ```
     *
     * @param timeout maximum time to wait
     * @param pollInterval polling interval
     * @param condition lambda expression to run the condition
     */
    fun until(
        timeout: Duration = 5.seconds,
        pollInterval: Duration = 500.milliseconds,
        condition: () -> Boolean
    ) {
        Awaitility.await()
            .with()
            .pollInterval(pollInterval.toJavaDuration())
            .atMost(timeout.toJavaDuration())
            .until {
                condition()
            }
    }
}
