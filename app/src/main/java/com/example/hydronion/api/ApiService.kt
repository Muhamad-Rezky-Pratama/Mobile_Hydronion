
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("sensor/latest")
    fun getSensorData(): Call<ApiResponse>

    @POST("control")
    fun sendControl(@Body request: ControlRequest): Call<Void>

    @POST("login")
    fun login(
        @Body request: LoginRequest
    ): Call<LoginResponse>

    @POST("register")
    fun register(
        @Body request: RegisterRequest
    ): Call<RegisterResponse>

    @GET("sensor/history")
    fun getSensorHistory(): Call<ApiHistoryResponse>
}
