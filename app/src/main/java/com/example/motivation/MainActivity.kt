package com.example.motivation

import AlarmApiService
import VideoApiService
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.motivation.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.atomic.AtomicInteger
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private val deviceAdapter = DeviceAdapter()
    private val allDevices = mutableListOf<BindableDevice>()

    private var currentFilter = DeviceFilter.ALL

    enum class DeviceFilter {
        ALL, FAVORITES, VIDEO, ALARM
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.Fab.setOnClickListener(this)
        binding.imageAllDevices.setOnClickListener { setFilter(DeviceFilter.ALL) }
        binding.imageAlarmDevices.setOnClickListener { setFilter(DeviceFilter.ALARM) }
        binding.imageVideoDevices.setOnClickListener { setFilter(DeviceFilter.VIDEO) }

        // Set up RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = deviceAdapter

        // Fetch devices and update the list
        fetchDevices()
    }

    override fun onClick(view: View) {
        if (view.id == R.id.Fab) {
            addNewDevice()
        }
    }

    private fun addNewDevice() {
        val intent = Intent(this, AddDeviceActivity::class.java)
        startActivityForResult(intent, ADD_DEVICE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_DEVICE_REQUEST_CODE && resultCode == RESULT_OK) {
            // Atualizar a lista quando um novo dispositivo for adicionado
            fetchDevices()
        }
    }

    companion object {
        private const val ADD_DEVICE_REQUEST_CODE = 1
        const val ACTION_DEVICE_ADDED = "com.example.motivation.ACTION_DEVICE_ADDED"
    }

    private fun fetchDevices() {
        var alarmDevices: List<BindableDevice> = emptyList()
        var videoDevices: List<BindableDevice> = emptyList()

        val fetchCounter = AtomicInteger(2)

        val updateDevices: () -> Unit = {
            if (fetchCounter.decrementAndGet() == 0) {
                updateDeviceList(alarmDevices = alarmDevices, videoDevices = videoDevices)
            }
        }

        videoFetchDevices { devices ->
            videoDevices = devices
            updateDevices()
        }
        alarmFetchDevices { devices ->
            alarmDevices = devices
            updateDevices()
        }
    }


    private fun videoFetchDevices(callback: (List<BindableDevice>) -> Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://squadapps.ddns-intelbras.com.br:3000/video-devices/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(VideoApiService::class.java)
        val call = apiService.getvideoDevices()

        call.enqueue(object : Callback<VideoApiService.VideoDeviceResponse> {
            override fun onResponse(
                call: Call<VideoApiService.VideoDeviceResponse>,
                response: Response<VideoApiService.VideoDeviceResponse>
            ) {
                if (response.isSuccessful) {
                    val videoDeviceList = response.body()?.data?.map {
                        VideoDevices(
                            id = it.id,
                            name = it.name,
                            serial = it.serial,
                            username = it.username,
                            password = it.password
                        )
                    }.orEmpty()
                    callback(videoDeviceList)
                } else {
                    Log.e(
                        "MainActivity",
                        "Erro na resposta da API: ${response.code()} - ${response.message()}"
                    )
                    // Tratar erros da API
                }
            }

            override fun onFailure(call: Call<VideoApiService.VideoDeviceResponse>, t: Throwable) {
                // Tratar falhas de rede
                Log.e("MainActivity", "Falha na comunicação com a API", t)
            }
        })
    }

    private fun alarmFetchDevices(callback: (List<BindableDevice>) -> Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://squadapps.ddns-intelbras.com.br:3000/alarm-centrals/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(AlarmApiService::class.java)
        val call = apiService.getAlarmDevices()

        call.enqueue(object : Callback<AlarmApiService.AlarmDeviceResponse> {
            override fun onResponse(
                call: Call<AlarmApiService.AlarmDeviceResponse>,
                response: Response<AlarmApiService.AlarmDeviceResponse>
            ) {
                if (response.isSuccessful) {
                    val alarmDeviceList = response.body()?.data?.map {
                        AlarmDevices(
                            id = it.id,
                            name = it.name,
                            macAddress = it.macAddress,
                            password = it.password
                        )
                    }.orEmpty()
                    callback(alarmDeviceList)
                } else {
                    Log.e(
                        "MainActivity",
                        "Erro na resposta da API: ${response.code()} - ${response.message()}"
                    )
                    // Tratar erros da API
                }
            }

            override fun onFailure(call: Call<AlarmApiService.AlarmDeviceResponse>, t: Throwable) {
                // Tratar falhas de rede
                Log.e("MainActivity", "Falha na comunicação com a API", t)
            }
        })
    }

    private fun updateDeviceList(
        alarmDevices: List<BindableDevice> = emptyList(),
        videoDevices: List<BindableDevice> = emptyList()
    ) {
        allDevices.clear()
        allDevices.addAll(alarmDevices)
        allDevices.addAll(videoDevices)

        applyFilter()
    }

    private fun applyFilter() {
        val filteredList = when (currentFilter) {
            DeviceFilter.ALL -> allDevices
            DeviceFilter.FAVORITES -> TODO("Implementar filtragem de favoritos")
            DeviceFilter.VIDEO -> allDevices.filterIsInstance<VideoDevices>()
            DeviceFilter.ALARM -> allDevices.filterIsInstance<AlarmDevices>()
        }
        deviceAdapter.updateItems(filteredList)
    }

    private fun setFilter(filter: DeviceFilter) {
        currentFilter = filter
        updateBottomBarIcons()
        applyFilter()
    }

    private fun updateBottomBarIcons() {
        binding.imageAllDevices.setColorFilter(
            if (currentFilter == DeviceFilter.ALL) ContextCompat.getColor(this, R.color.green)
            else ContextCompat.getColor(this, R.color.light_gray)
        )
        binding.imageFavorites.setColorFilter(
            if (currentFilter == DeviceFilter.FAVORITES) ContextCompat.getColor(this, R.color.green)
            else ContextCompat.getColor(this, R.color.light_gray)
        )
        binding.imageVideoDevices.setColorFilter(
            if (currentFilter == DeviceFilter.VIDEO) ContextCompat.getColor(this, R.color.green)
            else ContextCompat.getColor(this, R.color.light_gray)
        )
        binding.imageAlarmDevices.setColorFilter(
            if (currentFilter == DeviceFilter.ALARM) ContextCompat.getColor(this, R.color.green)
            else ContextCompat.getColor(this, R.color.light_gray)
        )
    }

}