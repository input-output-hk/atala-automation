package io.iohk.atala.automation.serenity.ensure

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import com.jayway.jsonpath.JsonPath
import io.iohk.atala.automation.restassured.CustomGsonObjectMapperFactory
import net.serenitybdd.annotations.Step
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.Interaction
import net.serenitybdd.screenplay.ensure.Expectation
import net.serenitybdd.screenplay.ensure.KnowableValue
import net.serenitybdd.screenplay.ensure.KnownValue

interface LastResponseInteraction: Interaction {
    val description: String

    companion object {
        /**
         * Performs validation for [LastResponseComparable]
         *
         * @param rootPath path to read from response
         * @param bodyPath extra path to parse body
         * @param expected expected value for the current field
         * @param expectation matcher to validate the field
         */
        inline fun <reified A> perform(
            rootPath: String,
            bodyPath: String,
            expected: A,
            expectation: Expectation<KnowableValue<A>?, A>,
        ): LastResponseInteraction {
            return object : LastResponseInteraction {
                private val expectedDescription by lazy {
                    var expectedDescription = "SerenityRest.lastResponse().$rootPath"
                    if (bodyPath.isNotEmpty()) {
                        expectedDescription += ".$bodyPath"
                    }
                    expectedDescription
                }

                @Suppress("unused")
                override val description by lazy {
                    expectation.describe(expected, false, expectedDescription)
                }

                @Step("{0} should see that #description")
                override fun <T : Actor> performAs(actor: T) {
                    val serenityResponse = SerenityRestJson()
                    val responseJson = serenityResponse.toString()
                    val documentContext = JsonPath.parse(responseJson)

                    val path = if (bodyPath.isNotEmpty()) { "$rootPath.$bodyPath" } else { rootPath }
                    val gson = CustomGsonObjectMapperFactory.builder().create()
                    val json = gson.toJson(documentContext.read<Any>(path))
                    val value = isSingleValue<A>(json, gson)
                    val actual = if (A::class == String::class && !value) {
                        json as A // return the value itself
                    } else {
                        gson.fromJson(json, A::class.java)
                    }

                    val knowValue: KnownValue<A> = KnownValue(actual, actual.toString())
                    val result: Boolean = expectation.apply(knowValue, expected, actor)
                    if (!result) {
                        throw AssertionError(expectation.compareActualWithExpected(knowValue, expected, false, expectedDescription))
                    }

                }

                private inline fun <reified A> isSingleValue(json: String, gson: Gson): Boolean {
                    try {
                        gson.fromJson(json, A::class.java)
                        return true
                    } catch (e: JsonSyntaxException) {
                        return false
                    }
                }
            }
        }
    }
}
