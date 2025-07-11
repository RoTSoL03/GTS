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
    val dateOfBirth: Long, // Store as timestamp
    val breed: String,
    val gender: String,
    val height: Double,
    val weight: Double,
    val lineage: String,
    val dateRegistered: Long,
    val vaccineGiven: String,
    val healthNotes: String,
    val milkYield: Double
) {
    // Computed property for age
    val age: Int
        get() {
            val birthCalendar = Calendar.getInstance().apply { timeInMillis = dateOfBirth }
            val today = Calendar.getInstance()
            var age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)

            if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
                age--
            }
            return age
        }

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