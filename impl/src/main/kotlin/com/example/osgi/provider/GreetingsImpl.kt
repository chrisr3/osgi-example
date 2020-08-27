package com.example.osgi.provider

import com.example.osgi.api.Greetings
import org.osgi.service.component.annotations.Component

@Component
class GreetingsImpl : Greetings {
    override fun greet(name: String): String {
        return "Hello $name!"
    }
}