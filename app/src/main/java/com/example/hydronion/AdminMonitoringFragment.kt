package com.example.hydronion

import ApiResponse
import SensorResponse
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

    // Handler untuk refresh data otomatis
    private val handler = Handler(Looper.getMainLooper())
    private val refreshRunnable = object : Runnable {
        override fun run() {
            fetchAverageData()
            handler.postDelayed(this, 3000) // Refresh tiap 3 detik
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdminMonitoringBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mulai ambil data
        fetchAverageData()

        binding.btnSendSuggestion.setOnClickListener {
            val text = binding.etAdminSuggestion.text.toString()
            if (text.isNotEmpty()) {
                Toast.makeText(requireContext(), "Instruksi terkirim ke Client!", Toast.LENGTH_SHORT).show()
                binding.etAdminSuggestion.text.clear()
            } else {
                Toast.makeText(requireContext(), "Saran tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchAverageData() {
        ApiClient.api.getSensorData().enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!.data
                    updateUI(data)
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.e("ADMIN_MONITOR", "Gagal load data: ${t.message}")
            }
        })
    }

    private fun updateUI(data: SensorResponse) {
        binding.apply {
            // Mengambil nilai rata-rata dari field avg_ di SensorResponse
            tvAvgTds.text = "Rata-rata: ${data.avg_tds ?: data.tds} ppm"
            tvAvgWater.text = "Rata-rata: ${data.avg_wl ?: data.waterlevel} cm"
            tvAvgPh.text = "Rata-rata: ${String.format("%.1f", data.pH)}"
            tvAvgTemp.text = "Rata-rata: ${(data.avg_suhu ?: data.suhu).toInt()}°C"
            tvAvgHum.text = "Rata-rata: ${data.avg_hum ?: data.hum}%"
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