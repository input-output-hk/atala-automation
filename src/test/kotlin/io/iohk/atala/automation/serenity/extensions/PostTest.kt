package io.iohk.atala.automation.serenity.extensions

import io.iohk.atala.automation.WithMockServer
import io.iohk.atala.automation.serenity.ensure.Ensure
import net.serenitybdd.core.Serenity
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.rest.abilities.CallAnApi
import net.serenitybdd.screenplay.rest.interactions.Post
import net.thucydides.core.steps.StepEventBus
import org.junit.After
import org.junit.Before
import org.junit.Test

class PostTest : WithMockServer() {
    class BodyTest {
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

        actor.attemptsTo(
            Post.to("/").body(BodyTest()),
            Ensure.thatTheLastResponse().statusCode().isEqualTo(200)
        )
    }
}