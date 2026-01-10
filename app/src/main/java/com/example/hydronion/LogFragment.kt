package com.example.hydronion

import ApiClient
import SensorData
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.hydronion.databinding.FragmentLogBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class LogFragment : Fragment(R.layout.fragment_log) {

    private var _binding: FragmentLogBinding? = null
    private val binding get() = _binding!!
    private val historyData = mutableListOf<SensorData>()
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
        ApiClient.api.getSensorHistory()
            .enqueue(object : Callback<List<SensorData>> {

                override fun onResponse(
                    call: Call<List<SensorData>>,
                    response: Response<List<SensorData>>
                ) {
                    if (!response.isSuccessful || response.body().isNullOrEmpty()) {
                        Log.e("API_LOG", "History kosong")
                        return
                    }

                    historyData.clear()
                    historyData.addAll(response.body()!!)
                    refreshUI()
                }

                override fun onFailure(call: Call<List<SensorData>>, t: Throwable) {
                    Log.e("API_LOG", "Gagal load history", t)
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

    private fun processFilteredData(
        rawData: List<SensorData>,
        filter: String
    ): List<SensorData>
    {
        return when (filter) {
            "Harian" -> rawData.takeLast(24)
            "Mingguan" -> groupDataByWeek(rawData)
            "Bulanan" -> groupDataByMonth(rawData)
            else -> rawData
        }
    }

    private fun groupDataByWeek(data: List<SensorData>): List<SensorData> {
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return data.groupBy {
            val date = try { sdf.parse(it.timestamp ?: "") } catch (e: Exception) { Date() }
            calendar.time = date ?: Date()
            "W${calendar.get(Calendar.WEEK_OF_MONTH)}"
        }.map { (weekLabel, group) ->
            SensorData(
                tds = group.mapNotNull { it.tds }.average().toFloat(),
                suhuAir = group.mapNotNull { it.suhuAir }.average().toFloat(),
                suhu = group.mapNotNull { it.suhu }.average().toFloat(),
                kelembapan = group.mapNotNull { it.kelembapan }.average().toFloat(),
                timestamp = weekLabel
            )
        }
    }

    private fun groupDataByMonth(data: List<SensorData>): List<SensorData> {
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return data.groupBy {
            val date = try { sdf.parse(it.timestamp ?: "") } catch (e: Exception) { Date() }
            calendar.time = date ?: Date()
            calendar.get(Calendar.MONTH).toString()
        }.map { (monthLabel, group) ->
            SensorData(
                tds = group.mapNotNull { it.tds }.average().toFloat(),
                suhuAir = group.mapNotNull { it.suhuAir }.average().toFloat(),
                suhu = group.mapNotNull { it.suhu }.average().toFloat(),
                kelembapan = group.mapNotNull { it.kelembapan }.average().toFloat(),
                timestamp = monthLabel
            )
        }
    }

    private fun updateMultiChart(data: List<SensorData>) {
        val tdsEntries = mutableListOf<Entry>()
        val suhuEntries = mutableListOf<Entry>()
        val humEntries = mutableListOf<Entry>()

        data.forEachIndexed { i, d ->
            tdsEntries.add(Entry(i.toFloat(), d.tds ?: 0f))
            suhuEntries.add(Entry(i.toFloat(), d.suhu ?: 0f))
            humEntries.add(Entry(i.toFloat(), d.kelembapan ?: 0f))
        }

        binding.lineChart.data = LineData(
            createDataSet(tdsEntries, "TDS", Color.BLUE),
            createDataSet(suhuEntries, "Suhu", Color.RED),
            createDataSet(humEntries, "Kelembapan", Color.GREEN)
        )
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

    private fun updateStatistics(data: List<SensorData>) {
        if (data.isEmpty()) return
        val last = data.last()

        binding.apply {
            tvTdsNow.text = "Live: ${last.tds ?: 0}"
            tvTdsMin.text = "Min: ${data.minOf { it.tds ?: 0f }}"
            tvTdsMax.text = "Max: ${data.maxOf { it.tds ?: 0f }}"
            tvTdsMean.text = "Mean: ${data.mapNotNull { it.tds }.average().toInt()}"

            tvTempNow.text = "Live: ${last.suhu ?: 0f}"
            tvTempMin.text = "Min: ${data.minOf { it.suhu ?: 0f }}"
            tvTempMax.text = "Max: ${data.maxOf { it.suhu ?: 0f }}"
            tvTempMean.text = "Mean: ${data.mapNotNull { it.suhu }.average().toInt()}"

            tvHumNow.text = "Live: ${last.kelembapan ?: 0f}%"
            tvHumMin.text = "Min: ${data.minOf { it.kelembapan ?: 0f }.toInt()}%"
            tvHumMax.text = "Max: ${data.maxOf { it.kelembapan ?: 0f }.toInt()}%"
            tvHumMean.text = "Mean: ${data.mapNotNull { it.kelembapan }.average().toInt()}%"
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
