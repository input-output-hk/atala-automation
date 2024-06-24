package io.iohk.atala.automation.serenity.ensure

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.iohk.atala.automation.restassured.CustomGsonObjectMapperFactory
import net.serenitybdd.rest.SerenityRest

/**
 * It's not possible to turn [SerenityRest.lastResponse] to JsonPath.
 *
 * This class aims to simplify the object, so we can parse it to JsonPath
 * and get the attributes through xpath.
 */
class SerenityRestJson {
    val statusCode: String
    val contentType: String
    val body: Any

    init {
        val lastResponse = try {
            SerenityRest.lastResponse()
        } catch (e: NullPointerException) {
            throw AssertionError("Couldn't find the last response, did you make a prior REST request?", e)
        }

        this.statusCode = lastResponse.statusCode.toString()
        this.contentType = lastResponse.contentType

        val gson = CustomGsonObjectMapperFactory.builder().create()

        val jsonBody = lastResponse.body.asString()
        var bodyJsonObject: JsonObject? = null
        var bodyJsonArray: JsonArray? = null
        suppress {
            bodyJsonObject = gson.fromJson(jsonBody, JsonObject::class.java)
        }
        suppress {
            bodyJsonArray = gson.fromJson(jsonBody, JsonArray::class.java)
        }

        if (bodyJsonObject != null) {
            this.body = bodyJsonObject!!
        } else if (bodyJsonArray != null) {
            this.body = bodyJsonArray!!
        } else {
            throw IllegalStateException("Response type is not JsonObject nor JsonArray")
        }
    }

    override fun toString(): String {
        val gson = CustomGsonObjectMapperFactory.builder().create()
        return gson.toJson(this)
    }

    private fun suppress(function: () -> Unit) {
        try {
            function.invoke()
        } catch (_: Exception) {
        }
    }
}
