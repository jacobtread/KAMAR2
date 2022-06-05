package com.jacobtread.kamar2.response

data class GlobalsResponse(
    val periodDefinitions: List<PeriodDefinition>,
    val startTimes: List<List<String>>
)

data class PeriodDefinition(
    val name: String,
    val time: String,
)