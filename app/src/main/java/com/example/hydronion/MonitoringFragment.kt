package com.example.hydronion

import SensorApiResponse
import SensorData
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

import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class MonitoringFragment : Fragment() {

    private var _binding: FragmentMonitoringBinding? = null
    private val binding get() = _binding!!

    private var isLiveMode = true

    private val WEATHER_API_KEY = "6413722b7606fafed462799567af2a9a"

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

        fetchPublicWeatherData("Bandung")

        binding.gaugeStatusText.setOnClickListener {
            Toast.makeText(requireContext(), "Menampilkan Detail Status...", Toast.LENGTH_SHORT).show()
        }
    }
    private fun fetchPublicWeatherData(city: String) {
        val url = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$WEATHER_API_KEY&units=metric"

        val queue = Volley.newRequestQueue(requireContext())
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val main = jsonResponse.getJSONObject("main")
                    val weatherArray = jsonResponse.getJSONArray("weather")
                    val description = weatherArray.getJSONObject(0).getString("description")
                    val temp = main.getDouble("temp")
                    val cityName = jsonResponse.getString("name")

                    // Update UI Card Cuaca yang baru di XML [cite: 31, 32]
                    binding.tvLocation.text = "Lokasi: $cityName"
                    binding.tvWeatherDesc.text = "Cuaca Luar: ${description.uppercase()}"
                    binding.tvExternalTemp.text = "${temp.toInt()}°C"
                } catch (e: Exception) {
                    Log.e("WEATHER_ERROR", e.message ?: "Parsing error")
                }
            },
            { error ->
                binding.tvLocation.text = "Gagal memuat cuaca"
            }
        )
        queue.add(stringRequest)
    }
    private fun generateSimulatedSensorData(): SensorResponse {
        return SensorResponse(
            tds = (300..1200).random(),
            suhu = (20..34).random().toFloat(),
            pH = listOf(5.8f, 6.0f, 6.2f, 6.5f, 6.8f).random(),
            hum = (40..90).random(),
            // Tambahkan nilai ini agar tidak error:
            avg_tds = 800,
            avg_suhu = 25f,
            avg_hum = 70,
            avg_wl = 15f
        )
    }

    private fun updateSensorData() {
        if (isLiveMode) {
            loadLiveData()
        } else {
        //    loadDemoData()
        }
    }
    //private fun loadDemoData() {
    //    val data = SensorData(
    //        tds = (600..1200).random().toFloat(),
    //        suhu_air = (10..25).random().toFloat(),
    //        suhu = (22..30).random().toFloat(),
    //        //pH = listOf(5.8f, 6.2f, 6.5f, 6.8f).random(),
    //        kelembapan = (60..90).random().toFloat(),
            // Tambahkan nilai ini:
    //        avg_tds = 900,
    //        avg_suhu = 24f,
    //        avg_hum = 75
    //   )

    //    updateUI(data)
    //}
    private fun loadLiveData() {
        ApiClient.api.getSensorData()
            .enqueue(object : Callback<SensorApiResponse> {

                override fun onResponse(
                    call: Call<SensorApiResponse>,
                    response: Response<SensorApiResponse>
                ) {
                    if (!response.isSuccessful) {
                        Toast.makeText(requireContext(), "Response tidak valid", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val body = response.body()
                    val data = body?.sensor_data

                    if (data == null) {
                        Toast.makeText(requireContext(), "Data sensor belum tersedia", Toast.LENGTH_SHORT).show()
                        return
                    }

                    updateUI(data)
                }

                override fun onFailure(call: Call<SensorApiResponse>, t: Throwable) {
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

    private fun updateUI(data: SensorData) {

        fun Number?.safeDouble(): Double = this?.toDouble() ?: 0.0
        fun Number?.safeInt(): Int = this?.toInt() ?: 0

        if (data.tds == null && data.avg_tds == null) {
            binding.gaugeStatusText.text = "MENUNGGU DATA"
            binding.gaugeProgressBar.progress = 0
            return
        }

        // DETAIL SENSOR
        binding.tvtds.text = getString(R.string.tds_format, data.tds.safeInt())
        binding.tvph.text = getString(R.string.ph_format, data.pH.safeDouble())
        binding.tvtemp.text = getString(R.string.temp_format, data.suhu.safeDouble())

        // RATA-RATA
        binding.avgTdsEcValue.text =
            getString(R.string.tds_suhu_format,
                data.avg_tds.safeInt(),
                data.avg_suhu.safeDouble()
            )

        binding.avgHumTempValue.text =
            getString(R.string.hum_temp_format,
                data.avg_hum.safeInt(),
                data.avg_suhu.safeDouble()
            )

        val status = checkOverallStatus(
            (data.avg_tds ?: data.tds).safeDouble(),
            data.pH.safeDouble(),
            (data.avg_suhu ?: data.suhu).safeDouble(),
            (data.avg_hum ?: data.hum).safeDouble()
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