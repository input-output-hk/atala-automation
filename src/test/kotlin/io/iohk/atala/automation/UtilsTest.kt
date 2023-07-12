package io.iohk.atala.automation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.serenitybdd.core.Serenity
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.rest.abilities.CallAnApi
import net.serenitybdd.screenplay.rest.interactions.Get
import net.thucydides.core.steps.StepEventBus
import org.awaitility.core.ConditionTimeoutException
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.lessThan
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime


class UtilsTest : WithMockServer() {
    private class TestClass {
        var field: String = "test"
    }

    @Before
    fun before() {
        Serenity.initialize(this)
        StepEventBus.getParallelEventBus().testStarted("unit test")
    }

    @After
    fun cleanup() {
        Serenity.done()
    }

    @Test
    fun `Given Guice CDI we should be able to create new instance`() {
        val test = Utils.getInstance<TestClass>()
        assertThat(test.field, notNullValue())
    }

    @Test
    fun `Get last response typed object`() {
        val actor = Actor.named("tester").whoCan(CallAnApi.at("http://localhost"))
        actor.attemptsTo(Get.resource("/field"))
        val typedResponse = Utils.lastResponseObject<TestClass>()
        assertThat(typedResponse, notNullValue())
        assertThat(typedResponse.field, equalTo("response"))
    }

    @Test
    fun `Get last response typed subfield object`() {
        val actor = Actor.named("tester").whoCan(CallAnApi.at("http://localhost"))
        actor.attemptsTo(Get.resource("/subfield"))
        val typedResponse = Utils.lastResponseObject<TestClass>("subfield")
        assertThat(typedResponse, notNullValue())
        assertThat(typedResponse.field, equalTo("response"))
    }

    @Test
    fun `Get last response typed list`() {
        val actor = Actor.named("tester").whoCan(CallAnApi.at("http://localhost"))
        actor.attemptsTo(Get.resource("/list"))
        val typedList = Utils.lastResponseList<Int>("list")
        assertThat(typedList, notNullValue())
        assertThat(typedList.size, equalTo(3))
        assertThat(typedList, equalTo(listOf(1, 2, 3)))
    }

    @Test
    fun `Convert to JsonPath`() {
        val testClass = TestClass()
        val jsonPath = Utils.toJsonPath(testClass)
        assertThat(jsonPath.read("field"), equalTo("test"))
    }

    @Test
    @ExperimentalTime
    fun `Wait should wait maximum expected time`() {
        val timeout = 1.seconds
        val delta = measureTime {
            Assert.assertThrows(ConditionTimeoutException::class.java) {
                Utils.waitUntil(timeout) {
                    false
                }
            }
        }
        assertThat(delta, greaterThan(timeout))
    }

    @Test
    @ExperimentalTime
    fun `Wait should return fast if condition is met`() {
        val timeout = 1.minutes
        var condition = false

        CoroutineScope(Dispatchers.Default).launch {
            delay(500)
            condition = true
        }

        val delta = measureTime {
            Utils.waitUntil(timeout, 10.milliseconds) {
                condition
            }
        }
        assertThat(delta, lessThan(timeout))
    }
}
