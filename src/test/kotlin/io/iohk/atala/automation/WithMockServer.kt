package io.iohk.atala.automation

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.restassured.RestAssured
import org.junit.After
import org.junit.Before

/**
 * Provides the mock server for all tests
 */
open class WithMockServer {
    private lateinit var wireMockServer: WireMockServer

    @Before
    fun setupServer() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = 8080
        wireMockServer = WireMockServer(WireMockConfiguration.wireMockConfig().port(8080))
        wireMockServer.start()

        WireMock.stubFor(
            WireMock.post(WireMock.urlEqualTo("/"))
                .withRequestBody(WireMock.equalToJson("{\"field\": \"some-value\"}")).willReturn(
                    WireMock.aResponse().withStatus(200).withBody("""{ "operation": "success" }""")
                )
        )

        WireMock.stubFor(
            WireMock.get(WireMock.urlEqualTo("/field")).willReturn(
                WireMock.aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                    .withBody("""{ "field": "response" }""")
            )
        )

        WireMock.stubFor(
            WireMock.get(WireMock.urlEqualTo("/subfield")).willReturn(
                WireMock.aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                    .withBody("""{ "subfield": { "field": "response" }}""")
            )
        )

        WireMock.stubFor(
            WireMock.get(WireMock.urlEqualTo("/list")).willReturn(
                WireMock.aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                    .withBody("""{ "list": [1, 2, 3] }""")
            )
        )

        WireMock.stubFor(
            WireMock.get(WireMock.urlEqualTo("/offsetdatetime")).willReturn(
                WireMock.aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                    .withBody("""{ "date": "2023-09-14T11:24:46.868625Z" }""")
            )
        )
    }

    @After
    fun teardown() {
        wireMockServer.stop()
    }
}
