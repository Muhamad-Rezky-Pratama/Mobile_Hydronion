package com.example.hydronion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.hydronion.databinding.FragmentLogBinding

class LogFragment : Fragment() {

    private var _binding: FragmentLogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- LOGIKA GRAFIK DITEMPATKAN DI SINI ---
        // Saat Anda siap menginstal library grafik, Anda akan menginisialisasinya di sini.
        // Contoh: setupChart(binding.lineChart)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}