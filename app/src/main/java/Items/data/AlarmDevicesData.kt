package com.example.motivation

data class AlarmDevices(
    val id: String,
    val name: String,
    val macAddress: String,
    val password: String
) : BindableDevice {
    override fun bind(viewHolder: ItemViewHolder) {
        viewHolder.alarmBind(this)
    }
}