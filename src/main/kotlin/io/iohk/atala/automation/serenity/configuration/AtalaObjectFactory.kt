package io.iohk.atala.automation.serenity.configuration

import io.cucumber.core.backend.ObjectFactory
import io.cucumber.core.plugin.ConfigureDriverFromTags
import net.serenitybdd.core.Serenity
import net.serenitybdd.core.annotations.events.BeforeScenario
import net.serenitybdd.core.lifecycle.LifecycleRegister
import net.thucydides.core.steps.StepEventBus
import java.util.*

class AtalaObjectFactory : ObjectFactory {
    companion object {
        private val classes = Collections.synchronizedSet(HashSet<Class<*>>())
        private val instances: MutableMap<Class<*>, Any> = Collections.synchronizedMap(HashMap())

        fun <T : Any> getInstance(type: Class<T>): T {
            ConfigureDriverFromTags.inTheCurrentTestOutcome()
            var instance = type.cast(instances[type])
            if (instance == null) {
                instance = cacheNewInstance(type)
            }
            Injector.inject(instance)
            return instance
        }

        private fun <T : Any> cacheNewInstance(type: Class<T>): T {
            val instance = newInstance(type)
            instances[type] = instance
            return instance
        }

        private fun <T> newInstance(type: Class<T>): T {
            val constructor = type.getConstructor()
            val instance: T = constructor.newInstance()

            Serenity.initializeWithNoStepListener(instance).throwExceptionsImmediately()
            if (StepEventBus.getParallelEventBus().isBaseStepListenerRegistered) {
                val newTestOutcome = StepEventBus.getParallelEventBus().baseStepListener.currentTestOutcome
                LifecycleRegister.register(instance)
                LifecycleRegister.invokeMethodsAnnotatedBy(BeforeScenario::class.java, newTestOutcome)
            }
            return instance
        }
    }

    override fun start() {}
    override fun stop() {
        instances.clear()
        Serenity.done(false)
    }

    override fun addClass(glueClass: Class<*>): Boolean {
        classes.add(glueClass)
        return true
    }

    override fun <T : Any> getInstance(type: Class<T>): T {
        return AtalaObjectFactory.getInstance(type)
    }
}
