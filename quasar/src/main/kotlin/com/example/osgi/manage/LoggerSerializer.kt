package com.example.osgi.manage

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.Serializer
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import org.osgi.service.log.Logger
import org.osgi.service.log.LoggerFactory

class LoggerSerializer constructor(
    private val factory: LoggerFactory
) : Serializer<Logger>(false, true) {
    override fun write(kryo: Kryo, output: Output, logger: Logger) {
        output.writeString(logger.name)
    }

    override fun read(kryo: Kryo, input: Input, type: Class<Logger>): Logger {
        val name = input.readString()
        return factory.getLogger(name)
    }
}