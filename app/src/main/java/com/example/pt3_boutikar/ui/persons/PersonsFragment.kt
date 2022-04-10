package com.example.pt3_boutikar.ui.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.pt3_boutikar.databinding.FragmentPersonsBinding
import com.example.pt3_boutikar.ui.feeling.PersonsViewModel

class PersonsFragment : Fragment() {

    private lateinit var personsViewModel: PersonsViewModel
    private var _binding: FragmentPersonsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        personsViewModel =
            ViewModelProvider(this).get(PersonsViewModel::class.java)

        _binding = FragmentPersonsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}