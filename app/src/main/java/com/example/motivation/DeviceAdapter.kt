package com.example.motivation

import AlarmApiService
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

    //excluir dispositivo de vídeo

    private val alarmRetrofit = Retrofit.Builder()
        .baseUrl("http://squadapps.ddns-intelbras.com.br:3000/alarm-centrals/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val alarmDevicesApi: AlarmApiService = alarmRetrofit.create(AlarmApiService::class.java)

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
                    val newVideoDevices =
                        devicesResponse.body()?.data?.map { VideoDevices(it.id, it.name, it.serial, it.username, it.password) as BindableDevice } ?: emptyList()

                    val alarmDevicesResponse = alarmDevicesApi.getAlarmDevices().execute()
                    if (alarmDevicesResponse.isSuccessful) {
                        val newAlarmDevices = alarmDevicesResponse.body()?.data?.map { AlarmDevices(it.id, it.name, it.macAddress, it.password) as BindableDevice } ?: emptyList()

                        withContext(Dispatchers.Main) {
                            (context as MainActivity).updateDevicesList()
                        }
                    } else {
                        Log.e("DeviceAdapter", "Falha ao buscar dispositivos de alarme atualizados: ${alarmDevicesResponse.errorBody()?.string()}")
                    }
                } else {
                    Log.e("DeviceAdapter", "Falha ao buscar dispositivos de vídeo atualizados: ${devicesResponse.errorBody()?.string()}")
                }
            } else {
                Log.e("DeviceAdapter", "Falha ao deletar o dispositivo: ${response.errorBody()?.string()}")
            }
        }
    }

    fun deleteAlarmDevice(deviceId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = alarmDevicesApi.deleteAlarmDevice(deviceId)
            if (response.isSuccessful && response.code() == 200) {
                Log.d("DeviceAdapter", "Dispositivo de alarme deletado com sucesso")

                val alarmDevicesResponse = alarmDevicesApi.getAlarmDevices().execute()
                if (alarmDevicesResponse.isSuccessful) {
                    val newAlarmDevices = alarmDevicesResponse.body()?.data?.map { AlarmDevices(it.id, it.name, it.macAddress, it.password) as BindableDevice } ?: emptyList()

                    val videoDevicesResponse = videoDevicesApi.getvideoDevices().execute()
                    if (videoDevicesResponse.isSuccessful) {
                        val newVideoDevices = videoDevicesResponse.body()?.data?.map { VideoDevices(it.id, it.name, it.serial, it.username, it.password) as BindableDevice } ?: emptyList()

                        withContext(Dispatchers.Main) {
                            (context as MainActivity).updateDevicesList()
                        }
                    } else {
                        Log.e("DeviceAdapter", "Falha ao buscar dispositivos de vídeo atualizados: ${videoDevicesResponse.errorBody()?.string()}")
                    }
                } else {
                    Log.e("DeviceAdapter", "Falha ao buscar dispositivos de alarme atualizados: ${alarmDevicesResponse.errorBody()?.string()}")
                }
            } else {
                Log.e("DeviceAdapter", "Falha ao deletar o dispositivo de alarme: ${response.errorBody()?.string()}")
            }
        }
    }
}
