package io.iohk.atala.automation

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import net.serenitybdd.rest.SerenityRest
import net.thucydides.core.guice.Injectors
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

object Utils {
    inline fun <reified T: Any> getInstance(): T {
        return Injectors.getInjector().getInstance(T::class.java)
    }

    fun <T : Any> lastResponseObject(path: String, clazz: KClass<T>): T {
        return SerenityRest.lastResponse().jsonPath().getObject(path, clazz.java)
    }

    fun <T : Any> lastResponseList(path: String, clazz: KClass<T>): List<T> {
        return SerenityRest.lastResponse().jsonPath().getList(path, clazz.java)
    }

    fun toJsonPath(any: Any) : DocumentContext {
        val json = ObjectMapper().writeValueAsString(any)
        return JsonPath.parse(json)
    }

    fun waitUntil(
        duration: Duration = 5.seconds,
        pollingEvery: Duration = 500.milliseconds,
        condition: () -> Boolean
    ) {
        runBlocking {
            withTimeout(duration) {
                while (true) {
                    if (condition()) {
                        break
                    }
                    delay(pollingEvery)
                }
            }
        }
    }
}