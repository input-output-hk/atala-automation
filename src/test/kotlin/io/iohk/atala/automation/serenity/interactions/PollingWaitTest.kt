package io.iohk.atala.automation.serenity.interactions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.serenitybdd.core.Serenity
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.Question
import net.thucydides.core.steps.StepEventBus
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.lessThan
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class PollingWaitTest {
    @Before
    fun setup() {
        Serenity.initialize(this)
        StepEventBus.getParallelEventBus().testStarted("unit test")
    }

    @After
    fun cleanup() {
        Serenity.done()
    }

    @Test
    @ExperimentalTime
    fun `Actor using PollingWait should not poll if condition is already met`() {
        val timeout = 1.minutes
        val polling = 1.minutes

        val delta = measureTime {
            val actor = Actor.named("Test")
            actor.attemptsTo(
                PollingWait
                    .upTo(timeout)
                    .pollingEvery(polling)
                    .until(
                        Question.about("value").answeredBy { "value" },
                        equalTo("value")
                    )
            )
        }
        assertThat(delta, lessThan(timeout))
    }


    @Test
    @ExperimentalTime
    fun `Actor using PollingWait should return when poll condition is changed`() {
        val timeout = 1.minutes
        val polling = 100.milliseconds
        var condition = false

        CoroutineScope(Dispatchers.Default).launch {
            delay(500)
            condition = true
        }

        val delta = measureTime {
            val actor = Actor.named("Test")
            actor.attemptsTo(
                PollingWait
                    .upTo(timeout)
                    .pollingEvery(polling)
                    .until(
                        Question.about("value").answeredBy { condition },
                        equalTo(true)
                    )
            )
        }

        assertThat(delta, greaterThan(polling))
        assertThat(delta, lessThan(timeout))
    }

    @Test
    @ExperimentalTime
    fun `Actor using PollingWait should throw exception if timeout is exceeded`() {
        val actor = Actor.named("Test")
        val timeout = 100.milliseconds
        val polling = 50.milliseconds

        val delta = measureTime {
            val exception = Assert.assertThrows(AssertionError::class.java) {
                actor.attemptsTo(
                    PollingWait
                        .upTo(timeout)
                        .pollingEvery(polling)
                        .until(
                            Question.about("value").answeredBy { "value" },
                            equalTo("other")
                        )
                )
            }
            assertThat(exception.message, containsString("Timeout [100ms] exceeded"))
        }
        assertThat(delta, greaterThan(timeout))
    }

    @Test
    fun `PollingWait should provide static ways to create`() {
        PollingWait.upTo(100.milliseconds).pollingEvery(100.milliseconds)
        PollingWait.pollingEvery(100.milliseconds).upTo(100.milliseconds)
        PollingWait.until(Question.about("value").answeredBy { "value" }, equalTo("something"))
    }
}