import com.google.gson.annotations.SerializedName

// Menggunakan Int agar cocok dengan Database Laragon Anda
data class User(
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("role") val role: String
)