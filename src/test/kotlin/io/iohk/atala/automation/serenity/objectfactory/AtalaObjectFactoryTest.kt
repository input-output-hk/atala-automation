package io.iohk.atala.automation.serenity.objectfactory

import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import io.cucumber.core.exception.CucumberException
import io.iohk.atala.automation.WithMockServer
import io.iohk.atala.automation.extensions.get
import io.iohk.atala.automation.restassured.CustomGsonObjectMapperFactory
import net.serenitybdd.rest.SerenityRest
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.rest.abilities.CallAnApi
import net.serenitybdd.screenplay.rest.interactions.Get
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert
import org.junit.Test
import java.time.OffsetDateTime
import javax.inject.Inject

class AtalaObjectFactoryTest : WithMockServer() {
    object ObjectTestClass
    private class PrivateTestClass
    class ParameterizedTestClass(val parameter: String)
    class TestClass
    class Injectable {
        val test = "Test"
    }

    class Injected {
        @Inject
        lateinit var injectable: Injectable
    }

    data class Date(
        @SerializedName("date")
        val date: OffsetDateTime
    )

    @Test
    fun `AtalaObjectFactory should create new object instance if not present`() {
        val test: TestClass = AtalaObjectFactory.getInstance(TestClass::class)
        assertThat(test, notNullValue())
    }

    @Test
    fun `AtalaObjectFactory should not be able to instantiate private classes`() {
        val exception = Assert.assertThrows(CucumberException::class.java) {
            AtalaObjectFactory.getInstance(PrivateTestClass::class)
        }
        assertThat(exception.message!!, containsString("not accessible"))
    }

    @Test
    fun `AtalaObjectFactory should not be able to instantiate object`() {
        val exception = Assert.assertThrows(CucumberException::class.java) {
            AtalaObjectFactory.getInstance(ObjectTestClass::class)
        }
        assertThat(exception.message!!, containsString("no constructor"))
    }

    @Test
    fun `AtalaObjectFactory should not be able to instantiate class with not empty constructor`() {
        val exception = Assert.assertThrows(CucumberException::class.java) {
            AtalaObjectFactory.getInstance(ParameterizedTestClass::class)
        }
        assertThat(exception.message!!, containsString("empty constructor"))
    }

    @Test
    fun `AtalaObjectFactory should inject @Inject instances`() {
        val injected = AtalaObjectFactory.getInstance(Injected::class)
        assertThat(injected.injectable, notNullValue())
        assertThat(injected.injectable.test, equalTo("Test"))
    }

    @Test
    fun `AtalaObjectFactory should able to create new instance`() {
        val test = AtalaObjectFactory.getInstance(TestClass::class)
        assertThat(test, notNullValue())
    }

    @Test
    fun `Should parse OffsetDateTime when using AtalaObjectFactory`() {
        AtalaObjectFactory
        val actor = Actor.named("tester").whoCan(CallAnApi.at("http://localhost"))
        actor.attemptsTo(Get.resource("/offsetdatetime"))
        val date = SerenityRest.lastResponse().get<Date>()
        assertThat(date, notNullValue())
        assertThat(date.date.toString(), equalTo("2023-09-14T11:24:46.868625Z"))
    }

    @Test
    fun `Should be able to serialize and deserialize OffsetDateTime`() {
        data class DateModelTest(
            @SerializedName("dateTime")
            var dateTime: OffsetDateTime,
        )

        val adapter = CustomGsonObjectMapperFactory.OffsetDateTimeTypeAdapter()
        val gson = GsonBuilder().registerTypeAdapter(OffsetDateTime::class.java, adapter).create()
        val body = DateModelTest(OffsetDateTime.now())
        val json = gson.toJson(body)
        assertThat(json, containsString(body.dateTime.toString()))

        val deserialized = gson.fromJson(json, DateModelTest::class.java)
        assertThat(deserialized.dateTime, equalTo(body.dateTime))
    }
}
