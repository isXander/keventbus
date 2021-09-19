
import com.google.common.eventbus.EventBus as GuavaBus
import com.google.common.eventbus.Subscribe as GuavaSubscribe
import dev.deamsy.eventbus.api.listener.EventListener as DesamsySubscribe
import me.kbrewster.eventbus.Subscribe as KBrewsterSubscribe
import me.kbrewster.eventbus.invokers.LMFInvoker
import me.kbrewster.eventbus.eventbus as kbrewsterbus
import dev.deamsy.eventbus.impl.asm.ASMEventBus as DesamsyBus
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SpeedTest {
    @Test
    fun `faster than desamsy`() {
        val desamsy = DesamsyBus()
        val kbrewster = kbrewsterbus {
            invoker { LMFInvoker() }
        }

        val desamsyStart = System.currentTimeMillis()
        desamsy.register(this)
        repeat(10_000_000) {
            desamsy.post(MessageReceivedEvent("KBrewster is god..."))
        }
        desamsy.unregister(this)
        val desamsyFinish = System.currentTimeMillis()

        val kbrewsterStart = System.currentTimeMillis()
        kbrewster.register(this)
        repeat(10_000_000) {
            kbrewster.post(MessageReceivedEvent("KBrewster is god..."))
        }
        kbrewster.unregister(this)
        val kbrewsterFinish = System.currentTimeMillis()

        val desamsyTime = desamsyFinish - desamsyStart
        val kbrewsterTime = kbrewsterFinish - kbrewsterStart
        println("Desamsy: $desamsyTime ms")
        println("KBrewster: $kbrewsterTime ms")
        assert(desamsyTime > kbrewsterTime)
    }

    @Test
    fun `faster than guava`() {
        val guava = GuavaBus()
        val kbrewster = kbrewsterbus {
            invoker { LMFInvoker() }
        }

        val guavaStart = System.currentTimeMillis()
        guava.register(this)
        repeat(10_000_000) {
            guava.post(MessageReceivedEvent("KBrewster is god..."))
        }
        guava.unregister(this)
        val guavaFinish = System.currentTimeMillis()

        val kbrewsterStart = System.currentTimeMillis()
        kbrewster.register(this)
        repeat(10_000_000) {
            kbrewster.post(MessageReceivedEvent("KBrewster is god..."))
        }
        kbrewster.unregister(this)
        val kbrewsterFinish = System.currentTimeMillis()

        val guavaTime = guavaFinish - guavaStart
        val kbrewsterTime = kbrewsterFinish - kbrewsterStart
        println("Guava: $guavaTime ms")
        println("KBrewster: $kbrewsterTime ms")
        assert(guavaTime > kbrewsterTime)
    }

    @KBrewsterSubscribe
    @GuavaSubscribe
    @DesamsySubscribe
    fun messageRecieved(event: MessageReceivedEvent) {
        // ...
    }
}