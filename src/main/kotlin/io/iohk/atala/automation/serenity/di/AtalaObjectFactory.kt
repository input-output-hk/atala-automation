package io.iohk.atala.automation.serenity.di

import io.cucumber.core.backend.ObjectFactory
import io.cucumber.core.exception.CucumberException
import io.cucumber.core.plugin.ConfigureDriverFromTags
import net.serenitybdd.core.Serenity
import net.serenitybdd.core.annotations.events.BeforeScenario
import net.serenitybdd.core.lifecycle.LifecycleRegister
import net.thucydides.core.steps.StepEventBus
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.*
import kotlin.reflect.jvm.isAccessible

class AtalaObjectFactory : ObjectFactory {
    companion object {
        private val classes = Collections.synchronizedSet(HashSet<Class<*>>())
        private val instances: MutableMap<KClass<*>, Any> = Collections.synchronizedMap(HashMap())

        fun <T : Any> getInstance(type: KClass<T>): T {
            ConfigureDriverFromTags.inTheCurrentTestOutcome()

            val instance = if (instances.containsKey(type)) {
                type.cast(instances[type])
            } else {
                cacheNewInstance(type)
            }
            inject(instance)
            return instance
        }

        private fun <T : Any> cacheNewInstance(type: KClass<T>): T {
            val instance = newInstance(type)
            instances[type] = instance
            return instance
        }

        private fun <T : Any> newInstance(type: KClass<T>): T {
            val instance = invokeConstructor(type)
            Serenity.initializeWithNoStepListener(instance).throwExceptionsImmediately()
            if (StepEventBus.getParallelEventBus().isBaseStepListenerRegistered) {
                val newTestOutcome = StepEventBus.getParallelEventBus().baseStepListener.currentTestOutcome
                LifecycleRegister.register(instance)
                LifecycleRegister.invokeMethodsAnnotatedBy(BeforeScenario::class.java, newTestOutcome)
            }
            return instance
        }

        private fun <T : Any> invokeConstructor(type: KClass<T>): T {
            val constructor = type.primaryConstructor
            try {
                return constructor!!.call()
            } catch (e: Exception) {
                val typeName = type.qualifiedName
                if (constructor == null) {
                    throw CucumberException("$typeName has no constructor", e)
                } else if (constructor.parameters.isNotEmpty()) {
                    throw CucumberException("$typeName should have an empty constructor", e)
                } else if (!constructor.isAccessible) {
                    throw CucumberException("$typeName constructor is not accessible", e)
                }
                throw e
            }
        }

        private fun inject(instance: Any) {
            val fields = instance.javaClass.declaredFields
            for (field in fields) {
                if (field.isAnnotationPresent(Wire::class.java)) {
                    val injection = getInstance(field.type.kotlin)
                    val default = field.canAccess(instance)
                    field.isAccessible = true
                    field.set(instance, injection)
                    field.isAccessible = default
                }
            }
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
        return getInstance(type.kotlin)
    }
}
