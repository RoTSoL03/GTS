package com.example.gts_goattracker

object ValidationUtils {

    fun validateTagId(tagId: String): String? {
        return when {
            tagId.isBlank() -> "Tag ID is required"
            tagId.length < 3 -> "Tag ID must be at least 3 characters"
            else -> null
        }
    }

    fun validateName(name: String): String? {
        return when {
            name.isBlank() -> "Name is required"
            name.length < 2 -> "Name must be at least 2 characters"
            else -> null
        }
    }

    fun validateWeight(weight: String): String? {
        val weightValue = weight.toDoubleOrNull()
        return when {
            weight.isBlank() -> "Weight is required"
            weightValue == null -> "Invalid weight format"
            weightValue <= 0 -> "Weight must be positive"
            weightValue > 200 -> "Weight seems too high (max 200kg)"
            else -> null
        }
    }

    fun validateHeight(height: String): String? {
        val heightValue = height.toDoubleOrNull()
        return when {
            height.isBlank() -> "Height is required"
            heightValue == null -> "Invalid height format"
            heightValue <= 0 -> "Height must be positive"
            heightValue > 150 -> "Height seems too high (max 150cm)"
            else -> null
        }
    }

    fun validateMilkYield(milkYield: String): String? {
        val milkYieldValue = milkYield.toDoubleOrNull()
        return when {
            milkYield.isBlank() -> "Milk yield is required for female goats"
            milkYieldValue == null -> "Invalid milk yield format"
            milkYieldValue < 0 -> "Milk yield cannot be negative"
            milkYieldValue > 10 -> "Milk yield seems too high (max 10L per day)"
            else -> null
        }
    }
}