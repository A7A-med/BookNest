package com.example.colorapp

import android.content.Context
import android.os.Build
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ColorItem :: class], version = 1, exportSchema = false)
abstract class ColorDatabase : RoomDatabase() {

    abstract fun ColorDao(): ColorDao

    companion object {
        private var INSTANCE: ColorDatabase? = null

        fun getDatabase(context: Context): ColorDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ColorDatabase::class.java,
                    "color_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }


    }
}