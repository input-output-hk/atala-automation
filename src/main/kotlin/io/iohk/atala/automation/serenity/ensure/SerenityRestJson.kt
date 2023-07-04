package io.iohk.atala.automation.serenity.ensure

import net.serenitybdd.rest.SerenityRest


class SerenityRestJson {
    val statusCode: Int
    val contentType: String
    val body: String

    init {
        val lastResponse = SerenityRest.lastResponse()
        this.statusCode = lastResponse.statusCode
        this.contentType = lastResponse.contentType
        this.body = lastResponse.body.asString()
    }
}