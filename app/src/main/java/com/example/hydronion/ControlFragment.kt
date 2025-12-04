package com.example.hydronion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.hydronion.databinding.FragmentControlBinding

class ControlFragment : Fragment() {

    // Pastikan View Binding diaktifkan
    private var _binding: FragmentControlBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentControlBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- Listener untuk 3 Kontrol ---

        // 1. Pompa Air
        binding.switchPompa.setOnCheckedChangeListener { _, isChecked ->
            sendIoTCommand("POMPA AIR", if (isChecked) "ON" else "OFF")
        }

        // 2. Lampu LED
        binding.switchLed.setOnCheckedChangeListener { _, isChecked ->
            sendIoTCommand("LAMPU LED", if (isChecked) "ON" else "OFF")
        }

        // 3. Humidifier
        binding.switchHumidifier.setOnCheckedChangeListener { _, isChecked ->
            sendIoTCommand("HUMIDIFIER", if (isChecked) "ON" else "OFF")
        }
    }

    private fun sendIoTCommand(device: String, command: String) {
        // [Fungsi ini adalah placeholder. Ganti dengan kode MQTT/HTTP Anda.]
        val status = if (command == "ON") "Dinyalakan" else "Dimatikan"
        Toast.makeText(requireContext(), "$device berhasil $status", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}