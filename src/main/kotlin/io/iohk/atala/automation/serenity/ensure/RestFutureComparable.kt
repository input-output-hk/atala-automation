package io.iohk.atala.automation.serenity.ensure

import net.serenitybdd.screenplay.Performable

class RestFutureComparable(private val jsonPath: String) {
    fun isEqualTo(expected: Int): Performable {
        return RestPerformable(jsonPath, expected)
    }
}
