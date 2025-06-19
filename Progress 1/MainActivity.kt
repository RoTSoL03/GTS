package com.example.gts_goattracker

import Goat
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch



class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Connect to the database
        val db = GoatDatabase.getDatabase(this)
        val goatDao = db.goatDao()

        // 2. Bind views
        val nameField = findViewById<EditText>(R.id.editName)
        val ageField = findViewById<EditText>(R.id.editAge)
        val breedField = findViewById<EditText>(R.id.editBreed)
        val weightField = findViewById<EditText>(R.id.editWeight)
        val saveButton = findViewById<Button>(R.id.saveButton)

        // 3. Set up Save button logic
        saveButton.setOnClickListener {
            val name = nameField.text.toString()
            val age = ageField.text.toString().toIntOrNull()
            val breed = breedField.text.toString()
            val weight = weightField.text.toString().toDoubleOrNull()

            if (name.isBlank() || age == null || breed.isBlank() || weight == null) {
                Toast.makeText(this, "Please fill in all fields correctly", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val goat = Goat(name = name, age = age, breed = breed, weight = weight)

                // 4. Save using Room (in coroutine)
                lifecycleScope.launch {
                    goatDao.insert(goat)
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Goat saved!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
