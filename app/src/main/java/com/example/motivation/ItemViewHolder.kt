package com.example.motivation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.motivation.databinding.ItemVideoDeviceBinding

class ItemViewHolder(private val binding: ItemVideoDeviceBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Item) {
        binding.deviceName.text = item.name
    }

    companion object {
        fun create(parent: ViewGroup): ItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemVideoDeviceBinding.inflate(inflater, parent, false)
            return ItemViewHolder(binding)
        }
    }
}