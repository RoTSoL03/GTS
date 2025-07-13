package com.example.gts_goattracker

object Constants {
    const val DATABASE_NAME = "goat_database"
    const val DATABASE_VERSION = 5 // Incremented for new fields

    // Validation constants
    const val MIN_TAG_LENGTH = 3
    const val MIN_NAME_LENGTH = 2
    const val MAX_WEIGHT = 200.0
    const val MAX_HEIGHT = 150.0
    const val MAX_MILK_YIELD = 10.0

    // Date format
    const val DATE_FORMAT = "yyyy-MM-dd"

    // Vaccine options
    val VACCINE_OPTIONS = arrayOf(
        "None",
        "Vitamin A - 10ml",
        "Vitamin B12 - 5ml",
        "Anti-Tetanus - 2ml"
    )
}