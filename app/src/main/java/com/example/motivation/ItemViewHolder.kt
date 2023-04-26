package com.example.motivation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.motivation.databinding.ItemAlarmDeviceBinding
import com.example.motivation.databinding.ItemVideoDeviceBinding
import android.widget.PopupMenu

sealed class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    abstract fun bind(bindableDevice: BindableDevice)

    class AlarmViewHolder(private val binding: ItemAlarmDeviceBinding) : ItemViewHolder(binding.root) {
        override fun bind(bindableDevice: BindableDevice) {
            if (bindableDevice is AlarmDevices) {
                binding.alarmDeviceName.text = bindableDevice.name
                binding.menuButton.setOnClickListener {
                    showPopupMenu(binding.menuButton, adapterPosition)
                }
            }
        }
    }

    class VideoViewHolder(private val binding: ItemVideoDeviceBinding) : ItemViewHolder(binding.root) {
        override fun bind(bindableDevice: BindableDevice) {
            if (bindableDevice is VideoDevices) {
                binding.videoDeviceName.text = bindableDevice.name
                binding.menuButton.setOnClickListener {
                    showPopupMenu(binding.menuButton, adapterPosition)
                }
            }
        }
    }

    fun showPopupMenu(view: View, position: Int) {
        // Criando um PopupMenu
        val popup = PopupMenu(view.context, view)
        // Inflando o item_context_menu
        popup.inflate(R.menu.item_context_menu)
        // Adicionando um listener de clique para os itens do menu
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_edit_video -> {
                    // Código para editar o dispositivo
                }
                R.id.action_bookmark_video -> {
                    // Código para adicionar o dispositivo aos favoritos
                }
                R.id.action_info_video -> {
                    // Código para mostrar informações do dispositivo
                }
                R.id.action_delete_video -> {
                    // Código para excluir o dispositivo
                }
            }
            false
        }
        // Mostrando o PopupMenu
        popup.show()
    }

    companion object {
        fun create(parent: ViewGroup, viewType: Int, deviceAdapter: DeviceAdapter): ItemViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            return when (viewType) {
                0 -> {
                    val alarmBinding = ItemAlarmDeviceBinding.inflate(layoutInflater, parent, false)
                    AlarmViewHolder(alarmBinding)
                }
                1 -> {
                    val videoBinding = ItemVideoDeviceBinding.inflate(layoutInflater, parent, false)
                    VideoViewHolder(videoBinding)
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
