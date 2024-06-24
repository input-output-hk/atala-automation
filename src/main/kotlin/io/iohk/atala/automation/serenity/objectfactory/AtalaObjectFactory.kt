package io.iohk.atala.automation.serenity.objectfactory

import io.cucumber.core.backend.ObjectFactory
import io.cucumber.core.exception.CucumberException
import io.cucumber.core.plugin.ConfigureDriverFromTags
import io.iohk.atala.automation.restassured.CustomGsonObjectMapperFactory
import io.restassured.config.ObjectMapperConfig
import io.restassured.config.RestAssuredConfig
import io.restassured.mapper.ObjectMapperType
import net.serenitybdd.core.Serenity
import net.serenitybdd.core.annotations.events.BeforeScenario
import net.serenitybdd.core.lifecycle.LifecycleRegister
import net.serenitybdd.rest.SerenityRest
import net.thucydides.core.steps.StepEventBus
import java.util.Collections
import javax.inject.Inject
import kotlin.reflect.KClass
import kotlin.reflect.full.IllegalCallableAccessException
import kotlin.reflect.full.cast
import kotlin.reflect.full.primaryConstructor

/**
 * Object Factory class used for Dependency Injection (DI).
 *
 * This class is exposed through SPI.
 */
class AtalaObjectFactory : ObjectFactory {
    companion object {
        private val classes = Collections.synchronizedSet(HashSet<Class<*>>())
        private val instances: MutableMap<KClass<*>, Any> = Collections.synchronizedMap(HashMap())

        init {
            val objectMapperConfig = ObjectMapperConfig(ObjectMapperType.GSON)
                .gsonObjectMapperFactory(CustomGsonObjectMapperFactory())
            val config = RestAssuredConfig.newConfig().objectMapperConfig(objectMapperConfig)
            SerenityRest.setDefaultConfig(config)
        }

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
            Serenity.initializeWithNoStepListener(instance)
            Serenity.throwExceptionsImmediately()
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
            } catch (e: IllegalCallableAccessException) {
                throw CucumberException("${type.qualifiedName} constructor is not accessible", e)
            } catch (e: NullPointerException) {
                throw CucumberException("${type.qualifiedName} has no constructor", e)
            } catch (e: IllegalArgumentException) {
                throw CucumberException("${type.qualifiedName} should have an empty constructor", e)
            }
        }

        private fun inject(instance: Any) {
            val fields = instance.javaClass.declaredFields
            for (field in fields) {
                if (field.isAnnotationPresent(Inject::class.java)) {
                    val injection = getInstance(field.type.kotlin)
                    val default = field.canAccess(instance)
                    field.isAccessible = true
                    field.set(instance, injection)
                    field.isAccessible = default
                }
            }
        }
    }

    override fun start() {
        instances.clear()
    }

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
