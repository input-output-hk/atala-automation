package io.iohk.atala.automation.serenity.interactions

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.Interaction
import net.serenitybdd.screenplay.Question
import net.serenitybdd.screenplay.SilentInteraction
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert
import org.junit.Assert
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class PollingWait<T>(
    private val question: Question<T>,
    private val matcher: Matcher<T>,
    private val upto: Duration,
    private val polling: Duration
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
        runBlocking {
            try {
                withTimeout(upto) {
                    while (true) {
                        try {
                            val answer = question.answeredBy(actor)
                            MatcherAssert.assertThat(answer, matcher)
                            break
                        } catch (e: AssertionError) {
                            delay(polling)
                        }
                    }
                }
            } catch (e: CancellationException) {
                Assert.fail("Timeout [$upto] exceeded to [${question.subject}] be [$matcher].")
            }
        }
    }
}
