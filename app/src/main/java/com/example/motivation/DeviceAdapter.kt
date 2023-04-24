package com.example.motivation

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class DeviceAdapter : RecyclerView.Adapter<ItemViewHolder>(), OnMenuClickListener {

    private var items: List<BindableDevice> = emptyList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {

        return ItemViewHolder.create(parent, viewType, this)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        items[position].bind(holder)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateItems(newItems: List<BindableDevice>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onMenuClick(position: Int, view: View) {
        // implementar a lógica para lidar com cliques no menu
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is AlarmDevices -> 0
            is VideoDevices -> 1
            else -> throw IllegalArgumentException("Invalid item type at position $position")
        }
    }
}