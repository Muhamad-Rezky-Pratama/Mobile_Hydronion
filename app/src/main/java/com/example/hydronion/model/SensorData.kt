import com.google.gson.annotations.SerializedName

data class SensorData(

    @SerializedName("sensor_id")
    val sensorId: Int? = null,

    val tds: Float?,

    val suhu: Float?,

    @SerializedName("suhu_air")
    val suhuAir: Float?,

    val kelembapan: Float?,

    val status: String? = null,

    val timestamp: String?,

    @SerializedName("ip_address")
    val ipAddress: String? = null
)
