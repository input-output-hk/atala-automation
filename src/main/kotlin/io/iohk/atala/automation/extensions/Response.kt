package io.iohk.atala.automation.extensions

import io.restassured.response.Response
import net.serenitybdd.rest.SerenityRest

/**
 * Retrieves the [SerenityRest.lastResponse] typed object from root
 *
 * Usage example:
 * ```
 * Serenity.lastResponse().get<Type>(/* path */)
 * ```
 */
inline fun <reified T : Any> Response.get(path: String = ""): T {
    return this.jsonPath().getObject(path, T::class.java)
}

/**
 * Retrieves the [SerenityRest.lastResponse] typed object from provided xpath
 *
 * Usage example:
 * ```
 * Serenity.lastResponse().getList<Type>(/* path */)
 * ```
 */
inline fun <reified T : Any> Response.getList(path: String = ""): List<T> {
    return this.jsonPath().getList(path, T::class.java)
}
