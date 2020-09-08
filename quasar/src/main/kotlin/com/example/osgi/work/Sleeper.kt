package com.example.osgi.work

import co.paralleluniverse.fibers.CustomFiberWriter
import co.paralleluniverse.fibers.Fiber
import co.paralleluniverse.fibers.Suspendable
import co.paralleluniverse.io.serialization.kryo.KryoSerializer
import co.paralleluniverse.strands.SuspendableCallable
import com.example.osgi.api.Greetings
import java.util.concurrent.CompletableFuture

class Sleeper(
    private val greetings: Greetings,

    @Transient
    private val checkpoint: CompletableFuture<ByteArray>
) : SuspendableCallable<String>, CustomFiberWriter {
    override fun write(fiber: Fiber<*>) {
        val serializer = Fiber.getFiberSerializer().also { s ->
            (s as KryoSerializer).kryo.classLoader = this::class.java.classLoader
        }
        checkpoint.complete(serializer.write(fiber))
    }

    @Suspendable
    override fun run(): String {
        Fiber.parkAndCustomSerialize(this)

        val worker = Fiber.currentFiber()
        return greetings.greet(worker.name)
    }
}
