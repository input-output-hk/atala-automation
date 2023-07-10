package io.iohk.atala.automation.serenity.ensure

import com.google.gson.Gson
import com.jayway.jsonpath.JsonPath
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.Performable
import net.serenitybdd.screenplay.ensure.*
import net.thucydides.core.annotations.Step

class RestPerformable<A : Comparable<A>>(
    private val jsonPath: String,
    private val expected: A,
    private val expectation: Expectation<((Actor) -> Comparable<A>?)?, A>,
) : Performable {
    private val expectedDescription = "SerenityRest.lastResponse().${jsonPath}"
    private val description = expectation.describe(expected,false, expectedDescription);

    @Step("{0} should see that #description")
    override fun <T : Actor?> performAs(actor: T) {
        val json: String = Gson().toJson(SerenityRestJson())
        val actual: A = JsonPath.parse(json).read(jsonPath)
        val knowValue: KnownValue<A> = KnownValue(actual, actual.toString())
        val result: Boolean = expectation.apply(knowValue, expected, actor)
        if (!result) {
            throw AssertionError(expectation.compareActualWithExpected(knowValue, expected, false, expectedDescription))
        }
    }
}