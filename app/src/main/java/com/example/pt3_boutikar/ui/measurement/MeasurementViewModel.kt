package com.example.pt3_boutikar.ui.measurement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MeasurementViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is measurement Fragment"
    }
    val text: LiveData<String> = _text
}