package com.example.osgi.work

import com.example.osgi.log.LoggingBridge
import com.example.osgi.manage.FreezerImpl
import com.example.osgi.manage.Manager
import org.junit.jupiter.api.Test
import org.osgi.service.log.Logger

class QuasarWorkTest {
    private val loggerFactory = LoggingBridge()

    private inline fun <reified T> loggerFor(): Logger {
        return loggerFactory.getLogger(T::class.java)
    }

    @Test
    fun testFibers() {
        val welcome = Welcome(loggerFor<Welcome>())
        val freezer = FreezerImpl(loggerFor<FreezerImpl>(), welcome)
        val worker = Worker(loggerFor<Worker>())
        val manager = Manager(loggerFor<Manager>(), worker, freezer)
        manager.run()
    }
}

