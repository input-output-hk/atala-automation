package io.iohk.atala.automation.serenity.ensure

import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.ensure.BlackBox
import net.serenitybdd.screenplay.ensure.CommonPreconditions
import net.serenitybdd.screenplay.ensure.KnowableValue
import net.serenitybdd.screenplay.ensure.expectThatActualIs

/**
 * Expose comparison methods for [LastResponseEnsure].
 *
 * @param jsonPath xpath to get the field
 */
class LastResponseComparable<A : Comparable<A>>(
    private val jsonPath: String
) {
    private val isEqualComparator =
        expectThatActualIs("equal to", fun(actor: Actor?, actual: KnowableValue<Comparable<A>>?, expected: A): Boolean {
            CommonPreconditions.ensureActualAndExpectedNotNull(actual, expected)
            val resolvedValue = actual!!(actor!!)
            BlackBox.logAssertion(resolvedValue, expected)
            return resolvedValue == expected
        })

    fun isEqualTo(expected: A): LastResponseInteraction<A> {
        return LastResponseInteraction(jsonPath, expected, isEqualComparator)
    }
}
