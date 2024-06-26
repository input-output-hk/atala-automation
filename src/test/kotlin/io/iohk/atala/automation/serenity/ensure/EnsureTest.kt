package io.iohk.atala.automation.serenity.ensure

import io.iohk.atala.automation.WithMockServer
import net.serenitybdd.annotations.DefaultUrl
import net.serenitybdd.annotations.Managed
import net.serenitybdd.core.Serenity
import net.serenitybdd.core.pages.PageObject
import net.serenitybdd.rest.SerenityRest
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.Question
import net.serenitybdd.screenplay.abilities.BrowseTheWeb
import net.serenitybdd.screenplay.actions.Open
import net.serenitybdd.screenplay.rest.abilities.CallAnApi
import net.serenitybdd.screenplay.rest.interactions.Get
import net.serenitybdd.screenplay.targets.ByTarget
import net.thucydides.core.steps.StepEventBus
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.util.*

class EnsureTest : WithMockServer() {
    @DefaultUrl("classpath:test.html")
    private class TestPage : PageObject()

    class Field(val field: String) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Field) return false
            return (field == other.field)
        }

        override fun toString(): String {
            return """${this::class.simpleName}(field="$field")"""
        }

        override fun hashCode(): Int {
            return field.hashCode()
        }
    }

    @Managed(driver = "chrome", options = "--headless")
    var driver: WebDriver? = null

    @Before
    fun setup() {
        Serenity.initialize(this)
        StepEventBus.getParallelEventBus().testStarted("unit test")
    }

    @After
    fun cleanup() {
        Serenity.done()
        Serenity.recordReportData()
    }

    @Test
    fun `Ensure should contain common comparators`() {
        val localDate = LocalDate.now()
        val localTime = LocalTime.now()

        val actor: Actor = Actor.named("Test")
        actor.attemptsTo(
            Ensure.that("").isEqualTo(""),
            Ensure.that(1).isEqualTo(1),
            Ensure.that(localDate).isEqualTo(localDate),
            Ensure.that(localTime).isEqualTo(localTime),
            Ensure.that(1F).isEqualTo(1F),
            Ensure.that(1.00).isEqualTo(1.00),
            Ensure.that(listOf(1)).isNotEmpty(),
            Ensure.that(Question.about("").answeredBy { "response" }) {
                it == "response"
            },
            Ensure.that("description", Question.about("subject").answeredBy { "response" }) {
                it == "response"
            },
            Ensure.that("description", Question.about("").answeredBy { "response" }).isEqualTo("response"),
            Ensure.that(Question.about("").answeredBy { "response" }).isEqualTo("response"),
            Ensure.that("description", Question.about("").answeredBy { listOf(1) }).isNotEmpty(),
            Ensure.that(Question.about("").answeredBy { listOf(1) }).isNotEmpty()
        )
    }

    @Test
    fun `Ensure should contain web comparators`() {
        val actor: Actor = Actor("Test").whoCan(BrowseTheWeb.with(driver))
        val target = ByTarget.the("button").locatedBy("//button[@id='buttonId']")
        val by = By.xpath(("//button[@id='buttonId']"))
        actor.attemptsTo(
            Open.browserOn(TestPage()),
            Ensure.thatTheCurrentPage().title().isEqualTo(""),
            Ensure.that(target).isDisplayed(),
            Ensure.that(by).isDisplayed(),
            Ensure.thatTheListOf(target).hasSize(1),
            Ensure.thatTheListOf(by).hasSize(1)
        )
    }

    @Test
    fun `Ensure should be able to check Optional for present value`() {
        val actor: Actor = Actor.named("Test")
        val optional = Optional.of("Test")

        actor.attemptsTo(Ensure.that(optional).isPresent())
        assertThrows(AssertionError::class.java) {
            actor.attemptsTo(Ensure.that(optional).isEmpty())
        }
    }

    @Test
    fun `Ensure should be able to check Optional for absent value`() {
        val actor: Actor = Actor.named("Test")

        val emptyOptional = Optional.empty<String>()
        actor.attemptsTo(Ensure.that(emptyOptional).isEmpty())
        assertThrows(AssertionError::class.java) {
            actor.attemptsTo(Ensure.that(emptyOptional).isPresent())
        }
    }

    @Test
    fun `Ensure should be able to check SerenityRest lastResponse()`() {
        val actor: Actor = Actor.named("Test").whoCan(CallAnApi.at(baseUrl))
        actor.attemptsTo(
            Get.resource("/field"),
            Ensure.thatTheLastResponse().statusCode().isEqualTo(200),
            Ensure.thatTheLastResponse().contentType().isEqualTo("application/json"),
            Ensure.thatTheLastResponse().body("field").isEqualTo("response")
        )
    }

    @Test
    fun `Ensure should be able to check SerenityRest lastResponse() status code`() {
        val actor: Actor = Actor.named("Test").whoCan(CallAnApi.at(baseUrl))
        actor.attemptsTo(
            Get.resource("/field"),
            Ensure.thatTheLastResponse().statusCode().isEqualTo(200)
        )
    }

    @Test
    fun `Ensure should be able to check SerenityRest lastResponse() typed body`() {
        val actor: Actor = Actor.named("Test").whoCan(CallAnApi.at(baseUrl))
        actor.attemptsTo(
            Get.resource("/field"),
            Ensure.thatTheLastResponse().body().isEqualTo(
                Field("response")
            )
        )
    }

    @Test
    fun `Ensure should be able to check SerenityRest lastResponse() array response body`() {
        val actor: Actor = Actor.named("Test").whoCan(CallAnApi.at(baseUrl))
        actor.attemptsTo(
            Get.resource("/list"),
            Ensure.thatTheLastResponse().body("[0]").isEqualTo("field"),
            Ensure.thatTheLastResponse().body("[1]").isEqualTo("response"),
            Ensure.thatTheLastResponse().body("[2].date").isEqualTo("2023-09-14T11:24:46.868625Z"),
            Ensure.thatTheLastResponse().body("[2].date").isEqualTo(OffsetDateTime.parse("2023-09-14T11:24:46.868625Z")),
        )
    }

    @Test
    fun `Ensure should be able to check SerenityRest lastResponse() array of object response body`() {
        val actor: Actor = Actor.named("Test").whoCan(CallAnApi.at(baseUrl))
        actor.attemptsTo(
            Get.resource("/objects"),
            Ensure.thatTheLastResponse().body("[0].field").isEqualTo("response"),
            Ensure.thatTheLastResponse().body("[1]").isEqualTo(Field("value")),
        )
    }

    @Test
    fun `Ensure should be able to check SerenityRest lastResponse() stringified object body`() {
        val actor: Actor = Actor.named("Test").whoCan(CallAnApi.at(baseUrl))
        actor.attemptsTo(
            Get.resource("/objects"),
            Ensure.thatTheLastResponse().body("[0]").isEqualTo("""{"field":"response"}"""),
        )
    }

    @Test
    fun `Ensure should throw the expected error`() {
        val actor: Actor = Actor.named("Test").whoCan(CallAnApi.at(baseUrl))
        val ex = assertThrows(AssertionError::class.java) {
            actor.attemptsTo(
                Get.resource("/field"),
                Ensure.thatTheLastResponse().statusCode().isEqualTo(200),
                Ensure.thatTheLastResponse().contentType().isEqualTo("application/json"),
                Ensure.thatTheLastResponse().body("field").isEqualTo("test")
            )
        }
        assertThat(ex.message, containsString("SerenityRest.lastResponse().\$.body.field that is equal to: <\"test\">"))
    }

    @Test
    fun `Ensure should be able to check SerenityRest lastResponse() nested body path`() {
        val actor: Actor = Actor.named("Test").whoCan(CallAnApi.at(baseUrl))
        actor.attemptsTo(
            Get.resource("/nested"),
            Ensure.thatTheLastResponse().statusCode().isEqualTo(200),
            Ensure.thatTheLastResponse().body("nested.field.value").isEqualTo("value")
        )
    }

    @Test
    fun `Ensure should thrown an exception if SerenityRest lastResponse is null`() {
        // clears any previous request
        SerenityRest.clear()

        val actor: Actor = Actor.named("Test").whoCan(CallAnApi.at(baseUrl))
        val exception = assertThrows(AssertionError::class.java) {
            actor.attemptsTo(
                Ensure.thatTheLastResponse().contentType().isEqualTo("application/json")
            )
        }
        assertThat(exception.message, containsString("Couldn't find the last response"))
    }

    @Test
    fun `LastResponseInteraction should have a description`() {
        val interaction = Ensure.thatTheLastResponse().statusCode().isEqualTo(1)
        assertThat(
            interaction.description,
            equalTo("SerenityRest.lastResponse().$.statusCode that is equal to: <1>")
        )
    }

    @Test
    fun `LastResponseInteraction should have a description with body path`() {
        val interaction = Ensure.thatTheLastResponse().body("my.path").isEqualTo(1)
        assertThat(
            interaction.description,
            equalTo("SerenityRest.lastResponse().$.body.my.path that is equal to: <1>")
        )
    }

    @Test
    fun `Ensure should be able to do soft assertions`() {
        val actor = Actor.named("Test")
        Ensure.enableSoftAssertions()
        try {
            actor.attemptsTo(
                Ensure.that("somevalue").isEqualTo("anothervalue"),
                Ensure.that("somevalue2").isEqualTo("anothervalue2")
            )
        } catch (_: Error) {
            val exception = assertThrows(AssertionError::class.java) {
                Ensure.reportSoftAssertions()
            }
            assertThat(exception.message, containsString("ASSERTION ERRORS"))
        }
    }
}
