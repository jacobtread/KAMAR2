package com.jacobtread.kamar2.response

data class AuthenticationResponse(
    val apiVersion: String,
    val portalVersion: String,

    val accessLevel: Int,
    val logonLevel: Int,
    val currentStudent: String,
    val key: String,
)

class AuthenticationException(
    val accessLevel: Int,
    val error: String,
    val errorCode: Int,
) : RuntimeException(error)

