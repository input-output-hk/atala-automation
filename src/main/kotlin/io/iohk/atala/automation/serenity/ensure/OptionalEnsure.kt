package io.iohk.atala.automation.serenity.ensure

import java.util.Optional

/**
 * Custom Ensure class to test Optional type.
 *
 * @param optional optional object to be validated
 */
class OptionalEnsure<A>(private val optional: Optional<A>) {

    fun isEmpty() = Ensure.that(optional.isEmpty).isTrue()

    fun isPresent() = Ensure.that(optional.isPresent).isTrue()
}
