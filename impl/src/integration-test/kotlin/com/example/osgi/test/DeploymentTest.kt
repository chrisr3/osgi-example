package com.example.osgi.test

import kotlin.reflect.KClass;
import com.example.osgi.api.Greetings
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.osgi.framework.ServiceListener
import org.osgi.test.common.annotation.InjectService
import org.osgi.test.junit5.service.ServiceExtension

@ExtendWith(ServiceExtension::class)
class DeploymentTest {

    var listener: KClass<ServiceListener> = ServiceListener::class

    @InjectService(timeout = 1000)
    lateinit var greetings: Greetings

    @Test
    fun testGreet() {
        assertEquals("Hello Bob!", greetings.greet("Bob"))
    }
}
