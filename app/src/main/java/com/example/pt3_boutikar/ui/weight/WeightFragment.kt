package com.example.pt3_boutikar.ui.weight

import android.content.Context
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.pt3_boutikar.databinding.FragmentWeightBinding
import org.json.JSONObject

class WeightFragment : Fragment() {

    private lateinit var weightViewModel: WeightViewModel
    private var _binding: FragmentWeightBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        weightViewModel =
            ViewModelProvider(this).get(WeightViewModel::class.java)

        _binding = FragmentWeightBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}