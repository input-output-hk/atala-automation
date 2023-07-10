package io.iohk.atala.automation.serenity.extensions

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.iohk.atala.automation.serenity.ensure.Ensure
import net.serenitybdd.core.Serenity
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.rest.abilities.CallAnApi
import net.serenitybdd.screenplay.rest.interactions.Post
import net.thucydides.core.steps.StepEventBus
import org.junit.After
import org.junit.Before
import org.junit.Test

class PostTest {

    private class BodyTest {
        val field = "some-value"
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
    fun `Post Rest Interaction should be enhanced with body property`() {
        val actor = Actor.named("Test").whoCan(CallAnApi.at("http://localhost"))

        val wireMockServer = WireMockServer(WireMockConfiguration.wireMockConfig().port(8080))
        wireMockServer.start()

        WireMock.stubFor(
            WireMock
                .post(WireMock.urlEqualTo("/"))
                .withRequestBody(WireMock.equalToJson("{\"field\": \"some-value\"}"))
                .willReturn(
                    WireMock.aResponse().withStatus(200)
                )
        )

        actor.attemptsTo(
            Post.to("/").body(BodyTest()),
            Ensure.thatTheLastResponse().statusCode().isEqualTo(200)
        )

        wireMockServer.stop()
    }
}