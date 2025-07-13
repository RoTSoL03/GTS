package com.example.gts_goattracker

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class GoatDetailActivity : AppCompatActivity() {
    private var goatId: Int = -1
    private lateinit var db: GoatDatabase
    private lateinit var milkYieldField: EditText
    private lateinit var milkYieldLabel: TextView
    private lateinit var lactatingCheckbox: CheckBox
    private lateinit var pregnantCheckbox: CheckBox
    private lateinit var femaleOnlyContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goat_detail)

        db = GoatDatabase.getDatabase(this)

        val tagField = findViewById<EditText>(R.id.editTagId)
        val nameField = findViewById<EditText>(R.id.editName)
        val ageField = findViewById<EditText>(R.id.editAge)
        val breedField = findViewById<EditText>(R.id.editBreed)
        val heightField = findViewById<EditText>(R.id.editHeight)
        val weightField = findViewById<EditText>(R.id.editWeight)
        val lineageField = findViewById<EditText>(R.id.editLineage)
        val healthNotesField = findViewById<EditText>(R.id.editHealthNotes)
        milkYieldField = findViewById(R.id.editMilkYield)
        val updateBtn = findViewById<Button>(R.id.updateButton)
        val deleteBtn = findViewById<Button>(R.id.deleteButton)

        val genderGroup = findViewById<RadioGroup>(R.id.radioGroupGender)
        val radioMale = findViewById<RadioButton>(R.id.radioMale)
        val radioFemale = findViewById<RadioButton>(R.id.radioFemale)

        // Initialize female-only UI elements
        milkYieldLabel = findViewById(R.id.labelMilkYield)
        lactatingCheckbox = findViewById(R.id.checkboxLactating)
        pregnantCheckbox = findViewById(R.id.checkboxPregnant)
        femaleOnlyContainer = findViewById(R.id.femaleOnlyContainer)

        val vaccineSpinner = findViewById<Spinner>(R.id.spinnerVaccine)
        val vaccineOptions = Constants.VACCINE_OPTIONS
        vaccineSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, vaccineOptions)

        goatId = intent.getIntExtra("goatId", -1)

        if (goatId == -1) {
            LoadingUtils.showError(this, "Invalid goat ID")
            finish()
            return
        }

        // Gender selection listener for conditional inputs
        genderGroup.setOnCheckedChangeListener { _, checkedId ->
            updateUIBasedOnGender(checkedId)
        }

        loadGoatData()

        updateBtn.setOnClickListener {
            if (validateForm()) {
                updateGoat()
            }
        }

        deleteBtn.setOnClickListener {
            showDeleteConfirmation()
        }
    }

    private fun updateUIBasedOnGender(checkedId: Int) {
        val radioMale = findViewById<RadioButton>(R.id.radioMale)
        val radioFemale = findViewById<RadioButton>(R.id.radioFemale)

        when (checkedId) {
            radioMale.id -> {
                // Male selected - hide female-only inputs
                milkYieldField.visibility = View.GONE
                milkYieldLabel.visibility = View.GONE
                femaleOnlyContainer.visibility = View.GONE

                // Clear female-only values
                milkYieldField.setText("0")
                lactatingCheckbox.isChecked = false
                pregnantCheckbox.isChecked = false
            }
            radioFemale.id -> {
                // Female selected - show female-only inputs
                milkYieldField.visibility = View.VISIBLE
                milkYieldLabel.visibility = View.VISIBLE
                femaleOnlyContainer.visibility = View.VISIBLE
            }
            else -> {
                // No gender selected - hide female-only inputs
                milkYieldField.visibility = View.GONE
                milkYieldLabel.visibility = View.GONE
                femaleOnlyContainer.visibility = View.GONE
            }
        }
    }

    private fun loadGoatData() {
        lifecycleScope.launch {
            try {
                val goat = db.goatDao().getById(goatId)
                if (goat != null) {
                    populateForm(goat)
                } else {
                    LoadingUtils.showError(this@GoatDetailActivity, "Goat not found")
                    finish()
                }
            } catch (e: Exception) {
                LoadingUtils.showError(this@GoatDetailActivity, "Error loading goat: ${e.message}")
                finish()
            }
        }
    }

    private fun populateForm(goat: Goat) {
        findViewById<EditText>(R.id.editTagId).setText(goat.tagId)
        findViewById<EditText>(R.id.editName).setText(goat.name)
        findViewById<EditText>(R.id.editAge).setText(goat.age.toString())
        findViewById<EditText>(R.id.editBreed).setText(goat.breed)
        findViewById<EditText>(R.id.editHeight).setText(goat.height.toString())
        findViewById<EditText>(R.id.editWeight).setText(goat.weight.toString())
        findViewById<EditText>(R.id.editLineage).setText(goat.lineage)
        findViewById<EditText>(R.id.editHealthNotes).setText(goat.healthNotes)
        milkYieldField.setText(goat.milkYield.toString())

        val genderGroup = findViewById<RadioGroup>(R.id.radioGroupGender)
        val radioMale = findViewById<RadioButton>(R.id.radioMale)
        val radioFemale = findViewById<RadioButton>(R.id.radioFemale)

        if (goat.gender == "Male") {
            radioMale.isChecked = true
        } else {
            radioFemale.isChecked = true
        }

        // Set female-only checkboxes
        lactatingCheckbox.isChecked = goat.isLactating
        pregnantCheckbox.isChecked = goat.isPregnant

        // Update UI based on gender
        updateUIBasedOnGender(genderGroup.checkedRadioButtonId)

        val vaccineSpinner = findViewById<Spinner>(R.id.spinnerVaccine)
        val vaccineOptions = Constants.VACCINE_OPTIONS
        val vaccineIndex = vaccineOptions.indexOf(goat.vaccineGiven)
        if (vaccineIndex >= 0) vaccineSpinner.setSelection(vaccineIndex)
    }

    private fun validateForm(): Boolean {
        val nameField = findViewById<EditText>(R.id.editName)
        val heightField = findViewById<EditText>(R.id.editHeight)
        val weightField = findViewById<EditText>(R.id.editWeight)
        val genderGroup = findViewById<RadioGroup>(R.id.radioGroupGender)

        // Validate Name
        val nameError = ValidationUtils.validateName(nameField.text.toString())
        if (nameError != null) {
            nameField.error = nameError
            return false
        }

        // Validate Weight
        val weightError = ValidationUtils.validateWeight(weightField.text.toString())
        if (weightError != null) {
            weightField.error = weightError
            return false
        }

        // Validate Height
        val heightError = ValidationUtils.validateHeight(heightField.text.toString())
        if (heightError != null) {
            heightField.error = heightError
            return false
        }

        // Validate Gender
        if (genderGroup.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate milk yield for female goats
        val radioFemale = findViewById<RadioButton>(R.id.radioFemale)
        if (radioFemale.isChecked) {
            val milkYieldError = ValidationUtils.validateMilkYield(milkYieldField.text.toString())
            if (milkYieldError != null) {
                milkYieldField.error = milkYieldError
                return false
            }
        }

        return true
    }

    private fun updateGoat() {
        val tagField = findViewById<EditText>(R.id.editTagId)
        val nameField = findViewById<EditText>(R.id.editName)
        val ageField = findViewById<EditText>(R.id.editAge)
        val breedField = findViewById<EditText>(R.id.editBreed)
        val heightField = findViewById<EditText>(R.id.editHeight)
        val weightField = findViewById<EditText>(R.id.editWeight)
        val lineageField = findViewById<EditText>(R.id.editLineage)
        val healthNotesField = findViewById<EditText>(R.id.editHealthNotes)
        val genderGroup = findViewById<RadioGroup>(R.id.radioGroupGender)
        val vaccineSpinner = findViewById<Spinner>(R.id.spinnerVaccine)

        val radioMale = findViewById<RadioButton>(R.id.radioMale)
        val gender = if (radioMale.isChecked) "Male" else "Female"

        // Gender-specific fields
        val milkYield = if (gender == "Female") {
            milkYieldField.text.toString().toDoubleOrNull() ?: 0.0
        } else {
            0.0 // Male goats don't produce milk
        }
        val isLactating = gender == "Female" && lactatingCheckbox.isChecked
        val isPregnant = gender == "Female" && pregnantCheckbox.isChecked

        val updatedGoat = Goat(
            id = goatId,
            tagId = tagField.text.toString(),
            name = nameField.text.toString(),
            age = ageField.text.toString().toIntOrNull() ?: 0,
            breed = breedField.text.toString(),
            gender = gender,
            height = heightField.text.toString().toDoubleOrNull() ?: 0.0,
            weight = weightField.text.toString().toDoubleOrNull() ?: 0.0,
            lineage = lineageField.text.toString(),
            dateRegistered = System.currentTimeMillis(),
            vaccineGiven = vaccineSpinner.selectedItem.toString(),
            healthNotes = healthNotesField.text.toString(),
            milkYield = milkYield,
            isLactating = isLactating,
            isPregnant = isPregnant
        )

        lifecycleScope.launch {
            try {
                db.goatDao().update(updatedGoat)
                LoadingUtils.showSuccess(this@GoatDetailActivity, "Goat updated successfully!")
                finish()
            } catch (e: Exception) {
                LoadingUtils.showError(this@GoatDetailActivity, "Error updating goat: ${e.message}")
            }
        }
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Delete Goat")
            .setMessage("Are you sure you want to delete this goat? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                deleteGoat()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteGoat() {
        lifecycleScope.launch {
            try {
                val goat = db.goatDao().getById(goatId)
                if (goat != null) {
                    db.goatDao().delete(goat)
                    LoadingUtils.showSuccess(this@GoatDetailActivity, "Goat deleted successfully!")
                    finish()
                }
            } catch (e: Exception) {
                LoadingUtils.showError(this@GoatDetailActivity, "Error deleting goat: ${e.message}")
            }
        }
    }
}