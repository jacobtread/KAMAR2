package com.jacobtread.kamar2.response

data class SettingsResponse(
    val settingsVersion: String,
    val logoName: String,
    val logoPath: String,
    val userAccess: List<UserAccess>,
)

data class UserAccess(
    val level: Int,
    val map: Map<String, String>,
)