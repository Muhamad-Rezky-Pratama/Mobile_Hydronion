import com.google.gson.annotations.SerializedName
data class SensorResponse(
    val tds: Int,
    val waterlevel: Float,
    val suhu: Float,
    val pH: Float,
    val hum: Int,
    // Tambahkan 4 field baru di bawah ini
    @SerializedName("avg_tds") val avg_tds: Int?,
    @SerializedName("avg_suhu") val avg_suhu: Float?,
    @SerializedName("avg_hum") val avg_hum: Int?,
    @SerializedName("avg_wl") val avg_wl: Float?
)