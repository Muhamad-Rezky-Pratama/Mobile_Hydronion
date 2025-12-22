import com.google.gson.annotations.SerializedName

data class SensorResponse(
    val tds: Int,
    val waterlevel: Float,
    val suhu: Float,
    val pH: Float,
    val hum: Int,
    @SerializedName("kelembapan") val kelembapan: Float? = 0f, // Tambahkan = 0f
    @SerializedName("timestamp") val timestamp: String? = "", // Tambahkan = ""
    @SerializedName("avg_tds") val avg_tds: Int? = null,
    @SerializedName("avg_suhu") val avg_suhu: Float? = null,
    @SerializedName("avg_hum") val avg_hum: Int? = null,
    @SerializedName("avg_wl") val avg_wl: Float? = null
)