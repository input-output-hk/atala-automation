package io.iohk.atala.automation.serenity.questions

import io.restassured.path.json.JsonPath
import net.serenitybdd.rest.SerenityRest
import net.serenitybdd.screenplay.Question
import net.serenitybdd.screenplay.rest.interactions.Get

/**
 * Answerable questions about HttpRequest
 */
object HttpRequest {
    /**
     * Answers a Get request.
     */
    fun get(url: String): Question<JsonPath> {
        return Question.about("get response body").answeredBy { actor ->
            val request = Get.resource(url)
            request.performAs(actor)
            SerenityRest.lastResponse().body.jsonPath()
        }
    }
}
