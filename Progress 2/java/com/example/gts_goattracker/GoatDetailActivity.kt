package com.example.gts_goattracker

import com.example.gts_goattracker.GoatDatabase
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class GoatDetailActivity : AppCompatActivity() {
    private var goatId: Int = -1
    private lateinit var db: GoatDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goat_detail)

        db = GoatDatabase.getDatabase(this)

        val tagField = findViewById<EditText>(R.id.editTagId)
        val nameField = findViewById<EditText>(R.id.editName)
        val ageField = findViewById<EditText>(R.id.editAge)
        val breedField = findViewById<EditText>(R.id.editBreed)
        val heightField = findViewById<EditText>(R.id.editHeight)
        val weightField = findViewById<EditText>(R.id.editWeight) // FIXED: Now properly referenced
        val lineageField = findViewById<EditText>(R.id.editLineage)
        val healthNotesField = findViewById<EditText>(R.id.editHealthNotes)
        val milkYieldField = findViewById<EditText>(R.id.editMilkYield)
        val updateBtn = findViewById<Button>(R.id.updateButton)
        val deleteBtn = findViewById<Button>(R.id.deleteButton)

        val genderGroup = findViewById<RadioGroup>(R.id.radioGroupGender)
        val radioMale = findViewById<RadioButton>(R.id.radioMale)
        val radioFemale = findViewById<RadioButton>(R.id.radioFemale)

        val vaccineSpinner = findViewById<Spinner>(R.id.spinnerVaccine)
        val vaccineOptions = arrayOf("None", "Vaccine A (1ml)", "Vaccine B (2ml)", "Vitamin C (0.5ml)")
        vaccineSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, vaccineOptions)

        goatId = intent.getIntExtra("goatId", -1)

        lifecycleScope.launch {
            val goat = db.goatDao().getById(goatId)
            if (goat != null) {
                tagField.setText(goat.tagId)
                nameField.setText(goat.name)
                ageField.setText(goat.age.toString())
                breedField.setText(goat.breed)
                heightField.setText(goat.height.toString())
                weightField.setText(goat.weight.toString()) // FIXED: Now properly sets weight
                lineageField.setText(goat.lineage)
                healthNotesField.setText(goat.healthNotes)
                milkYieldField.setText(goat.milkYield.toString())

                if (goat.gender == "Male") {
                    radioMale.isChecked = true
                } else {
                    radioFemale.isChecked = true
                }

                val vaccineIndex = vaccineOptions.indexOf(goat.vaccineGiven)
                if (vaccineIndex >= 0) vaccineSpinner.setSelection(vaccineIndex)
            }
        }

        updateBtn.setOnClickListener {
            val gender = if (radioMale.isChecked) "Male" else "Female"

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
                milkYield = milkYieldField.text.toString().toDoubleOrNull() ?: 0.0
            )

            lifecycleScope.launch {
                try {
                    // FIXED: Use proper update instead of insert
                    db.goatDao().update(updatedGoat)
                    Toast.makeText(this@GoatDetailActivity, "Goat updated!", Toast.LENGTH_SHORT).show()
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@GoatDetailActivity, "Error updating goat: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        deleteBtn.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val goat = db.goatDao().getById(goatId)
                    if (goat != null) {
                        db.goatDao().delete(goat)
                        Toast.makeText(this@GoatDetailActivity, "Goat deleted!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@GoatDetailActivity, "Error deleting goat: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}