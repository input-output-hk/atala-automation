package io.iohk.atala.automation.serenity.ensure

import kotlin.reflect.KClass

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
    fun statusCode() = LastResponseComparable("$.statusCode")

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
    fun contentType() = LastResponseComparable("$.contentType")

    /**
     * Usage example:
     * ```
     * actor.attemptsTo(
     *     Ensure.thatTheLastResponse().body("path.to.variable")./*validation*/()
     * )
     * ```
     *
     * @see LastResponseComparable
     */
    fun body(path: String = "") = LastResponseComparable("$.body", path)
}
