data class SensorData(
    val tds: Int?,
    val waterlevel: Float?,
    val suhu: Float?,
    val pH: Float?,
    val hum: Int?,

    val avg_tds: Int? = null,
    val avg_suhu: Float? = null,
    val avg_hum: Int? = null,
    val avg_wl: Float? = null
)
