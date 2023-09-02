package io.iohk.atala.automation.serenity.ensure

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.JsonPath
import net.serenitybdd.annotations.Step
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.Interaction
import net.serenitybdd.screenplay.ensure.Expectation
import net.serenitybdd.screenplay.ensure.KnownValue

/**
 * Performs validation for [LastResponseComparable]
 *
 * @param jsonPath xpath to get the field for validation
 * @param expected expected value for the current field
 * @param expectation matcher to validate the field
 */
class LastResponseInteraction<A : Comparable<A>>(
    private val jsonPath: String,
    private val expected: A,
    private val expectation: Expectation<((Actor) -> Comparable<A>?)?, A>
) : Interaction {
    private val expectedDescription = "SerenityRest.lastResponse().$jsonPath"
    val description = expectation.describe(expected, false, expectedDescription)

    @Step("{0} should see that #description")
    override fun <T : Actor?> performAs(actor: T) {
        val json = ObjectMapper().writeValueAsString(SerenityRestJson())
        val actual: A = JsonPath.parse(json).read(jsonPath)
        val knowValue: KnownValue<A> = KnownValue(actual, actual.toString())
        val result: Boolean = expectation.apply(knowValue, expected, actor)
        if (!result) {
            throw AssertionError(expectation.compareActualWithExpected(knowValue, expected, false, expectedDescription))
        }
    }
}
