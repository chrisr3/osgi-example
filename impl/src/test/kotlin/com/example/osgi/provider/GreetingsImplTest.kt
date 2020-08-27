package com.example.osgi.provider

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GreetingsImplTest {
    @Test
    fun testGreeting() {
        val greeter = GreetingsImpl()
        assertEquals("Hello Bob!", greeter.greet("Bob"))
    }
}