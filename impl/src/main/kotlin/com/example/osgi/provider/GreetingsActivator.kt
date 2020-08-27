package com.example.osgi.provider

import org.osgi.annotation.bundle.Header
import org.osgi.framework.BundleActivator
import org.osgi.framework.BundleContext
import org.osgi.framework.Constants.BUNDLE_ACTIVATOR

@Header(name = BUNDLE_ACTIVATOR, value = "\${@class}")
class GreetingsActivator : BundleActivator {
    override fun start(context: BundleContext) {
        println("Starting! WooHoo!")
    }

    override fun stop(context: BundleContext) {
        println("Stopping! Boo!!!")
    }
}