package io.iohk.atala.automation.serenity.questions

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import net.serenitybdd.core.Serenity
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.rest.abilities.CallAnApi
import net.thucydides.core.steps.StepEventBus
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class HttpRequestTest {
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
        val wireMockServer = WireMockServer(WireMockConfiguration.wireMockConfig().port(8080))
        wireMockServer.start()
        WireMock.stubFor(
            WireMock.get(WireMock.urlEqualTo("/"))
                .willReturn(
                    WireMock.aResponse().withStatus(200)
                )
        )

        val actor = Actor.named("Test").whoCan(CallAnApi.at("http://localhost"))
        HttpRequest.get("/").answeredBy(actor)

        wireMockServer.stop()

    }

    @Test
    fun `Actor who can't CallAnApi shouldn't be able to question about HttpRequest`() {
        val actor = Actor.named("Test")
        Assert.assertThrows(NullPointerException::class.java) {
            HttpRequest.get("/").answeredBy(actor)
        }
    }
}