package io.iohk.atala.automation

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath
import net.serenitybdd.rest.SerenityRest
import net.thucydides.core.guice.Injectors
import org.awaitility.Awaitility
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

/**
 * Helper methods.
 */
object Utils {
    /**
     * Retrieves an object instance of the requested type.
     */
    inline fun <reified T : Any> getInstance(): T {
        return Injectors.getInjector().getInstance(T::class.java)
    }

    /**
     * Retrieves the [SerenityRest.lastResponse] typed object from root
     */
    inline fun <reified T : Any> lastResponseObject(path: String = ""): T {
        return SerenityRest.lastResponse().jsonPath().getObject(path, T::class.java)
    }

    /**
     * Retrieves the [SerenityRest.lastResponse] typed object from provided xpath
     */
    inline fun <reified T : Any> lastResponseList(path: String = ""): List<T> {
        return SerenityRest.lastResponse().jsonPath().getList(path, T::class.java)
    }

    /**
     * Parses the object to [com.jayway.jsonpath.JsonPath] object.
     *
     * This allows the json manipulation to remove nodes and such.
     * Mainly it's used to make the json manipulation easier for testing purposes.
     */
    fun toJsonPath(any: Any): DocumentContext {
        val json = ObjectMapper().writeValueAsString(any)
        return JsonPath.parse(json)
    }

    /**
     * Blocks the execution until a condition is met.
     *
     * Usage example:
     * ```
     * Utils.waitUntil() {
     *     customRequest().statusCode == HttpStatus.SC_OK
     * }
     * ```
     *
     * @param timeout maximum time to wait
     * @param pollInterval polling interval
     * @param condition lambda expression to run the condition
     */
    fun waitUntil(
        timeout: Duration = 5.seconds,
        pollInterval: Duration = 500.milliseconds,
        condition: () -> Boolean
    ) {
        Awaitility.await()
            .with()
            .pollInterval(pollInterval.toJavaDuration())
            .atMost(timeout.toJavaDuration())
            .until {
                condition()
            }
    }
}