package me.kbrewster.eventbus.invokers

import java.lang.reflect.Method

interface InvokerType {
    fun setup(obj: Any, clazz: Class<*>, paramClazz: Class<*>, method: Method): (Any) -> Unit
}