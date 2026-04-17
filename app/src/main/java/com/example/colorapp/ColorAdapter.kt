package com.example.colorapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ColorAdapter(
    private val onDeleteClick: (ColorItem) -> Unit,
    private val onItemClick: (ColorItem) -> Unit
) : ListAdapter<ColorItem, ColorAdapter.ColorViewHolder>(ColorDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_color, parent, false)
        return ColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val colorItem = getItem(position)
        holder.bind(colorItem, onDeleteClick, onItemClick)
    }


    class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val colorBox: View = itemView.findViewById(R.id.colorBox)
        private val hexText: TextView = itemView.findViewById(R.id.hexCode)

        fun bind(item: ColorItem, onDelete: (ColorItem) -> Unit, onClick: (ColorItem) -> Unit) {
            colorBox.setBackgroundColor(Color.parseColor(item.hexCode))
            hexText.text = item.hexCode
            itemView.setOnClickListener { onClick(item) }
        }
    }

    class ColorDiffCallback : DiffUtil.ItemCallback<ColorItem>() {
        override fun areItemsTheSame(oldItem: ColorItem, newItem: ColorItem) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ColorItem, newItem: ColorItem) = oldItem == newItem
    }
}