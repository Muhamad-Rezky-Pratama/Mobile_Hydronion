package com.example.hydronion

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.hydronion.databinding.FragmentAccountBinding

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Ambil data sesi dari SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("SessionHydronion", Context.MODE_PRIVATE)
        val savedUsername = sharedPref.getString("username", "User")
        val savedRole = sharedPref.getString("role", "CLIENT")
        val savedUserId = sharedPref.getString("userId", "ITN-HIDRO-2025")

        // 2. Tampilkan data secara dinamis
        binding.tvUsername.text = savedUsername
        binding.tvRole.text = savedRole?.uppercase()
        binding.tvHidroponikId.text = savedUserId

        // 3. Menampilkan Alamat API
        binding.tvApiUrl.text = "Sumber Data Publik: \nhttps://api.openweathermap.org/data/2.5/weather"

        // 4. Logika Tombol Logout
        binding.btnLogout.setOnClickListener {
            handleLogout()
        }
    }

    private fun handleLogout() {
        // Hapus data sesi di SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("SessionHydronion", Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()

        Toast.makeText(requireContext(), "Logout Berhasil", Toast.LENGTH_SHORT).show()

        // Navigasi kembali ke Login dan bersihkan Backstack agar tidak bisa 'Back' lagi
        // Pastikan R.id.loginFragment sesuai dengan ID di nav_graph.xml Anda
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.accountFragment, true)
            .build()

        try {
            findNavController().navigate(R.id.loginFragment, null, navOptions)
        } catch (e: Exception) {
            // Fallback: Restart MainActivity jika NavGraph bermasalah
            val intent = requireActivity().intent
            requireActivity().finish()
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}