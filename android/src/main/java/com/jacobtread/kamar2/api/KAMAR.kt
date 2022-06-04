package com.jacobtread.kamar2.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.xml.*
import io.ktor.util.reflect.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException

object KAMAR {

    private const val USER_AGENT = "KAMAR/ CFNetwork/ Darwin/"
    private const val DEFAULT_KEY = "vtku"

    var address: String? = null

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            xml()
        }
    }

    private fun createApiEndpoint(): String {
        check(address != null) { "Attempted to send an API request without an address" }
        return "https://$address/api/api.php"
    }

    @SerialName("LogonResults")
    @Serializable
    data class AuthenticateResponse(
        @SerialName("AccessLevel") val accessLevel: Int,
        @SerialName("ErrorCode") val errorCode: Int,
        @SerialName("Success") val success: String,
        @SerialName("LogonLevel") val logonLevel: Int,
        @SerialName("CurrentStudent") val currentStudent: String,
        @SerialName("Key") val key: String,
    )

    suspend fun authenticate(username: String, password: String): Result<AuthenticateResponse> {
        return requestResource(
            "Logon", DEFAULT_KEY,
            mapOf(
                "Username" to username,
                "Password" to password
            )
        )
    }


    @Serializable
    class KAMARException(
        @SerialName("AccessLevel") val accessLevel: Int,
        @SerialName("Error") val error: String,
        @SerialName("ErrorCode") val errorCode: Int,
    ) : RuntimeException(error)

    class RequestException(reason: String) : RuntimeException(reason)


    private suspend inline fun <reified T> requestResource(command: String, key: String, parameters: Map<String, String>): Result<T> =
        requestResource(typeInfo<T>(), command, key, parameters)

    private suspend fun <T> requestResource(typeInfo: TypeInfo, command: String, key: String, parameters: Map<String, String>): Result<T> {
        val response = client.submitForm(
            url = createApiEndpoint(), formParameters = Parameters.build {
                append("Key", key)
                append("Command", command)
                parameters.forEach { (key, value) ->
                    append(key, value)
                }
            }, encodeInQuery = true
        ) {
            method = HttpMethod.Post
            header(HttpHeaders.UserAgent, USER_AGENT)
            header("X-Requested-With", "nz.co.KAMAR")
        }
        return try {
            val error = response.body<KAMARException>()
            Result.failure(error)
        } catch (e: SerializationException) {
            try {
                val body = response.body<T>(typeInfo)
                Result.success(body)
            } catch (e: SerializationException) {
                Result.failure(RequestException("Failed to deserialize response"))
            }
        }
    }
}