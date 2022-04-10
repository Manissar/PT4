package com.example.pt3_boutikar.ui.sleep

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.pt3_boutikar.MainActivity
import com.example.pt3_boutikar.databinding.FragmentSleepBinding
import com.example.pt3_boutikar.ui.feeling.SleepViewModel
import kotlinx.coroutines.MainScope

class SleepFragment : Fragment() {

    private lateinit var sleepViewModel: SleepViewModel
    private var _binding: FragmentSleepBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sleepViewModel =
            ViewModelProvider(this).get(SleepViewModel::class.java)

        _binding = FragmentSleepBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSleep
        sleepViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}