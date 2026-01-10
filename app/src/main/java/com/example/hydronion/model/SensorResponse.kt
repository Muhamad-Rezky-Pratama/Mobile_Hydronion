import com.google.gson.annotations.SerializedName

data class SensorResponse(

    @SerializedName("sensor_id")
    val sensorId: Int?,

    val tds: Float?,

    val suhu: Float?,

    @SerializedName("suhu_air")
    val suhuAir: Float?,

    val kelembapan: Float?,

    val status: String?,

    val timestamp: String?,

    @SerializedName("ip_address")
    val ipAddress: String? = null
)
