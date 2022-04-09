package com.example.pt3_boutikar.ui.feeling

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.pt3_boutikar.databinding.FragmentFeelingBinding

class FeelingFragment : Fragment() {

    private lateinit var feelingViewModel: FeelingViewModel
    private var _binding: FragmentFeelingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        feelingViewModel =
            ViewModelProvider(this).get(FeelingViewModel::class.java)

        _binding = FragmentFeelingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textFeeling
        feelingViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}