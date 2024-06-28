package io.iohk.atala.automation.serenity.ensure

import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.Interaction
import net.serenitybdd.screenplay.ensure.BlackBox
import net.serenitybdd.screenplay.ensure.CommonPreconditions
import net.serenitybdd.screenplay.ensure.Expectation
import net.serenitybdd.screenplay.ensure.KnowableValue
import net.serenitybdd.screenplay.ensure.expectThatActualIs
import org.assertj.core.api.Assertions.assertThat

/**
 * Expose comparison methods for [LastResponseEnsure].
 *
 * @param rootPath path to read from response
 * @param bodyPath extra path to parse body
 */
class LastResponseComparable(rootPath: String, bodyPath: String = "") {
    val rootPath: String
    val bodyPath: String

    init {
        check(rootPath, bodyPath)
        this.rootPath = rootPath
        this.bodyPath = bodyPath
    }

    private fun check(rootPath: String, bodyPath: String) {
        assertThat(rootPath).withFailMessage("Root path [$rootPath] should start with '$'").startsWith("$")
        assertThat(bodyPath).withFailMessage("Body path [$bodyPath] should not start with '.'").doesNotStartWith(".")
        assertThat(bodyPath).withFailMessage("Body path [$bodyPath] should not start with '$'").doesNotStartWith("$")
    }

    fun <A> isEqualComparator(): Expectation<KnowableValue<A>?, A> {
        return expectThatActualIs("equal to", fun(actor: Actor?, actual: KnowableValue<A>?, expected: A): Boolean {
            CommonPreconditions.ensureActualAndExpectedNotNull(actual, expected)
            val resolvedValue = actual!!(actor!!)
            BlackBox.logAssertion(resolvedValue, expected)
            return resolvedValue == expected
        })
    }

    inline fun <reified A> isEqualTo(expected: A): LastResponseInteraction {
        return LastResponseInteraction.perform(rootPath, bodyPath, expected, isEqualComparator())
    }
}
