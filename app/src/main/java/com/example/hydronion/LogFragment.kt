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
import com.example.hydronion.databinding.FragmentLogBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LogFragment : Fragment() {

    private var _binding: FragmentLogBinding? = null
    private val binding get() = _binding!!

    // Handler untuk update otomatis setiap 3 detik
    private val handler = Handler(Looper.getMainLooper())
    private val updateTask = object : Runnable {
        override fun run() {
            fetchSensorAverages()
            handler.postDelayed(this, 3000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Panggilan pertama saat view dibuat
        fetchSensorAverages()
    }

    private fun fetchSensorAverages() {
        ApiClient.api.getSensorData().enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!.data
                    updateAverageUI(data)
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.e("LOG_API_ERROR", "Gagal load data: ${t.message}")
            }
        })
    }

    private fun updateAverageUI(data: SensorResponse) {
        binding.apply {
            // Jika data rata-rata (avg_) dari API null, maka akan menampilkan data live
            tvAvgTds.text = "${data.avg_tds ?: data.tds} ppm"
            tvAvgWaterLevel.text = "${data.avg_wl ?: data.waterlevel} cm"
            tvAvgPh.text = String.format("%.1f", data.pH)
            tvAvgTemp.text = "${(data.avg_suhu ?: data.suhu).toInt()}°C"
            tvAvgHum.text = "${data.avg_hum ?: data.hum}%"
        }
    }

    // Menjalankan auto-update saat fragment aktif
    override fun onResume() {
        super.onResume()
        handler.post(updateTask)
    }

    // Memberhentikan update saat fragment tidak terlihat (hemat baterai)
    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updateTask)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}