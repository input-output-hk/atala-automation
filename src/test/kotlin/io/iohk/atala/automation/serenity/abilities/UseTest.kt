package io.iohk.atala.automation.serenity.abilities

import net.serenitybdd.screenplay.Ability
import net.serenitybdd.screenplay.Actor
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

internal class UseTest {

    private class CustomAbility : Ability {
        val test = "test"
    }

    @Test
    fun `Actor should be able to add ability using Use`() {
        val actor: Actor = Actor.named("Test").whoCan(Use.theAbilityOf(CustomAbility()))
        val actorAbility: CustomAbility = actor.abilityTo(CustomAbility::class.java)
        assertThat(actorAbility.test, equalTo("test"))
    }
}
