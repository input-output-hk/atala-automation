package io.iohk.atala.automation.serenity.questions

import io.iohk.atala.automation.WithMockServer
import net.serenitybdd.core.Serenity
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.rest.abilities.CallAnApi
import net.thucydides.core.steps.StepEventBus
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class HttpRequestTest : WithMockServer() {

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
    fun `Question about HttpRequest call`() {
        val actor = Actor.named("Test").whoCan(CallAnApi.at("http://localhost"))
        HttpRequest.get("/").answeredBy(actor)
    }

    @Test
    fun `Actor who can't CallAnApi shouldn't be able to question about HttpRequest`() {
        val actor = Actor.named("Test")
        Assert.assertThrows(NullPointerException::class.java) {
            HttpRequest.get("/").answeredBy(actor)
        }
    }
}