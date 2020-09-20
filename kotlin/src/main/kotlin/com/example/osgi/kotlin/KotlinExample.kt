@file:JvmName("KotlinExample")
package com.example.osgi.kotlin

import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList

fun resolvePath(path: Path): List<URL> {
    return Files.list(path).use { files ->
        files.filter(::isJarFile).map(Path::toURL).toList()
    }
}

private fun isJarFile(path: Path): Boolean = path.toString().endsWith(".jar", true)

private fun Path.toURL(): URL = toUri().toURL()
