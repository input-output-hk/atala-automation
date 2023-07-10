package io.iohk.atala.automation.serenity.ensure

import net.serenitybdd.rest.SerenityRest

class RestResponseEnsure {
    fun statusCode() =  RestFutureComparable<Int>("statusCode")
    fun contentType() = RestFutureComparable<String>("contentType")
}