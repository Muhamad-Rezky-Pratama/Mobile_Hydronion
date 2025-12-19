import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("sensor/latest")
    fun getSensorData(): Call<ApiResponse>

    @POST("api/control")
    fun controlPump(@Body request: ControlRequest): Call<Void>

    @POST("login")
    fun login(
        @Body request: LoginRequest
    ): Call<LoginResponse>

    @POST("register")
    fun register(
        @Body request: RegisterRequest
    ): Call<RegisterResponse>
}
