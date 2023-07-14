package io.iohk.atala.automation.extensions

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath

/**
 * Parses the object to [com.jayway.jsonpath.JsonPath] object.
 *
 * This allows the json manipulation to remove nodes and such.
 * Mainly it's used to make the json manipulation easier for testing purposes.
 */
fun Any.toJsonPath(): DocumentContext {
    val json = ObjectMapper().writeValueAsString(this)
    return JsonPath.parse(json)
}
