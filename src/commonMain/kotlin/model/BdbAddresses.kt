package drewcarlson.blockset

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed class BdbAddressesResult

@Serializable
data class BdbAddresses(
    @SerialName("_embedded")
    val embedded: Embedded,
) : BdbAddressesResult() {

    @Serializable
    data class Embedded(
        val addresses: List<BdbAddress>
    )
}

data class BdbAddressesError(
    val status: Int,
    val body: String?,
) : BdbAddressesResult()

@Serializable
data class BdbAddress(
    @SerialName("currency_code")
    val currencyCode: String,
    val status: Status? = null,
    val address: String? = null,
    val resolver: String? = null,
) {
    @Serializable
    enum class Status {
        @SerialName("success")
        SUCCESS,
        @SerialName("unknown_currency")
        UNKNOWN_CURRENCY,
        @SerialName("unsupported_domain")
        UNSUPPORTED_DOMAIN,
        @SerialName("record_not_found")
        RECORD_NOT_FOUND,
        @SerialName("blockchain_is_down")
        BLOCKCHAIN_IS_DOWN,
        @SerialName("unknown_error")
        UNKNOWN_ERROR,
        @SerialName("incorrect_contract_address")
        INCORRECT_CONTRACT_ADDRESS,
        @SerialName("incorrect_method_name")
        INCORRECT_METHOD_NAME,
        @SerialName("unspecified_resolver")
        UNSPECIFIED_RESOLVER,
        @SerialName("unsupported_currency")
        UNSUPPORTED_CURRENCY,
        @SerialName("not_implemented")
        NOT_IMPLEMENTED,
    }
}
