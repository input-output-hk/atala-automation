package io.iohk.atala.automation

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.restassured.RestAssured
import kotlinx.coroutines.*
import net.serenitybdd.core.Serenity
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.rest.abilities.CallAnApi
import net.serenitybdd.screenplay.rest.interactions.Get
import net.thucydides.core.steps.StepEventBus
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.lessThan
import org.junit.*
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime


class UtilsTest {
    private class TestClass {
        var field: String = "test"
    }

    companion object {
        private lateinit var wireMockServer: WireMockServer
        private lateinit var actor: Actor

        @JvmStatic
        @BeforeClass
        fun setup() {
            actor = Actor.named("tester").whoCan(CallAnApi.at("http://localhost"))

            wireMockServer = WireMockServer(WireMockConfiguration.wireMockConfig().port(8080))
            wireMockServer.start()
        }

        @JvmStatic
        @AfterClass
        fun `Teardown Wiremock`() {
            wireMockServer.stop()
        }
    }

    @Before
    fun before() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = 8080
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
        stubFor(
            get(urlEqualTo("/"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""{ "field": "response" }""")
                )
        )

        actor.attemptsTo(Get.resource("/"))
        val typedResponse = Utils.lastResponseObject<TestClass>()
        assertThat(typedResponse, notNullValue())
        assertThat(typedResponse.field, equalTo("response"))
    }

    @Test
    fun `Get last response typed subfield object`() {
        stubFor(
            get(urlEqualTo("/"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""{ "subfield": { "field": "response" }}""")
                )
        )

        actor.attemptsTo(Get.resource("/"))
        val typedResponse = Utils.lastResponseObject<TestClass>("subfield")
        assertThat(typedResponse, notNullValue())
        assertThat(typedResponse.field, equalTo("response"))
    }

    @Test
    fun `Get last response typed list`() {
        stubFor(
            get(urlEqualTo("/"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""{ "list": [1, 2, 3] }""")
                )
        )

        actor.attemptsTo(Get.resource("/"))
        val typedList = Utils.lastResponseList<Int>("list")
        assertThat(typedList, notNullValue())
        assertThat(typedList.size, equalTo(3))
        assertThat(typedList, equalTo(listOf(1,2,3)))
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
            Assert.assertThrows(TimeoutCancellationException::class.java) {
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
