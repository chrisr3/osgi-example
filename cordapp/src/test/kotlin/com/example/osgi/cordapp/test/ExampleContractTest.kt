package com.example.osgi.cordapp.test

import net.corda.core.contracts.Contract
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.osgi.test.common.annotation.InjectService
import org.osgi.test.junit5.service.ServiceExtension

@ExtendWith(ServiceExtension::class)
class ExampleContractTest {

    @InjectService(timeout = 1000)
    lateinit var contract: Contract

    @Test
    fun testCordapp() {
    }
}