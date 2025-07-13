package com.example.gts_goattracker

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "goats")
data class Goat(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val tagId: String,
    val name: String,
    val age: Int, // Keep as Int for compatibility with existing code
    val breed: String,
    val gender: String,
    val height: Double,
    val weight: Double,
    val lineage: String,
    val dateRegistered: Long,
    val vaccineGiven: String,
    val healthNotes: String,
    val milkYield: Double,
    val isLactating: Boolean = false, // New field for female goats
    val isPregnant: Boolean = false   // New field for female goats
) {
    // Validation functions
    fun isValid(): Boolean {
        return tagId.isNotBlank() &&
                name.isNotBlank() &&
                breed.isNotBlank() &&
                gender.isNotBlank() &&
                height > 0 &&
                weight > 0
    }
}