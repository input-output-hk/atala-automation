package io.iohk.atala.automation.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.awaitility.core.ConditionTimeoutException
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Test
import java.util.concurrent.Executors
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class WaitTest {
    @Test
    @ExperimentalTime
    fun `Wait should wait maximum expected time`() {
        val timeout = 1.seconds
        val delta = measureTime {
            Assert.assertThrows(ConditionTimeoutException::class.java) {
                Wait.until(timeout) {
                    false
                }
            }
        }
        MatcherAssert.assertThat(delta, Matchers.greaterThan(timeout))
    }

    @Test
    @ExperimentalTime
    fun `Wait should return fast if condition is met`() {
        val timeout = 1.minutes
        var condition = false

        val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
        CoroutineScope(dispatcher).launch {
            delay(500)
            condition = true
        }

        val delta = measureTime {
            Wait.until(timeout, 10.milliseconds) {
                condition
            }
        }
        MatcherAssert.assertThat(delta, Matchers.lessThan(timeout))
    }
}
