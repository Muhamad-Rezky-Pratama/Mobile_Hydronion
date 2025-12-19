package com.example.hydronion

import LoginRequest
import LoginResponse
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.hydronion.databinding.FragmentLoginBinding
import retrofit2.Response
import retrofit2.Call

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }
        binding.btnLogin.setOnClickListener {
            handleLogin()
        }
    }

    private fun handleLogin() {
        val username = binding.etUsername.text.toString()
        val password = binding.etPassword.text.toString()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Data tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val request = LoginRequest(username, password)

        ApiClient.api.login(request).enqueue(object : retrofit2.Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!

                    // --- SIMPAN DATA SESI KE SHAREDPREFERENCES ---
                    val sharedPref = requireContext().getSharedPreferences("SessionHydronion", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("username", user.username)
                        putString("role", user.role)
                        // Mengambil ID dari database dan memformatnya
                        putString("userId", "Hydronion-${user.id}")
                        putBoolean("isLoggedIn", true)
                        apply()
                    }

                    onLoginSuccess(user.role)
                } else {
                    Toast.makeText(requireContext(), "Login gagal: Periksa kembali akun Anda", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Gagal koneksi API", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun onLoginSuccess(role: String) {
        val mainActivity = requireActivity() as MainActivity
        when (role) {
            "admin" -> mainActivity.switchToAdmin()
            "user" -> mainActivity.switchToUser()
            else -> Toast.makeText(requireContext(), "Role tidak dikenali", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}