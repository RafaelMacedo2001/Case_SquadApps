package com.example.motivation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.motivation.databinding.ItemAlarmDeviceBinding
import com.example.motivation.databinding.ItemVideoDeviceBinding
import android.widget.PopupMenu

sealed class ItemViewHolder(itemView: View, val deviceAdapter: DeviceAdapter) : RecyclerView.ViewHolder(itemView) {

    abstract fun bind(bindableDevice: BindableDevice)

    class AlarmViewHolder(private val binding: ItemAlarmDeviceBinding, deviceAdapter: DeviceAdapter) : ItemViewHolder(binding.root, deviceAdapter) {
        override fun bind(bindableDevice: BindableDevice) {
            if (bindableDevice is AlarmDevices) {
                binding.alarmDeviceName.text = bindableDevice.name
                binding.menuButton.setOnClickListener {
                    showPopupMenu(binding.menuButton, adapterPosition, deviceAdapter.items)
                }
            }
        }
    }

    class VideoViewHolder(private val binding: ItemVideoDeviceBinding, deviceAdapter: DeviceAdapter) : ItemViewHolder(binding.root, deviceAdapter) {
        override fun bind(bindableDevice: BindableDevice) {
            if (bindableDevice is VideoDevices) {
                binding.videoDeviceName.text = bindableDevice.name
                binding.menuButton.setOnClickListener {
                    showPopupMenu(binding.menuButton, adapterPosition, deviceAdapter.items)
                }
            }
        }
    }

    fun showPopupMenu(view: View, position: Int, items: List<BindableDevice>) {
        // Criando um PopupMenu
        val popup = PopupMenu(view.context, view)

        // Inflando o menu com base no tipo de dispositivo
        when (items[position]) {
            is AlarmDevices -> popup.inflate(R.menu.item_alarm_context_menu)
            is VideoDevices -> popup.inflate(R.menu.item_video_context_menu)
        }

        // Adicionando um listener de clique para os itens do menu
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_edit_alarm -> {
                    // Código para editar o dispositivo de alarme
                }
                R.id.action_edit_video -> {
                    // Código para editar o dispositivo de vídeo
                }
                R.id.action_bookmark_alarm, R.id.action_bookmark_video -> {
                    // Código para adicionar os dispositivos aos favoritos
                }
                R.id.action_info_alarm -> {
                    // Código para informações do dispositivo de alarme
                }
                R.id.action_info_video -> {
                    // Código para informações do dispositivo de vídeo
                }
                R.id.action_delete_alarm -> {
                    // Código para excluir o dispositivo de alarme
                }
                R.id.action_delete_video -> {
                    // Código para excluir o dispositivo de vídeo
                    val deviceId = (items[position] as? VideoDevices)?.id
                    if (deviceId != null) {
                        deviceAdapter.deleteVideoDevice(deviceId)
                    }
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
                    AlarmViewHolder(alarmBinding, deviceAdapter)
                }
                1 -> {
                    val videoBinding = ItemVideoDeviceBinding.inflate(layoutInflater, parent, false)
                    VideoViewHolder(videoBinding, deviceAdapter)
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
