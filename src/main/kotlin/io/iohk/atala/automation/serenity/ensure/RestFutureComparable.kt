package io.iohk.atala.automation.serenity.ensure

import net.serenitybdd.rest.SerenityRest
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.Performable
import net.serenitybdd.screenplay.ensure.*

class RestFutureComparable<A : Comparable<A>>(
    private val jsonPath: String
) {
    private val isEqualComparator: Expectation<((Actor) -> Comparable<A>?)?, A> =
        expectThatActualIs("equal to", fun(actor: Actor?, actual: KnowableValue<Comparable<A>>?, expected: A): Boolean {
            CommonPreconditions.ensureActualAndExpectedNotNull(actual, expected)
            val resolvedValue = actual!!(actor!!)
            BlackBox.logAssertion(resolvedValue, expected)
            return resolvedValue == expected
        })

    fun isEqualTo(expected: A): Performable {
        return RestPerformable(jsonPath, expected, isEqualComparator)
    }
}
