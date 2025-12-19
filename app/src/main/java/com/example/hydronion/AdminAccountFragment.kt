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
import com.example.hydronion.databinding.FragmentAdminAccountBinding

class AdminAccountFragment : Fragment() {

    private var _binding: FragmentAdminAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireContext().getSharedPreferences("SessionHydronion", Context.MODE_PRIVATE)
        binding.tvAdminUsername.text = sharedPref.getString("username", "Admin")
        binding.tvAdminRole.text = sharedPref.getString("role", "ADMIN")?.uppercase()
        binding.tvAdminIdUser.text = sharedPref.getString("userId", "ADM-ITN-000")

        binding.btnLogoutAdmin.setOnClickListener {
            logoutUser()
        }
    }

    private fun logoutUser() {
        // 1. Hapus Sesi di SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("SessionHydronion", Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()

        Toast.makeText(requireContext(), "Logout Berhasil", Toast.LENGTH_SHORT).show()

        // 2. Navigasi ke Login dengan NavOptions untuk membersihkan tumpukan fragment (backstack)
        // Ganti R.id.loginFragment dengan ID yang sesuai di nav_graph.xml Anda
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.adminAccountFragment, true) // Menghapus fragment saat ini dari stack
            .build()

        try {
            // Menggunakan ID tujuan langsung tanpa blok lambda untuk menghindari error overload
            findNavController().navigate(R.id.loginFragment, null, navOptions)
        } catch (e: Exception) {
            // Jika NavGraph gagal, gunakan cara fallback: restart MainActivity
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