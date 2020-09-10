package com.example.osgi.manage

import co.paralleluniverse.fibers.CustomFiberWriter
import co.paralleluniverse.fibers.DefaultFiberScheduler
import co.paralleluniverse.fibers.Fiber
import co.paralleluniverse.io.serialization.ByteArraySerializer
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
import java.util.concurrent.TimeUnit.SECONDS

@Component
class FreezerImpl @Activate constructor(
    @Reference
    private val loggerFactory: LoggerFactory,

    // Identify which implementation we want here.
    @Reference(target = "(component.name=welcome)")
    private val welcome: Greetings
) : Freezer {
    private val logger: Logger = loggerFactory.getLogger(this::class.java)
    private val loggerSerializer = LoggerSerializer(loggerFactory)

    override fun freeze(workers: List<String>) {
        val checkpoints = Array<CompletableFuture<ByteArray>>(workers.size) { CompletableFuture() }

        val fibers = workers.mapIndexed { idx, name ->
            logger.info("Freezing {}", name)
            Fiber(name, DefaultFiberScheduler.getInstance(), Sleeper(welcome, checkpoints[idx]) { checkpoint ->
                Pod(getSerializer(), checkpoint)
            })
        }.associateBy(Fiber<String>::getName)
        fibers.values.forEach(Fiber<String>::start)

        // Wait for everyone to finish checkpointing.
        CompletableFuture.allOf(*checkpoints).join()

        // Now wake everyone up!
        val running = checkpoints.mapTo(LinkedList()) { checkpoint ->
            @Suppress("unchecked_cast")
            val fiber = getSerializer().read(checkpoint.get()) as Fiber<String>
            fiber.setUncaughtExceptionHandler { f, e ->
                logger.error("Fiber ${f.name} GOES BOOM! {}", e.message)
                e.printStackTrace()
            }
            Fiber.unparkDeserialized(fiber, DefaultFiberScheduler.getInstance())
        }

        while (running.isNotEmpty()) {
            val fiber: Fiber<String> = running.pop()
            logger.info("Year 3000 says: {}", fiber.get(30, SECONDS))
        }
    }

    private fun getSerializer(): ByteArraySerializer {
        val serializer = Fiber.getFiberSerializer() as KryoSerializer
        val kryo = serializer.kryo
        kryo.classLoader = this::class.java.classLoader

        /**
         * Tell Kryo to use the same "special" [LoggerSerializer]
         * for all of the different ways we can create a [Logger].
         */
        kryo.addDefaultSerializer(Logger::class.java, loggerSerializer)
        kryo.register(loggerFactory.getLogger(ROOT)::class.java, LOGGER)
        kryo.register(loggerFactory.getLogger(this::class.java)::class.java, LOGGER)
        kryo.register(loggerFactory.getLogger(ROOT, Logger::class.java)::class.java, LOGGER)
        return serializer
    }

    companion object {
        const val LOGGER: Int = Int.MAX_VALUE
        const val ROOT = "ROOT"
    }
}

private class Pod(
    private val serializer: ByteArraySerializer,
    private val checkpoint: CompletableFuture<ByteArray>
) : CustomFiberWriter {
    override fun write(fiber: Fiber<*>) {
        checkpoint.complete(serializer.write(fiber))
    }
}
