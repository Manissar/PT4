package com.example.pt3_boutikar.ui.measurement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.pt3_boutikar.databinding.FragmentFeelingBinding
import com.example.pt3_boutikar.databinding.FragmentMeasurementBinding

class MeasurementFragment : Fragment() {

    private lateinit var measurementViewModel: MeasurementViewModel
    private var _binding: FragmentMeasurementBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        measurementViewModel =
            ViewModelProvider(this).get(MeasurementViewModel::class.java)

        _binding = FragmentMeasurementBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textMeasurement
        measurementViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}