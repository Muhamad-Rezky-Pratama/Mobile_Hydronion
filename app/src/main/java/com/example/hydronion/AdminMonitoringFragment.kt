package com.example.hydronion

import SensorApiResponse
import SensorData
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.hydronion.databinding.FragmentAdminMonitoringBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminMonitoringFragment : Fragment() {

    private var _binding: FragmentAdminMonitoringBinding? = null
    private val binding get() = _binding!!

    private val handler = Handler(Looper.getMainLooper())
    private val refreshRunnable = object : Runnable {
        override fun run() {
            fetchSensorData()
            handler.postDelayed(this, 3000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminMonitoringBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchSensorData()

        binding.btnSendSuggestion.setOnClickListener {
            val text = binding.etAdminSuggestion.text.toString()
            if (text.isNotBlank()) {
                Toast.makeText(requireContext(), "Instruksi terkirim", Toast.LENGTH_SHORT).show()
                binding.etAdminSuggestion.text.clear()
            } else {
                Toast.makeText(requireContext(), "Saran tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchSensorData() {
        ApiClient.api.getSensorData()
            .enqueue(object : Callback<SensorApiResponse> {

                override fun onResponse(
                    call: Call<SensorApiResponse>,
                    response: Response<SensorApiResponse>
                ) {
                    val body = response.body()

                    if (!response.isSuccessful || body?.sensor_data == null) {
                        Log.w("ADMIN_MONITOR", "Data sensor kosong")
                        return
                    }

                    updateUI(body.sensor_data)
                }

                override fun onFailure(call: Call<SensorApiResponse>, t: Throwable) {
                    Log.e("ADMIN_MONITOR", "API Error: ${t.message}")
                }
            })
    }

    private fun updateUI(data: SensorData) {

        fun Number?.safe(): String = this?.toString() ?: "-"

        binding.apply {
            tvAvgTds.text = "TDS: ${data.avg_tds ?: data.tds ?: "-"} ppm"
            tvAvgWater.text = "Suhu Air: ${data.avg_wl ?: data.avg_wl ?: "-"} °C"
            tvAvgTemp.text = "Suhu: ${data.avg_suhu ?: data.suhu ?: "-"} °C"
            tvAvgHum.text = "Kelembapan: ${data.avg_hum ?: data.hum ?: "-"} %"
        }
    }

    override fun onResume() {
        super.onResume()
        handler.post(refreshRunnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(refreshRunnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
