package io.iohk.atala.automation.serenity.extensions

import net.serenitybdd.screenplay.rest.interactions.Post
import net.serenitybdd.screenplay.rest.interactions.RestInteraction

/**
 * Extension to simplify the usage of body in Serenity Rest Post request.
 *
 * @param obj serializable object to be turned into json
 */
fun Post.body(obj: Any): RestInteraction {
    return this.with { request ->
        request.header("Content-Type", "application/json")
            .body(obj)
    }
}
