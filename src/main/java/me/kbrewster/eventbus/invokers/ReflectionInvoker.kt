package me.kbrewster.eventbus.invokers

import java.lang.reflect.Method

class ReflectionInvoker : InvokerType {
    override fun setup(obj: Any, clazz: Class<*>, paramClazz: Class<*>, method: Method): (Any) -> Unit {
        method.isAccessible = true
        return { method.invoke(obj, it) }
    }
}