package com.example.colorapp

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity (tableName = "colors_table")
data class ColorItem (
    @PrimaryKey(autoGenerate = true) val id : Int = 0,
    val hexCode : String,
    val colorName : String?,
    val R : Int,
    val G : Int,
    val B : Int
)
