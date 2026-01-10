package com.example.hydronion

import ApiResponse
import ApiHistoryResponse
import SensorResponse
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.hydronion.databinding.FragmentLogBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class LogFragment : Fragment(R.layout.fragment_log) {

    private var _binding: FragmentLogBinding? = null
    private val binding get() = _binding!!
    private val historyData = mutableListOf<SensorResponse>()
    private var isMeanMode = true
    private var currentFilter = "Harian"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLogBinding.bind(view)

        setupChart()
        loadHistoryFromServer()

        binding.btnHarian.setOnClickListener { currentFilter = "Harian"; refreshUI() }
        binding.btnMingguan.setOnClickListener { currentFilter = "Mingguan"; refreshUI() }
        binding.btnBulanan.setOnClickListener { currentFilter = "Bulanan"; refreshUI() }

        binding.btnViewMean.setOnClickListener { isMeanMode = true; refreshUI() }
        binding.btnViewMinMax.setOnClickListener { isMeanMode = false; refreshUI() }
    }

    private fun loadHistoryFromServer() {
        ApiClient.api.getSensorHistory().enqueue(object : Callback<ApiHistoryResponse> {
            override fun onResponse(call: Call<ApiHistoryResponse>, response: Response<ApiHistoryResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    historyData.clear()
                    historyData.addAll(response.body()!!.data)
                    refreshUI()
                }
            }
            override fun onFailure(call: Call<ApiHistoryResponse>, t: Throwable) {
                Log.e("API_LOG", "Gagal load history: ${t.message}")
            }
        })
    }

    private fun refreshUI() {
        if (historyData.isEmpty()) return

        val filteredData = processFilteredData(historyData, currentFilter)

        // 1. UPDATE MULTI-GRAFIK (TDS, Suhu, Kelembaban)
        updateMultiChart(filteredData)

        // 2. UPDATE STATISTIK (5 Sensor Lengkap)
        updateStatistics(filteredData)
    }

    private fun processFilteredData(rawData: List<SensorResponse>, filter: String): List<SensorResponse> {
        return when (filter) {
            "Harian" -> rawData.takeLast(24)
            "Mingguan" -> groupDataByWeek(rawData)
            "Bulanan" -> groupDataByMonth(rawData)
            else -> rawData
        }
    }

    private fun groupDataByWeek(data: List<SensorResponse>): List<SensorResponse> {
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return data.groupBy {
            val date = try { sdf.parse(it.timestamp ?: "") } catch (e: Exception) { Date() }
            calendar.time = date ?: Date()
            "W${calendar.get(Calendar.WEEK_OF_MONTH)}"
        }.map { (weekLabel, weekGroup) ->
            SensorResponse(
                tds = weekGroup.map { it.tds }.average().toInt(),
                suhu = weekGroup.map { it.suhu }.average().toFloat(),
                hum = weekGroup.map { it.hum }.average().toInt(),
                kelembapan = weekGroup.map { it.kelembapan ?: it.hum.toFloat() }.average().toFloat(),
                pH = weekGroup.map { it.pH }.average().toFloat(),
                timestamp = weekLabel
            )
        }
    }

    private fun groupDataByMonth(data: List<SensorResponse>): List<SensorResponse> {
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return data.groupBy {
            val date = try { sdf.parse(it.timestamp ?: "") } catch (e: Exception) { Date() }
            calendar.time = date ?: Date()
            calendar.get(Calendar.MONTH).toString()
        }.map { (monthLabel, monthGroup) ->
            SensorResponse(
                tds = monthGroup.map { it.tds }.average().toInt(),
                suhu = monthGroup.map { it.suhu }.average().toFloat(),
                hum = monthGroup.map { it.hum }.average().toInt(),
                kelembapan = monthGroup.map { monthGroup.map { it.kelembapan ?: it.hum.toFloat() }.average().toFloat() }.first(),
                pH = monthGroup.map { it.pH }.average().toFloat(),
                timestamp = monthLabel
            )
        }
    }

    private fun updateMultiChart(data: List<SensorResponse>) {
        val tdsEntries = mutableListOf<Entry>()
        val suhuEntries = mutableListOf<Entry>()
        val humEntries = mutableListOf<Entry>()


        data.mapIndexed { i, d ->
            // Perbaikan Logika Mean: Menghitung rata-rata kumulatif dari list data yang difilter
            val tdsVal = if (isMeanMode) data.take(i + 1).map { it.tds }.average().toFloat() else d.tds.toFloat()
            val suhuVal = if (isMeanMode) data.take(i + 1).map { it.suhu }.average().toFloat() else d.suhu
            val humVal = if (isMeanMode) data.take(i + 1).map { it.kelembapan ?: it.hum.toFloat() }.average().toFloat() else (d.kelembapan ?: d.hum.toFloat())

            tdsEntries.add(Entry(i.toFloat(), tdsVal))
            suhuEntries.add(Entry(i.toFloat(), suhuVal))
            humEntries.add(Entry(i.toFloat(), humVal))
        }

        val tdsSet = createDataSet(tdsEntries, "TDS", Color.rgb(155, 89, 182))
        val suhuSet = createDataSet(suhuEntries, "Suhu", Color.RED)
        val humSet = createDataSet(humEntries, "Lembap", Color.rgb(26, 188, 156))

        binding.lineChart.data = LineData(tdsSet, suhuSet, humSet)
        binding.lineChart.invalidate()
    }

    private fun createDataSet(entries: List<Entry>, label: String, color: Int): LineDataSet {
        return LineDataSet(entries, label).apply {
            this.color = color
            setCircleColor(color)
            lineWidth = 2f
            setDrawCircles(true)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawValues(false)
        }
    }

    private fun updateStatistics(data: List<SensorResponse>) {
        val lastData = data.last()
        binding.apply {

            // TDS (PPM)
            tvTdsNow.text = "Live: ${lastData.tds}"
            tvTdsMin.text = "Min: ${data.minOf { it.tds }}"
            tvTdsMax.text = "Max: ${data.maxOf { it.tds }}"
            tvTdsMean.text = "Mean: ${data.map { it.tds }.average().toInt()}"

            // Suhu Air
            tvTempNow.text = "Live: ${lastData.suhu}"
            tvTempMin.text = "Min: ${data.minOf { it.suhu }}"
            tvTempMax.text = "Max: ${data.maxOf { it.suhu }}"
            tvTempMean.text = "Mean: ${data.map { it.suhu.toDouble() }.average().toInt()}"

            // Kelembaban
            val h = (lastData.kelembapan ?: lastData.hum.toFloat())
            tvHumNow.text = "Live: ${h.toInt()}%"
            tvHumMin.text = "Min: ${data.minOf { it.kelembapan ?: it.hum.toFloat() }.toInt()}%"
            tvHumMax.text = "Max: ${data.maxOf { it.kelembapan ?: it.hum.toFloat() }.toInt()}%"
            tvHumMean.text = "Mean: ${data.map { (it.kelembapan ?: it.hum.toFloat()).toDouble() }.average().toInt()}%"

        }
    }

    private fun setupChart() {
        binding.lineChart.apply {
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            axisRight.isEnabled = false
            description.isEnabled = false
            legend.isEnabled = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
