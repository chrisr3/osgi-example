package com.example.osgi.manage

import co.paralleluniverse.fibers.CustomFiberWriter
import co.paralleluniverse.fibers.DefaultFiberScheduler
import co.paralleluniverse.fibers.Fiber
import co.paralleluniverse.io.serialization.kryo.KryoSerializer
import com.example.osgi.api.Freezer
import com.example.osgi.api.Greetings
import com.example.osgi.work.Sleeper
import org.osgi.service.component.annotations.Activate
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference
import org.osgi.service.log.Logger
import org.osgi.service.log.LoggerFactory
import java.util.LinkedList
import java.util.concurrent.CompletableFuture

@Component
@Suppress("unused")
class FreezerImpl @Activate constructor(
    @Reference(service = LoggerFactory::class)
    private val logger: Logger,

    @Reference(target = "(component.name=welcome)")
    private val welcome: Greetings
) : Freezer {
    override fun freeze(workers: List<String>) {
        val checkpoints = Array<CompletableFuture<ByteArray>>(workers.size) { CompletableFuture() }

        val fibers = workers.mapIndexed { idx, name ->
            logger.info("Freezing {}", name)
            Fiber(name, DefaultFiberScheduler.getInstance(), Sleeper(welcome, checkpoints[idx], ::Pod))
        }.associateBy(Fiber<String>::getName)
        fibers.values.forEach(Fiber<String>::start)

        // Wait for everyone to finish checkpointing.
        CompletableFuture.allOf(*checkpoints).join()

        // Now wake everyone up!
        val running = checkpoints.mapTo(LinkedList()) { checkpoint ->
            val serializer = Fiber.getFiberSerializer().also { s ->
                (s as KryoSerializer).kryo.classLoader = this::class.java.classLoader
            }

            @Suppress("unchecked_cast")
            val fiber = serializer.read(checkpoint.get()) as Fiber<String>
            fiber.setUncaughtExceptionHandler { f, e ->
                logger.error("Fiber ${f.name} GOES BOOM! {}", e.message)
                e.printStackTrace()
            }
            Fiber.unparkDeserialized(fiber, DefaultFiberScheduler.getInstance())
        }

        while (running.isNotEmpty()) {
            val fiber: Fiber<String> = running.removeAt(0)
            logger.info("Year 3000 says: {}", fiber.get())
        }
    }
}

private class Pod(private val checkpoint: CompletableFuture<ByteArray>) : CustomFiberWriter {
    override fun write(fiber: Fiber<*>) {
        val serializer = Fiber.getFiberSerializer().also { s ->
            (s as KryoSerializer).kryo.classLoader = this::class.java.classLoader
        }
        checkpoint.complete(serializer.write(fiber))
    }
}
