package com.example.osgi.work

import co.paralleluniverse.fibers.Suspendable
import com.example.osgi.api.Greetings
import org.osgi.service.component.annotations.Activate
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference
import org.osgi.service.log.Logger
import org.osgi.service.log.LoggerFactory

@Suppress("unused")
@Component(name = "welcome")
class Welcome @Activate constructor(
    @Reference(service = LoggerFactory::class)
    private val logger: Logger
) : Greetings {
    @Suspendable
    override fun greet(name: String): String {
        logger.info("Thawed worker {}", name)
        return "Welcome back, $name!"
    }
}
