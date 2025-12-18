package com.example.hydronion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

        // Set data profil secara manual (Hardcoded untuk kebutuhan UTS)
        // Nantinya data ini bisa diambil dari hasil Login/Database
        binding.tvUsername.text = "ASEP Hydronion"
        binding.tvHidroponikId.text = "ITN-HIDRO-2025-01"
        binding.tvRole.text = "CLIENT "

        // Menampilkan Alamat API sesuai syarat soal nomor 3
        binding.tvApiUrl.text = "Sumber Data Publik: \nhttps://api.openweathermap.org/data/2.5/weather"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}