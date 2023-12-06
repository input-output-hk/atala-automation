package io.iohk.atala.automation.utils

import org.awaitility.Awaitility
import org.awaitility.core.ConditionTimeoutException
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
     * Wait.until() {
     *     customRequest().statusCode == HttpStatus.SC_OK
     * }
     * ```
     *
     * @param timeout maximum time to wait
     * @param pollInterval polling interval
     * @param condition lambda expression to run the condition
     * @param errorMessage error message to throw if the condition is not met
     */
    fun until(
        timeout: Duration = 5.seconds,
        pollInterval: Duration = 500.milliseconds,
        errorMessage: String? = null,
        condition: () -> Boolean,
    ) {
        try {
            Awaitility.await()
                .pollInSameThread()
                .with()
                .pollInterval(pollInterval.toJavaDuration())
                .atMost(timeout.toJavaDuration())
                .until {
                    condition()
                }
        } catch (err: ConditionTimeoutException) {
            if (errorMessage != null) {
                throw ConditionTimeoutException(errorMessage)
            } else {
                throw err
            }
        }
    }
}
