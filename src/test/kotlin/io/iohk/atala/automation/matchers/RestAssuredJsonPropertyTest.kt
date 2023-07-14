package io.iohk.atala.automation.matchers

import com.google.gson.Gson
import io.restassured.path.json.JsonPath
import org.hamcrest.MatcherAssert
import org.junit.Test

class RestAssuredJsonPropertyTest {
    private class TestClass {
        var field = ""
    }

    @Test
    fun `Rest Assured - Json Property matcher`() {
        val testClass = TestClass()
        testClass.field = "test"
        val jsonPath = JsonPath(Gson().toJson(testClass))
        MatcherAssert.assertThat(jsonPath, RestAssuredJsonProperty.toBe("field", "test"))
    }
}
