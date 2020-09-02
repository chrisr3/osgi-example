package com.example.osgi.provider

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.osgi.service.component.annotations.Activate
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference
import org.osgi.service.log.Logger
import org.osgi.service.log.LoggerFactory
import java.security.Security

@Component(immediate = true)
@Suppress("unused")
class BouncyCastleService @Activate constructor(
    @Reference(service = LoggerFactory::class)
    private val logger: Logger
) {
    @Activate
    private fun run() {
        logger.info("[OSGi] Start Bouncing!!!!")
        Security.insertProviderAt(BouncyCastleProvider(), 1)
        for (prov in Security.getProviders()) {
            logger.info("Provider: {}", prov.name)
        }
        for (algo in Security.getAlgorithms("Cipher")) {
            logger.info("Algorithm: {}", algo)
        }
    }
}
