package com.example.gts_goattracker

import GoatDatabase
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

import com.example.gts_goattracker.Goat


class GoatListActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var db: GoatDatabase
    private var goats: List<Goat> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goat_list)

        listView = findViewById(R.id.goatListView)
        db = GoatDatabase.getDatabase(this)

        lifecycleScope.launch {
            goats = db.goatDao().getAll()

            val goatStrings = goats.map {
                "🐐 [${it.tagId}] ${it.name}, ${it.age} yrs, ${it.breed}, ${it.weight} kg"
            }

            val adapter = ArrayAdapter(
                this@GoatListActivity,
                android.R.layout.simple_list_item_1,
                goatStrings
            )

            listView.adapter = adapter

            listView.setOnItemClickListener { _, _, position, _ ->
                val selectedGoat = goats[position]
                val intent = Intent(this@GoatListActivity, GoatDetailActivity::class.java)
                intent.putExtra("goatId", selectedGoat.id)
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh goat list when returning from edit/delete screen
        lifecycleScope.launch {
            goats = db.goatDao().getAll()

            val goatStrings = goats.map {
                "🐐 [${it.tagId}] ${it.name}, ${it.age} yrs, ${it.breed}, ${it.weight} kg"
            }

            val adapter: ArrayAdapter<String> = ArrayAdapter(
                this@GoatListActivity,
                android.R.layout.simple_list_item_1,
                goatStrings
            )

            listView.adapter = adapter
        }
    }
}
