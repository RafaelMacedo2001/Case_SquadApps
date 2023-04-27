package com.example.motivation

import VideoApiService
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


interface OnMenuClickListener {
    fun onMenuClick(position: Int, view: View)
}

class DeviceAdapter(private val context: Context) : RecyclerView.Adapter<ItemViewHolder>(), OnMenuClickListener {

    var items: List<BindableDevice> = emptyList()
    var allDevices: List<BindableDevice> = emptyList()
        private set

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
        allDevices = newItems
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

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://squadapps.ddns-intelbras.com.br:3000/video-devices/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val videoDevicesApi: VideoApiService = retrofit.create(VideoApiService::class.java)

    fun deleteVideoDevice(deviceId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = videoDevicesApi.deleteVideoDevice(deviceId)
            if (response.isSuccessful && response.code() == 200) {
                Log.d("DeviceAdapter", "Dispositivo deletado com sucesso")
                val devicesResponse = videoDevicesApi.getvideoDevices().execute()
                if (devicesResponse.isSuccessful) {
                    val newDevices =
                        devicesResponse.body()?.data?.map { it as BindableDevice } ?: emptyList()
                    withContext(Dispatchers.Main) {
                        updateItems(newDevices)
                        notifyDeviceRemoved()
                    }
                } else {
                    Log.e("DeviceAdapter", "Falha ao buscar dispositivos atualizados: ${devicesResponse.errorBody()?.string()}")
                }
            } else {
                Log.e("DeviceAdapter", "Falha ao deletar o dispositivo: ${response.errorBody()?.string()}")
            }
        }
    }

    private fun notifyDeviceRemoved() {
        Toast.makeText(context, "Dispositivo removido com sucesso!", Toast.LENGTH_LONG).show()
    }
}
