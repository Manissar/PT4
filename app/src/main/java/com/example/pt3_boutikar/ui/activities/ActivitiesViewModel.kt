package com.example.pt3_boutikar.ui.feeling

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ActivitiesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Activities Fragment"
    }
    val text: LiveData<String> = _text
}