package com.example.gts_goattracker

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class GoatListActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var db: GoatDatabase
    private var goats: List<Goat> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goat_list)

        listView = findViewById(R.id.goatListView)
        db = GoatDatabase.getDatabase(this)

        loadGoatList()

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedGoat = goats[position]
            val intent = Intent(this@GoatListActivity, GoatDetailActivity::class.java)
            intent.putExtra("goatId", selectedGoat.id)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh goat list when returning from edit/delete screen
        loadGoatList()
    }

    private fun loadGoatList() {
        lifecycleScope.launch {
            try {
                goats = db.goatDao().getAll()

                val goatStrings = goats.map { goat ->
                    val baseInfo = "🐐 [${goat.tagId}] ${goat.name}, ${goat.age} yrs, ${goat.breed}, ${goat.weight} kg"

                    if (goat.gender == "Female") {
                        val femaleInfo = buildString {
                            append(" | Milk: ${goat.milkYield}L/day")
                            if (goat.isLactating) append(" 🥛")
                            if (goat.isPregnant) append(" 🤰")
                        }
                        baseInfo + femaleInfo
                    } else {
                        baseInfo
                    }
                }

                val adapter = ArrayAdapter(
                    this@GoatListActivity,
                    android.R.layout.simple_list_item_1,
                    goatStrings
                )

                listView.adapter = adapter
            } catch (e: Exception) {
                LoadingUtils.showError(this@GoatListActivity, "Error loading goats: ${e.message}")
            }
        }
    }
}