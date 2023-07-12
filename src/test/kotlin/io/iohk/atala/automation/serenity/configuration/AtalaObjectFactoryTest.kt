package io.iohk.atala.automation.serenity.configuration

import io.cucumber.core.exception.CucumberException
import io.iohk.atala.automation.serenity.di.AtalaObjectFactory
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert
import org.junit.Test
import javax.inject.Inject

class AtalaObjectFactoryTest {
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
}
