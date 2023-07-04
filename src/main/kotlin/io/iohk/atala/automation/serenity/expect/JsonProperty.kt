package io.iohk.atala.automation.serenity.expect

import io.restassured.path.json.JsonPath
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

object JsonProperty {
    fun toBe(jsonPath: String, expected: String): TypeSafeMatcher<JsonPath> {
        return object: TypeSafeMatcher<JsonPath>() {
            override fun describeTo(description: Description) {
                description.appendText("contain a specified attribute value equal to $expected")
            }

            override fun matchesSafely(actual: JsonPath): Boolean {
                return actual.getString(jsonPath) == expected
            }
        }
    }
}