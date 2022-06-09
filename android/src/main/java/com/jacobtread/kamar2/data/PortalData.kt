package com.jacobtread.kamar2.data

data class PortalData(
    val apiVersion: String,
    val portalVersion: String,
) {
    companion object {
        val DEFAULT = PortalData("Unknown", "Unknown")
    }
}