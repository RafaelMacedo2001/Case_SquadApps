package com.example.motivation

import ApiService
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

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private val itemAdapter = ItemAdapter(mutableListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.Fab.setOnClickListener(this)

        // Set up RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = itemAdapter

        // Fetch devices and update the list
        fetchDevices()
    }

    override fun onClick(view: View) {
        if (view.id == R.id.Fab) {
            addNewDevice()
        }
    }

    private fun addNewDevice() {
        startActivity(Intent(this, AddDeviceActivity::class.java))
    }

    private fun fetchDevices() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://squadapps.ddns-intelbras.com.br:3000/video-devices/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.getvideoDevices()

        call.enqueue(object : Callback<ApiService.DeviceResponse> {
            override fun onResponse(call: Call<ApiService.DeviceResponse>, response: Response<ApiService.DeviceResponse>) {
                if (response.isSuccessful) {
                    val deviceList = response.body()?.data?.map {
                        Item(name = it.name)
                    }.orEmpty()
                    itemAdapter.updateItems(deviceList)
                    Log.d("MainActivity", "Dispositivos recebidos: $deviceList")
                } else {
                    Log.e("MainActivity", "Erro na resposta da API: ${response.code()} - ${response.message()}")
                    // Tratar erros da API
                }
            }

            override fun onFailure(call: Call<ApiService.DeviceResponse>, t: Throwable) {
                // Tratar falhas de rede
                Log.e("MainActivity", "Falha na comunicação com a API", t)
            }
        })
    }
}