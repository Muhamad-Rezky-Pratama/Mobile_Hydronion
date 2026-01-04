package com.example.hydronion

import ControlRequest
import ControlResponse
import ControlStateRequest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.hydronion.databinding.FragmentControlBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        // Pompa Air
        binding.switchPompa.setOnCheckedChangeListener { _, isChecked ->
            sendIoTCommand("pompa", isChecked)
        }

        // Lampu LED
        binding.switchLed.setOnCheckedChangeListener { _, isChecked ->
            sendIoTCommand("lampu", isChecked)
        }
    }


    private fun sendIoTCommand(device: String, isOn: Boolean) {
        val body = mapOf("state" to if (isOn) 1 else 0)

        ApiClient.api.controlDevice(device.lowercase(), body)
            .enqueue(object : Callback<ControlResponse> {

                override fun onResponse(
                    call: Call<ControlResponse>,
                    response: Response<ControlResponse>
                ) {
                    if (!isAdded) return

                    if (response.isSuccessful && response.body()?.status == "success") {
                        Toast.makeText(
                            requireContext(),
                            "${device.uppercase()} berhasil ${if (isOn) "DINYALAKAN" else "DIMATIKAN"}",
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

                override fun onFailure(
                    call: Call<ControlResponse>,
                    t: Throwable
                ) {
                    if (!isAdded) return
                    Toast.makeText(
                        requireContext(),
                        "Gagal koneksi ke server",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("CONTROL_ERROR", t.message ?: "unknown error")
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}