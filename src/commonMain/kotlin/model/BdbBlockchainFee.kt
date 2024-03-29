package drewcarlson.blockset.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BdbBlockchainFee(
    val fee: BdbAmount,
    val tier: String,
    @SerialName("estimated_confirmation_in")
    val confirmationTimeInMilliseconds: ULong,
)
