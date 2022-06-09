package com.jacobtread.kamar2.data

class AuthenticationData(
    val key: String,
    val id: String,
    val access: Int,
) {
    companion object {
        val DEFAULT = AuthenticationData("vtku", "", 0)
    }
}

