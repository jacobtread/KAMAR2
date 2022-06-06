package com.jacobtread.kamar2.response

data class GlobalsResponse(
    val periodDefinitions: Array<PeriodDefinition>,
    val startTimes: Array<Array<String>>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GlobalsResponse) return false
        if (!periodDefinitions.contentEquals(other.periodDefinitions)) return false
        if (!startTimes.contentDeepEquals(other.startTimes)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = periodDefinitions.contentHashCode()
        result = 31 * result + startTimes.contentDeepHashCode()
        return result
    }
}

data class PeriodDefinition(
    val name: String,
    val time: String,
)