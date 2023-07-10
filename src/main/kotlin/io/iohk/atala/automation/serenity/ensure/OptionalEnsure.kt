package io.iohk.atala.automation.serenity.ensure

import java.util.Optional

class OptionalEnsure<A>(private val optional: Optional<A>) {
    fun isEmpty() = Ensure.that(optional.isEmpty).isTrue()
    fun isPresent() = Ensure.that(optional.isPresent).isTrue()
}