package com.example.osgi.manage

import co.paralleluniverse.fibers.DefaultFiberScheduler
import co.paralleluniverse.fibers.Fiber
import co.paralleluniverse.fibers.SuspendExecution
import co.paralleluniverse.strands.SuspendableCallable
import com.example.osgi.api.Greetings
import org.osgi.service.component.annotations.Activate
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference
import org.osgi.service.log.Logger
import org.osgi.service.log.LoggerFactory
import java.util.concurrent.LinkedBlockingQueue

@Component(immediate = true)
@Suppress("unused")
class Manager @Activate constructor(
    @Reference(service = LoggerFactory::class)
    private val logger: Logger,

    @Reference
    private val greeting: Greetings
) {
    @Activate
    fun doStandUp() {
        val workers = listOf("Tom", "Dick", "Harry", "Barbara", "Jerry", "Margot", "Basil")

        val results = LinkedBlockingQueue<Fiber<in String>>()
        val fibers = workers.map { name ->
            Fiber("greet-$name", DefaultFiberScheduler.getInstance(), SuspendableCallable @Throws(SuspendExecution::class, InterruptedException::class) {
                val message = greeting.greet(name)
                results.add(Fiber.currentFiber())
                message
            })
        }.associateByTo(LinkedHashMap(), Fiber<*>::getName)
        fibers.values.forEach(Fiber<String>::start)

        while (fibers.isNotEmpty()) {
            val fiber: Fiber<in String> = results.take()
            logger.info("Role call: {}", fiber.get())
            fibers.keys.remove(fiber.name)
        }
    }
}
