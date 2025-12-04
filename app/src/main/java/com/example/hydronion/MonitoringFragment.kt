package com.example.hydronion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.hydronion.databinding.FragmentMonitoringBinding

class MonitoringFragment : Fragment() {

    private var _binding: FragmentMonitoringBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonitoringBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateSensorData()

        binding.gaugeStatusText.setOnClickListener {
            Toast.makeText(requireContext(), "Menampilkan Detail Status...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateSensorData() {
        // --- DATA SENSOR SIMULASI ---
        val currentTds = 850.0
        val currentWaterLevel = 15.5 // Ganti dari EC ke Tinggi Air (cm)
        val currentPh = 6.0
        val currentAirTemp = 23.5
        val currentHumidity = 75.0

        // 1. Tampilkan Nilai Sensor ke 5 CardView Individu (Detail Sensor)
        binding.tdsValue.text = "${currentTds.toInt()} ppm"
        // Menggunakan ID binding baru: waterLevelValue
        binding.waterLevelValue.text = "$currentWaterLevel cm"
        binding.phValue.text = "$currentPh"
        binding.airTempValue.text = "$currentAirTemp°C"
        binding.humValue.text = "${currentHumidity.toInt()}%"

        // 2. Tampilkan Nilai Rata-Rata di Gauge Card (Summary di Kanan)
        binding.avgTdsEcValue.text = "${currentTds.toInt()} ppm / $currentWaterLevel cm" // Ganti label EC dengan cm
        binding.avgPhValue.text = "$currentPh"
        binding.avgHumTempValue.text = "${currentHumidity.toInt()}% / $currentAirTemp°C"

        // 3. Tentukan Status dan Update Teks Gauge (Tampilan Utama)
        val overallStatus = checkOverallStatus(currentTds, currentPh, currentAirTemp, currentHumidity)
        applyOverallStatusText(overallStatus)
    }

    private fun checkOverallStatus(tds: Double, ph: Double, airTemp: Double, humidity: Double): String {
        // Logika status hanya menggunakan TDS dan PH
        if (ph < 5.5 || ph > 7.0 || tds < 600.0 || tds > 1200.0) {
            return "KRITIS"
        } else if (ph < 5.8 || ph > 6.5 || tds < 800.0 || tds > 1000.0) {
            return "PERHATIAN"
        }
        return "OPTIMAL"
    }

    private fun applyOverallStatusText(status: String) {
        val textColor: Int
        val percentage: Int

        when (status) {
            "KRITIS" -> {
                textColor = ContextCompat.getColor(requireContext(), R.color.color_status_critical)
                percentage = 20
            }
            "PERHATIAN" -> {
                textColor = ContextCompat.getColor(requireContext(), R.color.color_status_warning)
                percentage = 50
            }
            else -> {
                textColor = ContextCompat.getColor(requireContext(), R.color.color_status_optimal)
                percentage = 90
            }
        }

        binding.gaugeStatusText.text = status
        binding.gaugeStatusText.setTextColor(textColor)

        binding.gaugePercentageText.text = "$percentage%"
        binding.gaugePercentageText.setTextColor(textColor)
        binding.gaugeProgressBar.progress = percentage

        binding.statusDetailLabel.text = "Kondisi Hidroponik: $status"
        binding.statusDetailLabel.setTextColor(textColor)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}