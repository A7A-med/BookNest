package com.example.colorapp

import androidx.lifecycle.LiveData

class ColorRepository (private val colorDao: ColorDao){

    val allColors : LiveData<List<ColorItem>> = colorDao.getAllColors()

    suspend fun insert (color : ColorItem){
        colorDao.insertColor(color)
    }

    suspend fun delete (color : ColorItem){
        colorDao.deleteColor(color)
    }

}