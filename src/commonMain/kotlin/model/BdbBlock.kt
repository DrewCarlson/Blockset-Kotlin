package drewcarlson.blockset.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BdbBlock(
    @SerialName("block_id")
    val blockId: String,
    val hash: String,
    @SerialName("blockchain_id")
    val blockchainId: String,
    val height: ULong,
    val mined: String,
    @SerialName("transaction_ids")
    val transactionIds: List<String>? = emptyList(),
    val size: ULong,
    @SerialName("total_fees")
    val totalFees: BdbAmount,
    val acknowledgements: ULong,
    @SerialName("is_active_chain")
    val isActiveChain: Boolean,
    @SerialName("_embedded")
    val embedded: Embedded? = null,
    @SerialName("prevHash")
    val prevHash: String? = null,
    @SerialName("nextHash")
    val nextHash: String? = null,
    val header: String? = null,
    val raw: String? = null
) {

    @Serializable
    data class Embedded(
        val transactions: List<BdbTransaction>
    )
}
