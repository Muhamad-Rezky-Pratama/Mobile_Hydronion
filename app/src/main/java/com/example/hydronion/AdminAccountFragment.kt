package com.example.hydronion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
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

        // Isi data sesuai keinginan Anda
        binding.tvAdminUsername.text = "Muhamad Rezky Pratama"
        binding.tvAdminRole.text = "ADMIN"
        binding.tvAdminIdUser.text = "ADM-ITN-2526"

        binding.btnLogoutAdmin.setOnClickListener {
            Toast.makeText(requireContext(), "Sesi Admin Berakhir", Toast.LENGTH_SHORT).show()
            // Di sini kamu bisa tambahkan kode pindah ke LoginActivity jika sudah ada
        }
        // Alamat API untuk memenuhi SubCPMK 4
        binding.tvApiInfo.text = "Alamat API Publik: https://openweathermap.org/api"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}