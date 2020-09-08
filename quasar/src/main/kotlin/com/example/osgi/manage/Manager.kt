@file:JvmName("Toolkit")
package com.example.osgi.manage

import co.paralleluniverse.fibers.DefaultFiberScheduler
import co.paralleluniverse.fibers.Fiber
import co.paralleluniverse.fibers.SuspendExecution
import co.paralleluniverse.strands.SuspendableCallable
import com.example.osgi.api.Freezer
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

    @Reference(target = "(component.name=greetings)")
    private val greetings: Greetings,

    @Reference
    private val freezer: Freezer
) {
    @Activate
    fun run() {
        val workers = listOf("Tom", "Dick", "Harry", "Barbara", "Jerry", "Margot", "Basil", "Sybil")
        doRollCall(workers)
        freezer.freeze(workers)
    }

    private fun doRollCall(workers: List<String>) {
        val partners = workers.toRandomPartners()
        val results = LinkedBlockingQueue<Fiber<in String>>()
        val fibers = mutableMapOf<String, Fiber<String>>()

        fibers += workers.mapIndexed { idx, name ->
            Fiber(name, DefaultFiberScheduler.getInstance(), SuspendableCallable @Throws(SuspendExecution::class, InterruptedException::class) {
                val worker = Fiber.currentFiber()
                val partner = fibers[partners[idx]]

                val message = greetings.greet(worker.name)

                logger.info("{} hands over to {}...", worker.name, partner?.name)
                Fiber.parkAndUnpark(partner)
                logger.info("... and {} carries on.", worker.name)

                // Notify that this fiber is completing.
                results.add(worker)
                message
            })
        }.associateBy(Fiber<String>::getName)
        fibers.values.forEach(Fiber<String>::start)

        while (fibers.isNotEmpty()) {
            val fiber: Fiber<in String> = results.take()
            logger.info("Role call: {}", fiber.get())
            fibers.keys.remove(fiber.name)
        }
    }
}

private fun List<String>.toRandomPartners(): List<String> {
    val result = ArrayList(shuffled())
    for (i in 0..size - 2) {
        val temp = result[i]
        if (temp == this[i]) {
            result[i] = result[i + 1]
            result[i + 1] = temp
        }
    }
    return result
}