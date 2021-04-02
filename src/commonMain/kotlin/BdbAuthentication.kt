package drewcarlson.blockset

import drewcarlson.blockset.model.BdbUserTokenResult
import io.ktor.client.HttpClient
import io.ktor.client.features.HttpClientFeature
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.header
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.PipelinePhase
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

typealias UserTokenFactory = suspend (
    http: HttpClient,
    clientJwt: String,
    deviceId: String,
    pubKey: String,
    signature: String
) -> BdbUserTokenResult

internal class BdbAuthentication {

    private lateinit var authProvider: BdbService.AuthProvider
    private lateinit var userTokenFactory: UserTokenFactory

    fun setAuthProvider(authProvider: BdbService.AuthProvider) {
        this.authProvider = authProvider
    }

    fun setUserTokenFactory(userTokenFactory: UserTokenFactory) {
        this.userTokenFactory = userTokenFactory
    }

    companion object : HttpClientFeature<BdbAuthentication, BdbAuthentication> {

        private val authMutex = Mutex()

        private val AuthenticationPhase = PipelinePhase("Authentication")

        override val key: AttributeKey<BdbAuthentication> = AttributeKey("BDBAuthentication")

        override fun prepare(block: BdbAuthentication.() -> Unit): BdbAuthentication =
            BdbAuthentication().apply(block)

        override fun install(feature: BdbAuthentication, scope: HttpClient) {
            val authProvider = feature.authProvider
            scope.requestPipeline.insertPhaseBefore(HttpRequestPipeline.Render, AuthenticationPhase)
            scope.requestPipeline.intercept(AuthenticationPhase) {
                if (context.headers.contains("Authorization")) return@intercept

                val token = feature.userTokenFactory.fetchToken(scope, authProvider) ?: return@intercept
                context.header("Authorization", "Bearer $token")
            }
        }

        private suspend fun UserTokenFactory.fetchToken(
            http: HttpClient,
            authProvider: BdbService.AuthProvider
        ): String? {
            return authProvider.readUserJwt() ?: authMutex.withLock {
                // Lock acquired after successful auth
                val latestToken = authProvider.readUserJwt()
                if (latestToken != null) return latestToken

                val clientJwt = authProvider.readClientJwt() ?: return@withLock null

                // invoke UserTokenFactor
                val result = invoke(
                    http,
                    clientJwt,
                    authProvider.readDeviceId(),
                    authProvider.readPubKey(),
                    authProvider.signData(clientJwt),
                )

                val userToken = when (result) {
                    is BdbUserTokenResult.Success -> result.token
                    is BdbUserTokenResult.Error -> {
                        println("Failed to create user token: $result")
                        null
                    }
                }

                val jwtHeader = buildJsonObject {
                    put("alg", "ES256")
                    put("typ", "JWT")
                }.jwtEncode()

                val jwtBody = buildJsonObject {
                    val (iat, exp) = authProvider.newTokenDetails()
                    put("sub", userToken)
                    put("iat", iat)
                    put("exp", exp)
                    put("brd:ct", "usr")
                    put("brd:cli", clientJwt)
                }.jwtEncode()

                val rawUserJwt = "$jwtHeader.$jwtBody"
                val jwtSignature = authProvider.signData(rawUserJwt).jwtEncode()

                "$rawUserJwt.$jwtSignature".also(authProvider::saveUserJwt)
            }
        }
    }
}

private fun JsonObject.jwtEncode() =
    json.encodeToString(this).jwtEncode()

private fun String.jwtEncode() =
    encodeBase64().base64ToBase64URL()

private fun String.base64ToBase64URL() =
    replace("+", "-")
        .replace("/", "_")
        .replace("=", "")
