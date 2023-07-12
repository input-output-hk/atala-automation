package io.iohk.atala.automation.matchers

import io.restassured.path.json.JsonPath
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

/**
 * Hamcrest matcher to validate [JsonPath][io.restassured.path.json.JsonPath] object.
 */
object RestAssuredJsonProperty {
    /**
     * Expect a property to have a String value. It uses JsonPath xpath to navigate to expected property.
     *
     * Serenity usage example:
     * ```
     * actor.attemptsTo(
     *     PollingWait.until(
     *         HttpRequest.get("/"),
     *         RestAssuredJsonProperty.toBe("xpath.to.something", "something")
     *     )
     * )
     * ```
     *
     * Hamcrest usage example:
     * ```
     * MatcherAssert.assertThat(jsonPathObject, RestAssuredJsonProperty.toBe("xpath.to.something", "something"))
     * ```
     *
     * @param jsonPath xpath for the property
     * @param expected expected value for the property
     */
    fun toBe(jsonPath: String, expected: String): TypeSafeMatcher<JsonPath> {
        return object : TypeSafeMatcher<JsonPath>() {
            override fun describeTo(description: Description) {
                description.appendText("contain a specified attribute value equal to $expected")
            }

            override fun matchesSafely(actual: JsonPath): Boolean {
                return actual.getString(jsonPath) == expected
            }
        }
    }
}