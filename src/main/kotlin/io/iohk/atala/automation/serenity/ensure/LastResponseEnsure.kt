package io.iohk.atala.automation.serenity.ensure

/**
 * Exposes the methods for [Ensure.thatTheLastResponse].
 */
class LastResponseEnsure {

    /**
     * Usage example:
     * ```
     * actor.attemptsTo(
     *     Ensure.thatTheLastResponse().statusCode()./*validation*/()
     * )
     * ```
     *
     * @see LastResponseComparable
     */
    fun statusCode() = LastResponseComparable<Int>("statusCode")

    /**
     * Usage example:
     * ```
     * actor.attemptsTo(
     *     Ensure.thatTheLastResponse().contentType()./*validation*/()
     * )
     * ```
     *
     * @see LastResponseComparable
     */
    fun contentType() = LastResponseComparable<String>("contentType")
}
