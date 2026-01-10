
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("/sensor_data")
    fun getSensorData(
    ): Call<SensorApiResponse>

    @GET("/sensor_data")
    fun getSensorHistory(
        @Query("action") action: String = "history"
    ): Call<ApiHistoryResponse>

    @POST("api/control/{device}")
    fun controlDevice(
        @Path("device") device: String,
        @Body request: Map<String, Int>
    ): Call<ControlResponse>

    @POST("login")
    fun login(
        @Body request: LoginRequest
    ): Call<LoginResponse>

    @POST("register")
    fun register(
        @Body request : RegisterRequest
    ): Call<RegisterResponse>
}
