package com.example.hydronion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.hydronion.databinding.FragmentAdminMonitoringBinding

class AdminMonitoringFragment : Fragment() {

    private var _binding: FragmentAdminMonitoringBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdminMonitoringBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Simulasi data historis (Misal data dari jam 10, 12, 14)
        calculateAllAverages()

        binding.btnSendSuggestion.setOnClickListener {
            val text = binding.etAdminSuggestion.text.toString()
            if (text.isNotEmpty()) {
                Toast.makeText(requireContext(), "Instruksi terkirim ke Client!", Toast.LENGTH_SHORT).show()
                binding.etAdminSuggestion.text.clear()
            }
        }
    }

    private fun calculateAllAverages() {
        // Data simulasi untuk memenuhi perhitungan matematika
        val tdsList = listOf(820, 850, 880)
        val waterList = listOf(15.0f, 15.5f, 16.0f)
        val phList = listOf(6.0f, 6.2f, 6.1f)
        val tempList = listOf(23.0f, 24.0f, 23.5f)
        val humList = listOf(70, 75, 80)

        // Update UI dengan hasil rata-rata (SubCPMK 3)
        binding.tvAvgTds.text = "Rata-rata: ${tdsList.average().toInt()} ppm"
        binding.tvAvgWater.text = "Rata-rata: ${String.format("%.1f", waterList.average())} cm"
        binding.tvAvgPh.text = "Rata-rata: ${String.format("%.1f", phList.average())}"
        binding.tvAvgTemp.text = "Rata-rata: ${String.format("%.1f", tempList.average())}°C"
        binding.tvAvgHum.text = "Rata-rata: ${humList.average().toInt()}%"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}