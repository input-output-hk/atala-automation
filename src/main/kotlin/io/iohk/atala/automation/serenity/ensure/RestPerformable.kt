package io.iohk.atala.automation.serenity.ensure

import com.google.gson.Gson
import com.jayway.jsonpath.JsonPath
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.Performable
import net.serenitybdd.screenplay.ensure.*

class RestPerformable<A: Comparable<A>>(private val jsonPath: String, val expected: A): Performable {
    private val isEqualComparator =
        expectThatActualIs("equal to",
            fun(actor: Actor?, actual: KnowableValue<Comparable<A>>?, expected: A): Boolean {
                CommonPreconditions.ensureActualAndExpectedNotNull(actual, expected)
                val resolvedValue = actual!!(actor!!)
                BlackBox.logAssertion(resolvedValue, expected)
                return resolvedValue == expected
            })

    override fun <T : Actor?> performAs(actor: T) {
        val json = Gson().toJson(SerenityRestJson())
        val actual = JsonPath.parse(json).read<A>(jsonPath)
        val knowValue = KnownValue(actual, actual.toString())
        isEqualComparator.apply(knowValue, expected, actor)
    }
}