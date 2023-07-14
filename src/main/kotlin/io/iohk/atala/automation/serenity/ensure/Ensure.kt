package io.iohk.atala.automation.serenity.ensure

import net.serenitybdd.screenplay.Question
import org.openqa.selenium.By
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import net.serenitybdd.screenplay.ensure.enableSoftAssertions as EnableSoftAssertions
import net.serenitybdd.screenplay.ensure.reportSoftAssertions as ReportSoftAssertions
import net.serenitybdd.screenplay.ensure.that as That
import net.serenitybdd.screenplay.ensure.thatTheCurrentPage as ThatTheCurrentPage
import net.serenitybdd.screenplay.ensure.thatTheListOf as ThatTheListOf
import net.serenitybdd.screenplay.targets.Target as SerenityTarget

/**
 * Centralization of the Ensure methods in a singleton class.
 */
object Ensure {
    fun that(value: String) = That(value)
    fun that(value: LocalDate) = That(value)
    fun that(value: LocalTime) = That(value)
    fun that(value: Boolean) = That(value)
    fun that(value: Float) = That(value)
    fun that(value: Double) = That(value)
    fun thatTheLastResponse() = LastResponseEnsure()
    fun <A> that(optional: Optional<A>) = OptionalEnsure(optional)

    fun <A> that(value: Comparable<A>) = That(value)
    fun <A> that(value: Collection<A>) = That(value)

    fun <A> that(question: Question<A>, predicate: (actual: A) -> Boolean) = That(question, predicate)
    fun <A> that(description: String, question: Question<A>, predicate: (actual: A) -> Boolean) =
        That(description, question, predicate)

    fun <A : Comparable<A>> that(description: String, question: Question<A>) = That(description, question)
    fun <A : Comparable<A>> that(question: Question<A>) = That(question)

    fun <A> that(description: String, question: Question<Collection<A>>) = That(description, question)
    fun <A> that(question: Question<Collection<A>>) = That(question)

    fun thatTheCurrentPage() = ThatTheCurrentPage()
    fun that(value: SerenityTarget) = That(value)
    fun that(value: By) = net.serenitybdd.screenplay.ensure.that(value)

    // Collection matchers
    fun thatTheListOf(value: SerenityTarget) = ThatTheListOf(value)
    fun thatTheListOf(value: By) = ThatTheListOf(value)

    fun enableSoftAssertions() = EnableSoftAssertions()
    fun reportSoftAssertions() = ReportSoftAssertions()
}
