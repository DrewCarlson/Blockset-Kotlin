package drewcarlson.blockset.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BdbSubscriptions(
    @SerialName("_embedded")
    val embedded: Embedded
) {

    @Serializable
    data class Embedded(
        val subscriptions: List<BdbSubscription>
    )
}

@Serializable
data class BdbSubscription(
    @SerialName("subscription_id")
    val subscriptionId: String,
    @SerialName("device_id")
    val deviceId: String,
    val endpoint: String,
    val currencies: List<Currency>
) {

    @Serializable
    data class Create(
        @SerialName("device_id")
        val deviceId: String,
        val endpoint: Endpoint,
        val currencies: List<Currency>
    )

    @Serializable
    data class Currency(
        @SerialName("currency_id")
        val currencyId: String,
        val addresses: List<String>,
        val events: List<Event>
    )

    @Serializable
    data class Event(
        val name: String,
        val confirmations: Int // TODO: UInt
    )

    @Serializable
    data class Endpoint(
        val kind: String,
        val environment: String,
        val value: String
    )
}
