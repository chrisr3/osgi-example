package com.example.osgi.work;

import co.paralleluniverse.fibers.CustomFiberWriter;
import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.SuspendableCallable;
import com.example.osgi.api.Greetings;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class JavaSleeper implements SuspendableCallable<String>, Serializable {
    private final Greetings greetings;
    private final transient CompletableFuture<byte[]> checkpoint;
    private final transient Function<CompletableFuture<byte[]>, CustomFiberWriter> factory;

    public JavaSleeper(Greetings greetings, CompletableFuture<byte[]> checkpoint, Function<CompletableFuture<byte[]>, CustomFiberWriter> factory) {
        this.greetings = greetings;
        this.checkpoint = checkpoint;
        this.factory = factory;
    }

    @Override
    public String run() throws SuspendExecution {
        Fiber.parkAndCustomSerialize(factory.apply(checkpoint));

        Fiber<?> worker = Fiber.currentFiber();
        return greetings.greet(worker.getName());
    }
}
