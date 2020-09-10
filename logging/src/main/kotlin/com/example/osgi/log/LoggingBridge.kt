package com.example.osgi.log

import org.osgi.framework.Bundle
import org.osgi.framework.ServiceReference
import org.osgi.service.component.annotations.Component
import org.osgi.service.log.LogService
import org.osgi.service.log.LogService.LOG_DEBUG
import org.osgi.service.log.LogService.LOG_ERROR
import org.osgi.service.log.LogService.LOG_INFO
import org.osgi.service.log.LogService.LOG_WARNING
import org.osgi.service.log.Logger
import org.osgi.service.log.LoggerConsumer
import org.osgi.service.log.LoggerFactory
import org.slf4j.Logger.ROOT_LOGGER_NAME
import java.lang.Exception
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * A crude implementation of OSGi's [LoggerFactory] and [LogService]
 * components that logs to the underlying SLF4J back-end.
 */
@Component(immediate = true, service = [ LoggerFactory::class ])
@Suppress("unused")
class LoggingBridge : LogService {
    override fun getLogger(name: String): Logger {
        return LoggerAdapter(org.slf4j.LoggerFactory.getLogger(name))
    }

    override fun getLogger(clazz: Class<*>): Logger {
        return LoggerAdapter(org.slf4j.LoggerFactory.getLogger(clazz))
    }

    override fun <L : Logger> getLogger(name: String, loggerType: Class<L>): L {
        val adapter = LoggerAdapter(org.slf4j.LoggerFactory.getLogger(name))
        return loggerType.cast(
            Proxy.newProxyInstance(this::class.java.classLoader, arrayOf(loggerType), LoggerHandler(adapter))
        )
    }

    override fun <L : Logger> getLogger(clazz: Class<*>, loggerType: Class<L>): L {
        val adapter = LoggerAdapter(org.slf4j.LoggerFactory.getLogger(clazz))
        return loggerType.cast(
            Proxy.newProxyInstance(clazz.classLoader, arrayOf(loggerType), LoggerHandler(adapter))
        )
    }

    override fun <L : Logger> getLogger(bundle: Bundle, name: String, loggerType: Class<L>): L {
        val adapter = LoggerAdapter(org.slf4j.LoggerFactory.getLogger(name))
        return loggerType.cast(
            Proxy.newProxyInstance(this::class.java.classLoader, arrayOf(loggerType), LoggerHandler(adapter))
        )
    }

    override fun log(level: Int, message: String?) {
        log(org.slf4j.LoggerFactory.getLogger(ROOT_LOGGER_NAME), level, message)
    }

    override fun log(sr: ServiceReference<*>, level: Int, message: String?) {
        log(org.slf4j.LoggerFactory.getLogger(sr::class.java), level, message)
    }

    @Suppress("deprecation")
    private fun log(logger: org.slf4j.Logger, level: Int, message: String?) {
        when (level) {
            LOG_DEBUG -> logger.debug(message)
            LOG_INFO -> logger.info(message)
            LOG_WARNING -> logger.warn(message)
            LOG_ERROR -> logger.error(message)
        }
    }

    override fun log(level: Int, message: String?, exception: Throwable?) {
        log(org.slf4j.LoggerFactory.getLogger(ROOT_LOGGER_NAME), level, message, exception)
    }

    override fun log(sr: ServiceReference<*>, level: Int, message: String?, exception: Throwable?) {
        log(org.slf4j.LoggerFactory.getLogger(sr::class.java), level, message, exception)
    }

    @Suppress("deprecation")
    private fun log(logger: org.slf4j.Logger, level: Int, message: String?, exception: Throwable?) {
        when (level) {
            LOG_DEBUG -> logger.debug(message, exception)
            LOG_INFO -> logger.info(message, exception)
            LOG_WARNING -> logger.warn(message, exception)
            LOG_ERROR -> logger.error(message, exception)
        }
    }

    private class LoggerHandler(private val logger: Logger) : InvocationHandler {
        override fun invoke(proxy: Any?, method: Method, args: Array<out Any>?): Any? {
            return method.invoke(logger, *(args ?: emptyArray()))
        }
    }

    private class LoggerAdapter(private val logger: org.slf4j.Logger) : Logger {
        override fun getName(): String {
            return logger.name
        }

        override fun isTraceEnabled(): Boolean {
            return logger.isTraceEnabled
        }

        override fun trace(message: String?) {
            logger.trace(message)
        }

        override fun trace(format: String?, arg: Any?) {
            logger.trace(format, arg)
        }

        override fun trace(format: String?, arg1: Any?, arg2: Any?) {
            logger.trace(format, arg1, arg2)
        }

        override fun trace(format: String?, vararg arguments: Any?) {
            logger.trace(format, *arguments)
        }

        override fun <E : Exception?> trace(consumer: LoggerConsumer<E>?) {
            if (isTraceEnabled) {
                consumer?.accept(this)
            }
        }

        override fun isDebugEnabled(): Boolean {
            return logger.isDebugEnabled
        }

        override fun debug(message: String?) {
            logger.debug(message)
        }

        override fun debug(format: String?, arg: Any?) {
            logger.debug(format, arg)
        }

        override fun debug(format: String?, arg1: Any?, arg2: Any?) {
            logger.debug(format, arg1, arg2)
        }

        override fun debug(format: String?, vararg arguments: Any?) {
            logger.debug(format, *arguments)
        }

        override fun <E : Exception?> debug(consumer: LoggerConsumer<E>?) {
            if (isDebugEnabled) {
                consumer?.accept(this)
            }
        }

        override fun isInfoEnabled(): Boolean {
            return logger.isInfoEnabled
        }

        override fun info(message: String?) {
            logger.info(message)
        }

        override fun info(format: String?, arg: Any?) {
            logger.info(format, arg)
        }

        override fun info(format: String?, arg1: Any?, arg2: Any?) {
            logger.info(format, arg1, arg2)
        }

        override fun info(format: String?, vararg arguments: Any?) {
            logger.info(format, *arguments)
        }

        override fun <E : Exception?> info(consumer: LoggerConsumer<E>?) {
            if (isInfoEnabled) {
                consumer?.accept(this)
            }
        }

        override fun isWarnEnabled(): Boolean {
            return logger.isWarnEnabled
        }

        override fun warn(message: String?) {
            logger.warn(message)
        }

        override fun warn(format: String?, arg: Any?) {
            logger.warn(format, arg)
        }

        override fun warn(format: String?, arg1: Any?, arg2: Any?) {
            logger.warn(format, arg1, arg2)
        }

        override fun warn(format: String?, vararg arguments: Any?) {
            logger.warn(format, *arguments)
        }

        override fun <E : Exception?> warn(consumer: LoggerConsumer<E>?) {
            if (isWarnEnabled) {
                consumer?.accept(this)
            }
        }

        override fun isErrorEnabled(): Boolean {
            return logger.isErrorEnabled
        }

        override fun error(message: String?) {
            logger.error(message)
        }

        override fun error(format: String?, arg: Any?) {
            logger.error(format, arg)
        }

        override fun error(format: String?, arg1: Any?, arg2: Any?) {
            logger.error(format, arg1, arg2)
        }

        override fun error(format: String?, vararg arguments: Any?) {
            logger.error(format, *arguments)
        }

        override fun <E : Exception?> error(consumer: LoggerConsumer<E>?) {
            if (isErrorEnabled) {
                consumer?.accept(this)
            }
        }

        override fun audit(message: String?) {
            logger.info(message)
        }

        override fun audit(format: String?, arg: Any?) {
            logger.info(format, arg)
        }

        override fun audit(format: String?, arg1: Any?, arg2: Any?) {
            logger.info(format, arg1, arg2)
        }

        override fun audit(format: String?, vararg arguments: Any?) {
            logger.info(format, *arguments)
        }
    }
}
