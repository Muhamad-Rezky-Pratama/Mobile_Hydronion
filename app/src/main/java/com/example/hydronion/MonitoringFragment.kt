package com.example.hydronion

import ApiClient
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
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.hydronion.databinding.FragmentMonitoringBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MonitoringFragment : Fragment() {

    private var _binding: FragmentMonitoringBinding? = null

    private val binding get() = _binding!!

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

    private fun updateSensorData() {
        loadLiveData()
    }

    private fun loadLiveData() {
        ApiClient.api.getSensorData()
            .enqueue(object : Callback<SensorApiResponse> {
                private var isFirstFetch = true

                override fun onResponse(
                    call: Call<SensorApiResponse>,
                    response: Response<SensorApiResponse>
                ) {
                    val body = response.body()

                    if (isFirstFetch && body?.sensorData == null) {
                        isFirstFetch = false
                        loadLiveData()
                        return
                    }

                    updateUI(body!!.sensorData!!)
                }

                override fun onFailure(call: Call<SensorApiResponse>, t: Throwable) {
                    Log.e("API_FAIL", t.message ?: "error")
                }
            })
    }

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

    private fun updateUI(data: SensorData) {
        if (!isAdded || _binding == null) return

        val tds = data.tds ?: 0f
        val suhu = data.suhu ?: 0f
        val hum = data.kelembapan ?: 0f

        binding.tvtds.text = "$tds ppm"
        binding.tvtemp.text = "$suhu °C"
        binding.tvhum.text = "${hum.toInt()}%"

        val status = checkOverallStatus(
            tds = tds.toDouble(),
            suhu = suhu.toDouble(),
            kelembapan = hum.toDouble()
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
        handler.removeCallbacksAndMessages(null)
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
        handler.removeCallbacksAndMessages(null)
    }

}