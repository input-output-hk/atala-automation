package io.iohk.atala.automation.serenity.ensure

import net.serenitybdd.rest.SerenityRest

/**
 * It's not possible to turn [SerenityRest.lastResponse] to JsonPath.
 *
 * This class aims to simplify the object, so we can parse it to JsonPath
 * and get the attributes through xpath.
 */
class SerenityRestJson {
    val statusCode: Int
    val contentType: String
    val body: String

    init {
        val lastResponse = try {
            SerenityRest.lastResponse()
        } catch (e: Exception) {
            throw AssertionError("Couldn't find the last response, did you make a prior REST request?", e)
        }

        this.statusCode = lastResponse.statusCode
        this.contentType = lastResponse.contentType
        this.body = lastResponse.body.asString()
    }
}
