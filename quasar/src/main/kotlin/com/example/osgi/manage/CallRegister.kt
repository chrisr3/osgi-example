package com.example.osgi.manage

import co.paralleluniverse.fibers.Fiber
import co.paralleluniverse.fibers.Suspendable
import com.example.osgi.api.Greetings

class CallRegister(private val worker: String, private val greeting: Greetings) : Fiber<String>() {
    @Suspendable
    override fun run(): String {
        return greeting.greet(worker)
    }
}
