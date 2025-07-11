package com.example.gts_goattracker

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Goat::class], version = 3)
abstract class GoatDatabase : RoomDatabase() {
    abstract fun goatDao(): GoatDao

    companion object {
        @Volatile
        private var INSTANCE: GoatDatabase? = null

        fun getDatabase(context: Context): GoatDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GoatDatabase::class.java,
                    "goat_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}