package com.jacobtread.kamar2.response

class AuthenticationException(
    val accessLevel: Int,
    val error: String,
    val errorCode: Int,
) : RuntimeException(error)

