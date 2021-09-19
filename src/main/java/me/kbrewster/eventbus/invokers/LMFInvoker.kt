package me.kbrewster.eventbus.invokers

import java.lang.invoke.LambdaMetafactory
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.lang.reflect.Method

class LMFInvoker : InvokerType {
    private lateinit var lookup: MethodLookup

    override fun setup(obj: Any, clazz: Class<*>, paramClazz: Class<*>, method: Method): (Any) -> Unit {
        method.isAccessible = true
        val caller = lazyPrivateLookup(clazz)
        val subscription: MethodType = MethodType.methodType(Void.TYPE, paramClazz)
        val target = caller.findVirtual(clazz, method.name, subscription)
        val site = LambdaMetafactory.metafactory(
            caller,
            "invoke",
            MethodType.methodType(SubscriberMethod::class.java, clazz),
            subscription.changeParameterType(0, Any::class.java),
            target,
            subscription
        )

        val factory = site.target
        return { (factory.bindTo(obj).invokeExact() as SubscriberMethod).invoke(it) }
    }

    private fun lazyPrivateLookup(clazz: Class<*>): MethodHandles.Lookup {
        return if (!this::lookup.isInitialized) { // try java 9 lookup
            try {
                this.lookup = MethodLookup.JAVA_9 // cache
                this.lookup.privateLookup(clazz)
            } catch (e: NoSuchMethodException) { // try java 8 lookup
                this.lookup = MethodLookup.JAVA_8 // cache
                this.lookup.privateLookup(clazz)
            }
        } else {
            this.lookup.privateLookup(clazz)
        }
    }

    internal enum class MethodLookup {
        JAVA_8 {
            // Java 8
            @Throws(Exception::class)
            override fun privateLookup(clazz: Class<*>): MethodHandles.Lookup {
                val lookupIn = MethodHandles.lookup().`in`(clazz)

                // and then we mark it as trusted for private lookup via reflection on private field
                val modes = MethodHandles.Lookup::class.java.getDeclaredField("allowedModes")
                modes.isAccessible = true
                modes.setInt(lookupIn, -1) // -1 == TRUSTED
                return lookupIn
            }
        },
        JAVA_9 {
            // Java 9+
            @Throws(Exception::class)
            override fun privateLookup(clazz: Class<*>): MethodHandles.Lookup {
                val privateLookupIn =
                    MethodHandles::class.java.getMethod(
                        "privateLookupIn",
                        Class::class.java,
                        MethodHandles.Lookup::class.java
                    )
                return privateLookupIn.invoke(null, clazz, MethodHandles.lookup()) as MethodHandles.Lookup
            }
        };

        abstract fun privateLookup(clazz: Class<*>): MethodHandles.Lookup
    }

    fun interface SubscriberMethod {
        operator fun invoke(event: Any)
    }
}