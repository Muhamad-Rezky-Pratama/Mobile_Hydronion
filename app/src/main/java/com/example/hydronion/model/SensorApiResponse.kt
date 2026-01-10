import com.google.gson.annotations.SerializedName

data class SensorApiResponse(
    val status: String,

    @SerializedName("sensor_data")
    val sensorData: SensorData?
)
