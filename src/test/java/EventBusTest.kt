import me.kbrewster.eventbus.Subscribe
import me.kbrewster.eventbus.eventbus
import me.kbrewster.eventbus.invokers.LMFInvoker
import org.junit.jupiter.api.*

class MessageReceivedEvent(val message: String)

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class EventBusTest {
    private val eventBus = eventbus {
        invoker { LMFInvoker() }
        exceptionHandler { e, _ -> println("Error occurred in method: ${e.message}")  }
        threadSafety { false }
    }

    @Test
    @Order(0)
    fun `subscribing class`() {
        eventBus.register(this)
    }

    @Subscribe
    fun `subscribed method`(event: MessageReceivedEvent) {
        // do something
    }

    @Test
    @Order(1)
    fun `posting event`() {
        repeat(10_000_000) {
            eventBus.post { MessageReceivedEvent("Hello world") }
        }
    }

    @Test
    @Order(2)
    fun `removing class`() {
        eventBus.unregister(this)
    }
}