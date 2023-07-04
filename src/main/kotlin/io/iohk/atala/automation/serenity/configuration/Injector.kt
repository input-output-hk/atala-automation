package io.iohk.atala.automation.serenity.configuration

import io.iohk.atala.automation.serenity.di.Wire

object Injector {
    fun inject(instance: Any) {
        val fields = instance.javaClass.declaredFields
        for (field in fields) {
            if (field.isAnnotationPresent(Wire::class.java)) {
                val injection = AtalaObjectFactory.getInstance(field.type)
                val default = field.canAccess(instance)
                field.isAccessible = true
                field.set(instance, injection)
                field.isAccessible = default
            }
        }

    }
}
