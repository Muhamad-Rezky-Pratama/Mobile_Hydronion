package com.example.hydronion

import SensorApiResponse
import SensorData
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

    private var retryFetchOnce = true   // ⬅️ penting

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonitoringBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchSensorData()
    }

    // =====================================================
    // API FETCH
    // =====================================================
    private fun fetchSensorData() {
        ApiClient.api.getSensorData(action = "fetch")
            .enqueue(object : Callback<SensorApiResponse> {

                override fun onResponse(
                    call: Call<SensorApiResponse>,
                    response: Response<SensorApiResponse>
                ) {
                    if (!response.isSuccessful) {
                        showError("Response tidak valid")
                        return
                    }

                    val body = response.body()

                    // 🔥 CASE 1: logging response → fetch ulang sekali
                    if (body?.sensorData == null && retryFetchOnce) {
                        Log.d("API", "Logging response detected, retrying fetch...")
                        retryFetchOnce = false
                        fetchSensorData()
                        return
                    }

                    // ❌ CASE 2: tetap null → stop
                    if (body?.sensorData == null) {
                        showError("Data sensor tidak tersedia")
                        return
                    }

                    // ✅ CASE 3: data valid
                    updateUI(body.sensorData)
                }

                override fun onFailure(call: Call<SensorApiResponse>, t: Throwable) {
                    showError("Gagal koneksi ke server")
                }
            })
    }

    // =====================================================
    // UI UPDATE
    // =====================================================
    private fun updateUI(data: SensorData) {

        val tds = data.tds ?: 0f
        val suhu = data.suhuAir ?: data.suhu ?: 0f
        val hum = data.kelembapan ?: 0f

        binding.tvtds.text = "${tds.toInt()} ppm"
        binding.tvtemp.text = "${suhu} °C"
        binding.tvhum.text = "${hum.toInt()}%"

        val status = checkOverallStatus(
            tds = tds.toDouble(),
            suhu = suhu.toDouble(),
            kelembapan = hum.toDouble()
        )

        applyOverallStatus(status)
    }

    // =====================================================
    // STATUS LOGIC
    // =====================================================
    private fun checkOverallStatus(
        tds: Double,
        suhu: Double,
        kelembapan: Double
    ): String {
        return when {
            tds < 500 || tds > 1200 || suhu < 15 || suhu > 35 -> "KRITIS"
            tds < 700 || tds > 1000 || suhu < 20 || suhu > 30 -> "PERHATIAN"
            else -> "OPTIMAL"
        }
    }

    private fun applyOverallStatus(status: String) {
        val (color, percent) = when (status) {
            "KRITIS" -> R.color.color_status_critical to 20
            "PERHATIAN" -> R.color.color_status_warning to 50
            else -> R.color.color_status_optimal to 90
        }

        val resolvedColor = ContextCompat.getColor(requireContext(), color)

        binding.gaugeStatusText.text = status
        binding.gaugeStatusText.setTextColor(resolvedColor)

        binding.gaugePercentageText.text = "$percent%"
        binding.gaugePercentageText.setTextColor(resolvedColor)

        binding.gaugeProgressBar.progress = percent
        binding.statusDetailLabel.text = "Kondisi Hidroponik: $status"
        binding.statusDetailLabel.setTextColor(resolvedColor)
    }

    // =====================================================
    // ERROR HANDLER
    // =====================================================
    private fun showError(msg: String) {
        if (!isAdded) return
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    // =====================================================
    // LIFECYCLE
    // =====================================================
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
