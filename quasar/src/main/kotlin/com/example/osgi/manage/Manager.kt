package com.example.osgi.manage

import co.paralleluniverse.fibers.Fiber
import com.example.osgi.api.Greetings
import org.osgi.service.component.annotations.Activate
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference
import org.osgi.service.log.Logger
import org.osgi.service.log.LoggerFactory

@Component(immediate = true)
@Suppress("unused")
class Manager @Activate constructor(
    @Reference(service = LoggerFactory::class)
    private val logger: Logger,

    @Reference
    private val greeting: Greetings
) {
    @Activate
    fun doStandUp() {
        val workers = listOf("Tom", "Dick", "Harry", "Barbara", "Jerry", "Margot")
        val fibers = workers.mapTo(ArrayList()) { name ->
            CallRegister(name, greeting)
        }
        fibers.forEach(Fiber<String>::start)

        while (fibers.isNotEmpty()) {
            val greeting = fibers[0].get()
            logger.info("Role call: {}", greeting)
            fibers.removeAt(0)
        }
    }
}
