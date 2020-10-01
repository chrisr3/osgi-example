package com.example.osgi.cordapp.contract

import com.google.common.collect.ImmutableList
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.ContractState
import net.corda.core.crypto.Crypto
import net.corda.core.identity.AbstractParty
import net.corda.core.transactions.LedgerTransaction
import net.corda.core.utilities.OpaqueBytes
import org.osgi.service.component.annotations.Activate
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference
import org.osgi.service.log.Logger
import org.osgi.service.log.LoggerFactory
import java.security.PublicKey

@Suppress("unused")
@Component(immediate = true)
class ExampleContract @Activate constructor(
    @Reference(service = LoggerFactory::class)
    private val logger: Logger
) : Contract {
    private val extraStuff = ImmutableList.of("one", "two", "three")

    init {
        logger.info("CORDA Started {}", this::class.java)
    }

    override fun verify(tx: LedgerTransaction) {
        val cryptoData = tx.outputsOfType<CryptoState>()
        val validators = tx.commandsOfType<Validate>()

        val isValid = validators.all { validate ->
            with (validate.value) {
                cryptoData.all { crypto ->
                    Crypto.doVerify(schemeCodeName, publicKey, crypto.signature.bytes, crypto.original.bytes)
                }
            }
        }

        require(cryptoData.isNotEmpty() && validators.isNotEmpty() && isValid) {
            "Failed to validate signatures in command data"
        }
    }

    @Suppress("CanBeParameter", "MemberVisibilityCanBePrivate")
    class CryptoState(val owner: AbstractParty, val original: OpaqueBytes, val signature: OpaqueBytes) : ContractState {
        override val participants: List<AbstractParty> = listOf(owner)
    }

    class Validate(
        val schemeCodeName: String,
        val publicKey: PublicKey
    ) : CommandData
}
