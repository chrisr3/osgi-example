package com.example.osgi.kotlin.test

import com.example.osgi.kotlin.resolvePath
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class KotlinExampleTest {
    @Test
    fun example() {
        val paths = resolvePath(Paths.get("."))
        assertEquals(0, paths.size)
    }
}
