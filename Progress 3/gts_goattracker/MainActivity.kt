package com.example.gts_goattracker

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var db: GoatDatabase
    private lateinit var milkYieldField: EditText
    private lateinit var milkYieldLabel: TextView
    private lateinit var lactatingCheckbox: CheckBox
    private lateinit var pregnantCheckbox: CheckBox
    private lateinit var femaleOnlyContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = GoatDatabase.getDatabase(this)

        val tagField = findViewById<EditText>(R.id.editTagId)
        val nameField = findViewById<EditText>(R.id.editName)
        val dobField = findViewById<EditText>(R.id.editDob)
        val breedField = findViewById<EditText>(R.id.editBreed)
        val genderGroup = findViewById<RadioGroup>(R.id.radioGroupGender)
        val radioMale = findViewById<RadioButton>(R.id.radioMale)
        val radioFemale = findViewById<RadioButton>(R.id.radioFemale)
        val heightField = findViewById<EditText>(R.id.editHeight)
        val weightField = findViewById<EditText>(R.id.editWeight)
        val lineageField = findViewById<EditText>(R.id.editLineage)
        val vaccineSpinner = findViewById<Spinner>(R.id.spinnerVaccine)
        val healthNotesField = findViewById<EditText>(R.id.editHealthNotes)
        milkYieldField = findViewById(R.id.editMilkYield)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val viewGoatsButton = findViewById<Button>(R.id.viewGoatsButton)

        // Initialize female-only UI elements
        // Note: These would need to be added to your layout XML
        milkYieldLabel = findViewById(R.id.labelMilkYield)
        lactatingCheckbox = findViewById(R.id.checkboxLactating)
        pregnantCheckbox = findViewById(R.id.checkboxPregnant)
        femaleOnlyContainer = findViewById(R.id.femaleOnlyContainer)

        // Auto-generate RFID tag
        generateRfidTag(tagField)

        // Vaccine dropdown values
        val vaccineOptions = Constants.VACCINE_OPTIONS
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, vaccineOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        vaccineSpinner.adapter = spinnerAdapter

        // Calendar Picker for Date of Birth
        val calendar = Calendar.getInstance()
        dobField.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, y, m, d ->
                val formatted = String.format("%04d-%02d-%02d", y, m + 1, d)
                dobField.setText(formatted)
            }, year, month, day)
            datePicker.show()
        }

        // Gender selection listener for conditional inputs
        genderGroup.setOnCheckedChangeListener { _, checkedId ->
            updateUIBasedOnGender(checkedId)
        }

        // Initially hide female-only inputs
        updateUIBasedOnGender(-1)

        viewGoatsButton.setOnClickListener {
            val intent = Intent(this, GoatListActivity::class.java)
            startActivity(intent)
        }

        saveButton.setOnClickListener {
            if (validateForm()) {
                saveGoat()
            }
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

    private fun generateRfidTag(tagField: EditText) {
        lifecycleScope.launch {
            try {
                val count = db.goatDao().getGoatCount()
                val nextId = count + 1
                val rfidTag = "RFID${String.format("%04d", nextId)}"
                tagField.setText(rfidTag)
            } catch (e: Exception) {
                tagField.setText("RFID0001")
            }
        }
    }

    private fun validateForm(): Boolean {
        val tagField = findViewById<EditText>(R.id.editTagId)
        val nameField = findViewById<EditText>(R.id.editName)
        val heightField = findViewById<EditText>(R.id.editHeight)
        val weightField = findViewById<EditText>(R.id.editWeight)
        val genderGroup = findViewById<RadioGroup>(R.id.radioGroupGender)

        // Validate Tag ID
        val tagError = ValidationUtils.validateTagId(tagField.text.toString())
        if (tagError != null) {
            tagField.error = tagError
            return false
        }

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

    private fun saveGoat() {
        val tagField = findViewById<EditText>(R.id.editTagId)
        val nameField = findViewById<EditText>(R.id.editName)
        val dobField = findViewById<EditText>(R.id.editDob)
        val breedField = findViewById<EditText>(R.id.editBreed)
        val genderGroup = findViewById<RadioGroup>(R.id.radioGroupGender)
        val heightField = findViewById<EditText>(R.id.editHeight)
        val weightField = findViewById<EditText>(R.id.editWeight)
        val lineageField = findViewById<EditText>(R.id.editLineage)
        val vaccineSpinner = findViewById<Spinner>(R.id.spinnerVaccine)
        val healthNotesField = findViewById<EditText>(R.id.editHealthNotes)

        val tagId = tagField.text.toString()
        val name = nameField.text.toString()
        val dobString = dobField.text.toString()
        val breed = breedField.text.toString()
        val selectedGenderId = genderGroup.checkedRadioButtonId
        val gender = findViewById<RadioButton>(selectedGenderId)?.text?.toString() ?: ""
        val height = heightField.text.toString().toDoubleOrNull() ?: 0.0
        val weight = weightField.text.toString().toDoubleOrNull() ?: 0.0
        val lineage = lineageField.text.toString()
        val vaccine = vaccineSpinner.selectedItem.toString()
        val healthNotes = healthNotesField.text.toString()

        // Gender-specific fields
        val milkYield = if (gender == "Female") {
            milkYieldField.text.toString().toDoubleOrNull() ?: 0.0
        } else {
            0.0 // Male goats don't produce milk
        }
        val isLactating = gender == "Female" && lactatingCheckbox.isChecked
        val isPregnant = gender == "Female" && pregnantCheckbox.isChecked

        // Calculate age from DOB
        val age = calculateAge(dobString)

        val newGoat = Goat(
            tagId = tagId,
            name = name,
            age = age,
            breed = breed,
            gender = gender,
            height = height,
            weight = weight,
            lineage = lineage,
            dateRegistered = System.currentTimeMillis(),
            vaccineGiven = vaccine,
            healthNotes = healthNotes,
            milkYield = milkYield,
            isLactating = isLactating,
            isPregnant = isPregnant
        )

        lifecycleScope.launch {
            try {
                db.goatDao().insert(newGoat)
                LoadingUtils.showSuccess(this@MainActivity, "Goat saved successfully!")
                clearForm()
            } catch (e: Exception) {
                LoadingUtils.showError(this@MainActivity, "Error saving goat: ${e.message}")
            }
        }
    }

    private fun calculateAge(dobString: String): Int {
        return try {
            if (dobString.isBlank()) 0
            else {
                val sdf = SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault())
                val dobDate = sdf.parse(dobString)
                val dobCalendar = Calendar.getInstance().apply { time = dobDate!! }
                val today = Calendar.getInstance()
                var age = today.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR)
                if (today.get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
                    age--
                }
                age
            }
        } catch (e: Exception) {
            0
        }
    }

    private fun clearForm() {
        findViewById<EditText>(R.id.editName).setText("")
        findViewById<EditText>(R.id.editDob).setText("")
        findViewById<EditText>(R.id.editBreed).setText("")
        val genderGroup = findViewById<RadioGroup>(R.id.radioGroupGender)
        genderGroup.clearCheck()
        findViewById<EditText>(R.id.editHeight).setText("")
        findViewById<EditText>(R.id.editWeight).setText("")
        findViewById<EditText>(R.id.editLineage).setText("")
        findViewById<Spinner>(R.id.spinnerVaccine).setSelection(0)
        findViewById<EditText>(R.id.editHealthNotes).setText("")
        milkYieldField.setText("")
        lactatingCheckbox.isChecked = false
        pregnantCheckbox.isChecked = false

        // Update UI based on cleared gender selection
        updateUIBasedOnGender(-1)

        // Regenerate RFID tag for next goat
        generateRfidTag(findViewById(R.id.editTagId))
    }
}