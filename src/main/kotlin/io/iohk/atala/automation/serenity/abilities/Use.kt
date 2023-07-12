package io.iohk.atala.automation.serenity.abilities

import net.serenitybdd.screenplay.Ability

/**
 * This is a sample class just for readability purposes.
 */
object Use {
    /**
     * Usage example:
     * ```
     * actor.whoCan(Use.theAbilityOf(/*ability*/)
     * ```
     *
     * @see net.serenitybdd.screenplay.Ability
     * @param ability any ability
     * @return the ability
     */
    fun <T : Ability> theAbilityOf(ability: T): T {
        return ability
    }
}
