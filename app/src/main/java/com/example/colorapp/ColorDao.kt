package com.example.colorapp

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ColorDao {
    @Query ("SELECT * FROM colors_table ORDER BY id DESC")
    fun getAllColors(): LiveData<List<ColorItem>>

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertColor (color : ColorItem)

    @Delete
    suspend fun deleteColor (color : ColorItem)
}