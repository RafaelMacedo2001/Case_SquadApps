package com.example.motivation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.motivation.databinding.ItemAlarmDeviceBinding
import com.example.motivation.databinding.ItemVideoDeviceBinding

interface OnMenuClickListener {
    fun onMenuClick(position: Int, view: View)
}

sealed class ItemViewHolder(itemView: View, val menuClickListener: OnMenuClickListener) : RecyclerView.ViewHolder(itemView) {

    abstract fun bind(bindableDevice: BindableDevice)

    class AlarmViewHolder(private val binding: ItemAlarmDeviceBinding, menuClickListener: OnMenuClickListener) : ItemViewHolder(binding.root, menuClickListener) {
        override fun bind(bindableDevice: BindableDevice) {
            if (bindableDevice is AlarmDevices) {
                binding.alarmDeviceName.text = bindableDevice.name
                binding.menuButton.setOnClickListener {
                    menuClickListener.onMenuClick(bindingAdapterPosition, it)
                }
            }
        }
    }

    class VideoViewHolder(private val binding: ItemVideoDeviceBinding, menuClickListener: OnMenuClickListener) : ItemViewHolder(binding.root, menuClickListener) {
        override fun bind(bindableDevice: BindableDevice) {
            if (bindableDevice is VideoDevices) {
                binding.videoDeviceName.text = bindableDevice.name
                binding.menuButton.setOnClickListener {
                    menuClickListener.onMenuClick(bindingAdapterPosition, it)
                }
            }
        }
    }

    companion object {
        fun create(parent: ViewGroup, viewType: Int, menuClickListener: OnMenuClickListener): ItemViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            return when (viewType) {
                0 -> {
                    val alarmBinding = ItemAlarmDeviceBinding.inflate(layoutInflater, parent, false)
                    AlarmViewHolder(alarmBinding, menuClickListener)
                }
                1 -> {
                    val videoBinding = ItemVideoDeviceBinding.inflate(layoutInflater, parent, false)
                    VideoViewHolder(videoBinding, menuClickListener)
                }
                else -> throw IllegalArgumentException("Invalid viewType: $viewType")
            }
        }
    }
}

fun ItemViewHolder.alarmBind(alarmDevice: AlarmDevices) {
    if (this is ItemViewHolder.AlarmViewHolder) {
        this.bind(alarmDevice)
    }
}

fun ItemViewHolder.videoBind(videoDevice: VideoDevices) {
    if (this is ItemViewHolder.VideoViewHolder) {
        this.bind(videoDevice)
    }
}