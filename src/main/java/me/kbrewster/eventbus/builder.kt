package me.kbrewster.eventbus

import me.kbrewster.eventbus.invokers.InvokerType
import me.kbrewster.eventbus.invokers.ReflectionInvoker

fun eventbus(lambda: EventBusBuilder.() -> Unit): EventBus {
    return EventBusBuilder().apply(lambda).build()
}

class EventBusBuilder {
    /**
     * Default: reflection invoker
     */
    private var invokerType: InvokerType = ReflectionInvoker()

    /**
     * Default: throws exception again
     */
    private var exceptionHandler: (Exception, Any) -> Unit = { e, _ -> throw e }

    private var threadSafety = false

    fun invoker(lambda: () -> InvokerType) {
        this.invokerType = lambda()
    }

    fun threadSafety(lambda: () -> Boolean) {
        this.threadSafety = lambda()
    }

    fun exceptionHandler(lambda: (Exception, Any) -> Unit) {
        this.exceptionHandler = lambda
    }

    fun build() = EventBus(this.invokerType, this.exceptionHandler, this.threadSafety)
}