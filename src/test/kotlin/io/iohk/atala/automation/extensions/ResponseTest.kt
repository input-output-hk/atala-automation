package io.iohk.atala.automation.extensions

import io.iohk.atala.automation.WithMockServer
import net.serenitybdd.core.Serenity
import net.serenitybdd.rest.SerenityRest
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.rest.abilities.CallAnApi
import net.serenitybdd.screenplay.rest.interactions.Get
import net.thucydides.core.steps.StepEventBus
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Test

class ResponseTest : WithMockServer() {
    private class TestClass {
        var field: String = "test"
    }

    @Before
    fun setup() {
        Serenity.initialize(this)
        StepEventBus.getParallelEventBus().testStarted("unit test")
    }

    @After
    fun cleanup() {
        Serenity.done()
    }

    @Test
    fun `Get last response typed object`() {
        val actor = Actor.named("tester").whoCan(CallAnApi.at(baseUrl))
        actor.attemptsTo(Get.resource("/field"))
        val typedResponse = SerenityRest.lastResponse().get<TestClass>()
        MatcherAssert.assertThat(typedResponse, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(typedResponse.field, CoreMatchers.equalTo("response"))
    }

    @Test
    fun `Get last response typed subfield object`() {
        val actor = Actor.named("tester").whoCan(CallAnApi.at(baseUrl))
        actor.attemptsTo(Get.resource("/subfield"))
        val typedResponse = SerenityRest.lastResponse().get<TestClass>("subfield")
        MatcherAssert.assertThat(typedResponse, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(typedResponse.field, CoreMatchers.equalTo("response"))
    }

    @Test
    fun `Get last response typed list`() {
        val actor = Actor.named("tester").whoCan(CallAnApi.at(baseUrl))
        actor.attemptsTo(Get.resource("/simplelist"))
        val typedList = SerenityRest.lastResponse().getList<Int>("list")
        MatcherAssert.assertThat(typedList, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(typedList.size, CoreMatchers.equalTo(3))
        MatcherAssert.assertThat(typedList, CoreMatchers.equalTo(listOf(1, 2, 3)))
    }
}
