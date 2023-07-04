package io.iohk.atala.automation.serenity.ensure

import net.serenitybdd.rest.SerenityRest

class RestResponseEnsure {
    fun statusCode() =  RestFutureComparable("statusCode")
    fun contentType() = Ensure.that(SerenityRest.lastResponse().contentType)
    fun statusLine() = Ensure.that(SerenityRest.lastResponse().statusLine)
}