package drewcarlson.blockset.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BdbBlockchains(
    @SerialName("_embedded")
    val embedded: Embedded
) {

    @Serializable
    data class Embedded(
        val blockchains: List<BdbBlockchain>
    )
}

private val BLOCK_HEIGHT_UNSPECIFIED = ULong.MAX_VALUE

@Serializable
data class BdbBlockchain(
    val name: String,
    val id: String,
    @SerialName("native_currency_id")
    val currencyId: String,
    @SerialName("fee_estimates")
    val feeEstimates: List<BdbBlockchainFee>,
    val network: String,
    @SerialName("is_mainnet")
    val isMainnet: Boolean,
    @SerialName("confirmations_until_final")
    val confirmationsUntilFinal: UInt,
    @SerialName("block_height")
    val blockHeight: ULong,
    @SerialName("verified_block_hash")
    val verifiedBlockHash: String? = null,
    @SerialName("verified_height")
    val verifiedHeight: ULong? = null,
) {
    fun hasBlockHeight() = blockHeight != BLOCK_HEIGHT_UNSPECIFIED
}
