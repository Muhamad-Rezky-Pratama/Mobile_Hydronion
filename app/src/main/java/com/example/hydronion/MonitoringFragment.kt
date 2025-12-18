package com.example.hydronion

import ApiResponse
import SensorResponse
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.hydronion.databinding.FragmentMonitoringBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MonitoringFragment : Fragment() {

    private var _binding: FragmentMonitoringBinding? = null
    private val binding get() = _binding!!

    private var isLiveMode = true

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

    private fun generateSimulatedSensorData(): SensorResponse {
        return SensorResponse(
            tds = (300..1200).random(),
            waterlevel = (5..25).random().toFloat(),
            suhu = (20..34).random().toFloat(),
            pH = listOf(5.8f, 6.0f, 6.2f, 6.5f, 6.8f).random(),
            hum = (40..90).random()
        )
    }

    private fun updateSensorData() {
        if (isLiveMode) {
            loadLiveData()
        } else {
            loadDemoData()
        }
    }
    private fun loadDemoData() {
        val data = SensorResponse(
            tds = (600..1200).random(),
            waterlevel = (10..25).random().toFloat(),
            suhu = (22..30).random().toFloat(),
            pH = listOf(5.8f, 6.2f, 6.5f, 6.8f).random(),
            hum = (60..90).random()
        )

        updateUI(data)
    }
    private fun loadLiveData() {
        ApiClient.api.getSensorData()
            .enqueue(object : Callback<ApiResponse> {

                override fun onResponse(
                    call: Call<ApiResponse>,
                    response: Response<ApiResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        updateUI(response.body()!!.data)
                    } else {
                        Toast.makeText(requireContext(), "Response tidak valid", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Gagal koneksi API", Toast.LENGTH_SHORT).show()
                    Log.e("API_ERROR", t.message ?: "unknown error")
                }
            })
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
    private fun updateUI(data: SensorResponse) {

        // Detail
        binding.tvtds.text = getString(R.string.tds_format, data.tds)
        binding.tvwaterlevel.text = getString(R.string.water_level_format, data.waterlevel)
        binding.tvph.text = getString(R.string.ph_format, data.pH)
        binding.tvtemp.text = getString(R.string.temp_format, data.suhu)
        binding.tvhum.text = getString(R.string.hum_format, data.hum)

        // Summary
        binding.avgTdsEcValue.text =
            getString(R.string.tds_waterlevel_format, data.tds, data.waterlevel)

        binding.avgPhValue.text = getString(R.string.ph_format, data.pH)

        binding.avgHumTempValue.text =
            getString(R.string.hum_temp_format, data.hum, data.suhu)

        // Status
        val status = checkOverallStatus(
            data.tds.toDouble(),
            data.pH.toDouble(),
            data.suhu.toDouble(),
            data.hum.toDouble()
        )

        applyOverallStatusText(status)
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
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())

    private val updater = object : Runnable {
        override fun run() {
            updateSensorData()
            handler.postDelayed(this, 3000) // tiap 3 detik
        }
    }

    override fun onResume() {
        super.onResume()
        handler.post(updater)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updater)
    }

}