package io.iohk.atala.automation.serenity.questions

import net.serenitybdd.screenplay.Question

/**
 * Answerable questions about array.
 */
object Array {
    fun <T> size(description: String, list: List<T>): Question<Int> {
        return Question.about(description).answeredBy { _ ->
            list.size
        }
    }
}
