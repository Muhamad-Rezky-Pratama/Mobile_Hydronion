
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("/sensor_data")
    fun getSensorData(
    ): Call<List<SensorData>>

    @GET("/sensor_data?limit=200")
    fun getSensorHistory(): Call<List<SensorData>>

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

    @GET("users")
    fun getUsers(): Call<UserResponse>

    @PUT("users/{id}/username")
    fun updateUsername(
        @Path("id") userId: Int,
        @Body request: UpdateUsernameRequest
    ): Call<ApiResponse>

    @DELETE("users/{id}")
    fun deleteUser(
        @Path("id") userId: Int
    ): Call<ApiResponse>

}
