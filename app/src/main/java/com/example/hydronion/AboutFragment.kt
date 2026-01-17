package com.example.hydronion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.hydronion.databinding.ActivityAboutBinding

class AboutFragment : Fragment() {

    private var _binding: ActivityAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Di sini kamu bisa menambahkan logika klik jika ingin
        // Contoh: Klik alamat API untuk membuka browser
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}