package com.example.osgi.test

import com.example.osgi.api.Greetings
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import org.osgi.framework.FrameworkUtil
import org.osgi.util.tracker.ServiceTracker

class DeploymentTest {
    @Test
    fun testGreet() {
        assertEquals("Hello Bob!", greetings.greet("Bob"))
    }

    private val greetings: Greetings get() {
        val bundle = FrameworkUtil.getBundle(DeploymentTest::class.java) ?: fail("Bundle not found!")
        return with(ServiceTracker<Greetings, Greetings>(bundle.bundleContext, Greetings::class.java, null)) {
            open()
            waitForService(1000) ?: fail("No Greetings")
        }
    }
}