package io.iohk.atala.automation.serenity.questions

import net.serenitybdd.screenplay.Actor
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class ArrayTest {
    @Test
    fun `Question about Array`() {
        val actor = Actor.named("Test")
        val size = Array.size("some description", listOf(1)).answeredBy(actor)
        assertThat(size, equalTo(1))
    }
}
