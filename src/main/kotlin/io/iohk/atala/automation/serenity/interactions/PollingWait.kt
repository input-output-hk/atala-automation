package io.iohk.atala.automation.serenity.interactions

import io.iohk.atala.automation.Utils
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.Question
import net.serenitybdd.screenplay.SilentInteraction
import org.awaitility.core.ConditionTimeoutException
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert
import org.junit.Assert
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Polling wait for condition for [Actor.attemptsTo].
 *
 * @param question answerable question from actor
 * @param matcher condition for validation
 * @param timeout maximum time to wait
 * @param pollInterval polling interval
 */
class PollingWait<T>(
    private val question: Question<T>,
    private val matcher: Matcher<T>,
    private val timeout: Duration,
    private val pollInterval: Duration
) : SilentInteraction() {
    class PollingWaitBuilder(
        private var upto: Duration = 30.seconds,
        private var polling: Duration = 1.seconds
    ) {
        fun <T> until(
            question: Question<T>,
            matcher: Matcher<T>
        ): PollingWait<T> {
            return PollingWait(question, matcher, upto, polling)
        }

        fun upTo(duration: Duration): PollingWaitBuilder {
            this.upto = duration
            return this
        }

        fun pollingEvery(duration: Duration): PollingWaitBuilder {
            this.polling = duration
            return this
        }
    }

    companion object {
        fun upTo(duration: Duration): PollingWaitBuilder {
            return PollingWaitBuilder(upto = duration)
        }

        fun pollingEvery(duration: Duration): PollingWaitBuilder {
            return PollingWaitBuilder(polling = duration)
        }

        fun <T> until(
            question: Question<T>,
            matcher: Matcher<T>
        ): PollingWait<T> {
            return PollingWaitBuilder().until(question, matcher)
        }
    }

    override fun <T : Actor> performAs(actor: T) {
        try {
            Utils.waitUntil(timeout, pollInterval) {
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
