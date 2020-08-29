package com.example.osgi.provider

import com.example.osgi.api.Greetings
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.ServiceScope.SINGLETON
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Component(scope = SINGLETON)
//class GreetingsImpl @Activate constructor(@Reference(service = LoggerFactory::class) logger: Logger) : Greetings {
class GreetingsImpl : Greetings {
    private companion object {
        private val logger: Logger = LoggerFactory.getLogger(GreetingsImpl::class.java)
    }

    init {
        logger.info("Activating Greetings")
    }

    override fun greet(name: String): String {
        return "Hello $name!"
    }
}