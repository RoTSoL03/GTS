package com.example.gts_goattracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Goat(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tagId: String,
    val name: String,
    val age: Int,
    val breed: String,
    val gender: String,
    val height: Double,
    val weight: Double,
    val lineage: String,
    val dateRegistered: Long,
    val vaccineGiven: String,
    val healthNotes: String,
    val milkYield: Double
)
