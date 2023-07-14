package io.iohk.atala.automation.serenity.interactions

import io.iohk.atala.automation.utils.Wait
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.Question
import net.serenitybdd.screenplay.SilentInteraction
import org.awaitility.core.ConditionTimeoutException
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert
import org.junit.Assert
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * Polling wait for condition for [Actor.attemptsTo].
 *
 * Usage example:
 * ```
 * actor.attemptsTo(
 *     Get.resource("some resource"),
 *     PollingWait.until {
 *     }
 * ```
 * @param question answerable question from actor
 * @param matcher condition for validation
 * @param timeout maximum time to wait
 * @param pollInterval polling interval
 */
class PollingWait<T>(
    private val question: Question<T>,
    private val matcher: Matcher<T>,
    private val timeout: Duration = 30.seconds,
    private val pollInterval: Duration = 500.milliseconds
) : SilentInteraction() {

    data class Builder(
        private var timeout: Duration,
        private var pollInterval: Duration
    ) {
        fun <T> until(
            question: Question<T>,
            matcher: Matcher<T>
        ): PollingWait<T> {
            return PollingWait(question, matcher, timeout, pollInterval)
        }
    }

    companion object {
        fun with(timeout: Duration, pollInterval: Duration): Builder {
            return Builder(timeout, pollInterval)
        }

        fun <T> until(
            question: Question<T>,
            matcher: Matcher<T>
        ): PollingWait<T> {
            return PollingWait(question, matcher)
        }
    }

    override fun <T : Actor> performAs(actor: T) {
        try {
            Wait.until(timeout, pollInterval) {
                try {
                    val answer = question.answeredBy(actor)
                    MatcherAssert.assertThat(answer, matcher)
                    true
                } catch (e: AssertionError) {
                    false
                }
            }
        } catch (e: ConditionTimeoutException) {
            Assert.fail("Timeout [$timeout] exceeded to [${question.subject}] be [$matcher].")
        }
    }
}
