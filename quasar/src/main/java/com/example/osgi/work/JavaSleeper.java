package com.example.osgi.work;

import co.paralleluniverse.fibers.CustomFiberWriter;
import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.io.serialization.kryo.KryoSerializer;
import co.paralleluniverse.strands.SuspendableCallable;
import com.example.osgi.api.Greetings;

import java.util.concurrent.CompletableFuture;

public class JavaSleeper implements SuspendableCallable<String>, CustomFiberWriter {
    private final Greetings greetings;
    private final transient CompletableFuture<byte[]> checkpoint;

    public JavaSleeper(Greetings greetings, CompletableFuture<byte[]> checkpoint) {
        this.greetings = greetings;
        this.checkpoint = checkpoint;
    }

    @Override
    public String run() throws SuspendExecution {
        Fiber.parkAndCustomSerialize(this);

        Fiber<?> worker = Fiber.currentFiber();
        return greetings.greet(worker.getName());
    }

    @Override
    public void write(Fiber fiber) {
        KryoSerializer serializer = (KryoSerializer) Fiber.getFiberSerializer();
        serializer.kryo.setClassLoader(getClass().getClassLoader());
        checkpoint.complete(serializer.write(fiber));
    }
}
