package io.iohk.atala.automation.cucumber.plugins

import io.cucumber.datatable.DataTable
import io.cucumber.java.Before
import io.cucumber.java.ParameterType
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.CucumberOptions
import io.iohk.atala.automation.serenity.ensure.Ensure
import net.serenitybdd.annotations.Step
import net.serenitybdd.cucumber.CucumberWithSerenity
import net.serenitybdd.screenplay.Ability
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.Interaction
import net.serenitybdd.screenplay.actors.OnStage
import net.serenitybdd.screenplay.actors.OnlineCast
import org.junit.runner.RunWith


@CucumberOptions(
    features = ["src/test/resources"],
    plugin = [SerenityWithCucumberFormatter.PLUGIN],
)
@RunWith(CucumberWithSerenity::class)
class SerenityPrettyRunner

class Test: Ability {
    open class Something : Interaction {

        @Step("{0} tests something")
        override fun <T : Actor> performAs(actor: T) {}
    }

    companion object {
        fun something(): Interaction {
            return Something()
        }
    }
}
class Steps {
    @ParameterType(".*")
    fun actor(actorName: String): Actor {
        return OnStage.theActorCalled(actorName).whoCan(Test())
    }

    @Before
    fun setTheStage() {
        OnStage.setTheStage(OnlineCast())
    }

    @Given("the {actor} actor")
    fun something(actor: Actor) {
        actor.attemptsTo(
            Test.something(),
            Ensure.that(1).isEqualTo(1),
        )
    }

    @When("some datatable")
    fun someDataTable(dataTable: DataTable) {
        println(dataTable)
    }

    @When("some {}, {}, {} parameters")
    fun someParameters(i1: Int, i2: Int, i3: Int) {
        println("$i1, $i2, $i3")
    }

    @When("some {} example")
    fun someExample(example: String) {
        println(example)
    }

    @Then("should pass")
    fun shouldPass() {}
}
