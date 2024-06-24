package io.iohk.atala.automation

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalToJson
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.junit.WireMockRule
import io.restassured.RestAssured
import org.junit.Before
import org.junit.Rule


/**
 * Provides the mock server for all tests
 */
open class WithMockServer {
    private val httpsPort = 9009

    val baseUrl = "https://localhost:$httpsPort"

    @Rule
    @JvmField
    var wireMockRule: WireMockRule = WireMockRule(WireMockConfiguration.options().httpsPort(httpsPort))

    @Before
    fun setupServer() {
        RestAssured.useRelaxedHTTPSValidation()

        wireMockRule.stubFor(get("/")
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withBody("""{ "operation": "success" }""")
                )
        )

        wireMockRule.stubFor(post("/")
                .withRequestBody(equalToJson("{\"field\": \"some-value\"}"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withBody("""{ "operation": "success" }""")
                )
        )

        wireMockRule.stubFor(get("/field")
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("""{ "field": "response" }""")
            )
        )

        wireMockRule.stubFor(get("/nested")
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("""{ "nested": { "field": { "value": "value" } } }""")
            )
        )

        wireMockRule.stubFor(get(WireMock.urlEqualTo("/subfield"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("""{ "subfield": { "field": "response" }}""")
            )
        )

        wireMockRule.stubFor(get(WireMock.urlEqualTo("/simplelist"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("""{ "list": [1, 2, 3] }""")
            )
        )

        wireMockRule.stubFor(get(WireMock.urlEqualTo("/offsetdatetime"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("""{ "date": "2023-09-14T11:24:46.868625Z" }""")
            )
        )

        wireMockRule.stubFor(get(WireMock.urlEqualTo("/list"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("""[ "field", "response", { "date": "2023-09-14T11:24:46.868625Z"  } ]""")
            )
        )

        wireMockRule.stubFor(get(WireMock.urlEqualTo("/objects"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("""[ {"field": "response"}, {"field": "value"} ]""")
            )
        )
    }
}
