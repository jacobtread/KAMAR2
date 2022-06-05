package com.jacobtread.kamar2.api

import com.jacobtread.kamar2.response.AuthenticationException
import com.jacobtread.kamar2.response.AuthenticationResponse
import com.jacobtread.kamar2.utils.getElementByName
import com.jacobtread.kamar2.utils.getElementByNameOrNull
import com.jacobtread.kamar2.utils.number
import com.jacobtread.kamar2.utils.text
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.xml.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.w3c.dom.Document
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.jvm.Throws

object KAMAR {

    private const val USER_AGENT = "KAMAR/ CFNetwork/ Darwin/"
    private const val DEFAULT_KEY = "vtku"
    private val documentBuilderFactory = DocumentBuilderFactory
        .newInstance()

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

    @Throws(AuthenticationException::class, RequestException::class)
    suspend fun authenticate(username: String, password: String): AuthenticationResponse {
        val response = requestResource(
            "Logon", DEFAULT_KEY,
            mapOf(
                "Username" to username,
                "Password" to password
            )
        )
        val rootElement = response.documentElement

        val apiVersion = rootElement.getAttribute("apiversion")
        val portalVersion = rootElement.getAttribute("portalversion")

        val accessLevel = rootElement.getElementByName("AccessLevel").number()

        val errorElement = rootElement.getElementByNameOrNull("Error")
        if (errorElement != null) {
            val errorCode = rootElement.getElementByName("ErrorCode").number()
            throw AuthenticationException(accessLevel, errorElement.text(), errorCode)
        }

        val logonLevel = rootElement.getElementByName("LogonLevel").number()
        val currentStudent = rootElement.getElementByName("CurrentStudent").text()
        val key = rootElement.getElementByName("Key").text()

        return AuthenticationResponse(
            apiVersion,
            portalVersion,
            accessLevel,
            logonLevel,
            currentStudent,
            key
        )
    }

    class RequestException(reason: String) : RuntimeException(reason)

    @Throws(RequestException::class)
    private suspend fun requestResource(command: String, key: String, parameters: Map<String, String>): Document {
        return withContext(Dispatchers.IO) {
            val response = client.submitForm(
                url = createApiEndpoint(), formParameters = Parameters.build {
                    append("Key", key)
                    append("Command", command)
                    parameters.forEach { (key, value) ->
                        append(key, value)
                    }
                }
            ) {
                method = HttpMethod.Post
                header(HttpHeaders.UserAgent, USER_AGENT)
                header("X-Requested-With", "nz.co.KAMAR")
            }
            val rawBody = response.bodyAsText()
            val builder = documentBuilderFactory.newDocumentBuilder()
            val stream = InputSource(StringReader(rawBody))
            try {
                return@withContext builder.parse(stream)
            } catch (e: Exception) {
                throw RequestException("Failed to deserialize response")
            }
        }
    }
}