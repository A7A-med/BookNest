package com.example.colorapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ColorViewModel (application: Application) : AndroidViewModel(application) {

    private val repository : ColorRepository
    val allColors : LiveData<List<ColorItem>>

    init{
        val colorDao = ColorDatabase.getDatabase(application).ColorDao()
        repository = ColorRepository(colorDao)
        allColors = repository.allColors
    }

    fun addColor(color : ColorItem) = viewModelScope.launch(Dispatchers.IO){
        repository.insert(color)
    }

    fun deleteColor(color : ColorItem) = viewModelScope.launch(Dispatchers.IO){
        repository.delete(color)
    }
}