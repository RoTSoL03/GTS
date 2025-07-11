package com.example.gts_goattracker

import com.example.gts_goattracker.GoatDatabase
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent


class MainActivity : AppCompatActivity() {
    private lateinit var db: GoatDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = GoatDatabase.getDatabase(this)

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
        val milkYieldField = findViewById<EditText>(R.id.editMilkYield)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val viewGoatsButton = findViewById<Button>(R.id.viewGoatsButton)

        // Vaccine dropdown values
        val vaccineOptions = arrayOf("None", "Vitamin A - 10ml", "Vitamin B12 - 5ml", "Anti-Tetanus - 2ml")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, vaccineOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        vaccineSpinner.adapter = spinnerAdapter

        // 📅 Calendar Picker for Date of Birth
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

        // FIXED: View Goats Button moved outside of save button listener
        viewGoatsButton.setOnClickListener {
            val intent = Intent(this, GoatListActivity::class.java)
            startActivity(intent)
        }

        saveButton.setOnClickListener {
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
            val milkYield = milkYieldField.text.toString().toDoubleOrNull() ?: 0.0

            // Calculate age from DOB
            val age = try {
                if (dobString.isBlank()) 0
                else {
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val dobDate = sdf.parse(dobString)
                    val dobCalendar = Calendar.getInstance().apply { time = dobDate!! }
                    val today = Calendar.getInstance()
                    today.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR)
                }
            } catch (e: Exception) {
                0
            }

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
                milkYield = milkYield
            )

            lifecycleScope.launch {
                try {
                    db.goatDao().insert(newGoat)
                    Toast.makeText(this@MainActivity, "Goat saved!", Toast.LENGTH_SHORT).show()
                    // FIXED: Clear form instead of finish() to prevent crash
                    clearForm()
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Error saving goat: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Helper function to clear the form after saving
    private fun clearForm() {
        findViewById<EditText>(R.id.editTagId).setText("")
        findViewById<EditText>(R.id.editName).setText("")
        findViewById<EditText>(R.id.editDob).setText("")
        findViewById<EditText>(R.id.editBreed).setText("")
        findViewById<RadioGroup>(R.id.radioGroupGender).clearCheck()
        findViewById<EditText>(R.id.editHeight).setText("")
        findViewById<EditText>(R.id.editWeight).setText("")
        findViewById<EditText>(R.id.editLineage).setText("")
        findViewById<Spinner>(R.id.spinnerVaccine).setSelection(0)
        findViewById<EditText>(R.id.editHealthNotes).setText("")
        findViewById<EditText>(R.id.editMilkYield).setText("")
    }
}