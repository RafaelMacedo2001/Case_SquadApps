    package com.example.motivation

    data class VideoDevices(
        val id: String,
        val name: String,
        val serial: String,
        val username: String,
        val password: String
    ) : BindableDevice {
        override fun bind(viewHolder: ItemViewHolder) {
            viewHolder.videoBind(this)
        }
    }