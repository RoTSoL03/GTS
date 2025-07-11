package com.example.gts_goattracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class GoatListAdapter(
    private val onItemClick: (Goat) -> Unit
) : ListAdapter<Goat, GoatListAdapter.GoatViewHolder>(GoatDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return GoatViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GoatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(android.R.id.text1)
        private val subtitleText: TextView = itemView.findViewById(android.R.id.text2)

        fun bind(goat: Goat) {
            titleText.text = "🐐 [${goat.tagId}] ${goat.name}"
            subtitleText.text = "${goat.age} years • ${goat.breed} • ${goat.weight}kg"

            itemView.setOnClickListener {
                onItemClick(goat)
            }
        }
    }
}

class GoatDiffCallback : DiffUtil.ItemCallback<Goat>() {
    override fun areItemsTheSame(oldItem: Goat, newItem: Goat): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Goat, newItem: Goat): Boolean {
        return oldItem == newItem
    }
}