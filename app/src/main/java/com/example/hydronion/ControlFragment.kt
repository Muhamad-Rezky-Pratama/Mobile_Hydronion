package com.example.hydronion

import ControlRequest
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

        val request = ControlRequest(
            device = device,
            state = command
        )

        ApiClient.api.sendControl(request)
            .enqueue(object : retrofit2.Callback<Void> {

                override fun onResponse(
                    call: retrofit2.Call<Void>,
                    response: retrofit2.Response<Void>
                ) {
                    if (response.isSuccessful) {
                        val status = if (command == "ON") "Dinyalakan" else "Dimatikan"
                        Toast.makeText(
                            requireContext(),
                            "$device berhasil $status",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Gagal mengirim perintah",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        "Koneksi ke server gagal",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}