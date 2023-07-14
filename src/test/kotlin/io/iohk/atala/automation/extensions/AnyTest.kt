package io.iohk.atala.automation.extensions

import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Test

class AnyTest {
    private class TestClass {
        var field: String = "test"
    }

    @Test
    fun `Convert to JsonPath`() {
        val testClass = TestClass()
        val jsonPath = testClass.toJsonPath()
        MatcherAssert.assertThat(jsonPath.read("field"), CoreMatchers.equalTo("test"))
    }
}
