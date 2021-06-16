package drewcarlson.blockset.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class BdbTransactions(
    @SerialName("_embedded")
    val embedded: Embedded = Embedded(emptyList()),
    @SerialName("_links")
    val links: Links? = null
) {

    @Serializable
    data class Embedded(val transactions: List<BdbTransaction>)
}

@Serializable
data class BdbTransaction(
    @SerialName("transaction_id")
    val transactionId: String,
    val identifier: String,
    val hash: String,
    @SerialName("blockchain_id")
    val blockchainId: String,
    val size: ULong,
    val fee: BdbAmount,
    val status: String,
    @SerialName("_embedded")
    val embedded: Embedded? = null,
    @SerialName("first_seen")
    val firstSeen: String? = null, // TODO: Date
    val timestamp: String? = null, // TODO: Date
    val index: ULong? = null,
    @SerialName("block_hash")
    val blockHash: String? = null,
    @SerialName("block_height")
    val blockHeight: ULong? = null,
    val acknowledgements: ULong? = null,
    val confirmations: ULong? = null,
    val raw: String? = null,
    val proof: String? = null
) {

    @Serializable
    data class Embedded(val transfers: List<BdbTransfer>)

    val transfers: List<BdbTransfer>
        get() = embedded?.transfers ?: emptyList()
}
