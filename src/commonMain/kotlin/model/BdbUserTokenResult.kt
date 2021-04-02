package drewcarlson.blockset.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed class BdbUserTokenResult {

    @Serializable
    data class Success(
        val token: String,
        @SerialName("client_token")
        val clientToken: String,
    ) : BdbUserTokenResult()

    data class Error(
        val status: Int,
        val body: String?,
    ) : BdbUserTokenResult()
}
