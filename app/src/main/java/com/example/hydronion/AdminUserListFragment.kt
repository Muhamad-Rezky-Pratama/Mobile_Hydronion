package com.example.hydronion

import ApiClient
import User
import UserResponse
import ApiResponse
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hydronion.databinding.FragmentAdminUserListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminUserListFragment : Fragment(R.layout.fragment_admin_user_list) {

    private var _binding: FragmentAdminUserListBinding? = null
    private val binding get() = _binding!!
    private lateinit var userAdapter: UserAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAdminUserListBinding.bind(view)

        setupRecyclerView()
        loadData()
    }

    private fun setupRecyclerView() {
        // ID ditulis sebagai angka (Int) tanpa tanda kutip agar tidak error
        val dummyData = mutableListOf(
            User(1, "admin_dani", "admin@mail.com", "admin")
        )

        userAdapter = UserAdapter(dummyData) { user ->
            // Memanggil ApiClient.instance (properti yang tadi error)
            deleteUser(user.id)
        }

        binding.rvUserList.layoutManager = LinearLayoutManager(context)
        binding.rvUserList.adapter = userAdapter
    }

    private fun loadData() {
        // Menggunakan ApiClient.api sesuai kode Anda
        ApiClient.api.getUsers().enqueue(object : Callback<List<User>> { // Ubah ke List<User> jika perlu
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    // Jika datanya langsung List, tidak perlu memanggil .data
                    val users = response.body()
                    if (users != null) {
                        userAdapter.updateData(users)
                    }
                } else {
                    Toast.makeText(context, "Server error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                // Log detail error ke Logcat agar Anda tahu penyebab pastinya
                android.util.Log.e("API_ERROR", "Penyebab Gagal: ${t.message}")
                Toast.makeText(context, "Gagal ambil data: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteUser(id: Int) {
        ApiClient.api.deleteUser(id).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "User Dihapus", Toast.LENGTH_SHORT).show()
                    loadData()
                }
            }
            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {}
        })
    }
}