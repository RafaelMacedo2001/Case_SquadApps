package com.example.motivation

import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.motivation.databinding.ActivityAddDeviceBinding
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class AddDeviceActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityAddDeviceBinding
    private lateinit var videoLayout: LinearLayout
    private lateinit var alarmLayout: LinearLayout
    private lateinit var saveVideo: Button
    private lateinit var saveAlarm: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        videoLayout = findViewById(R.id.video_layout)
        alarmLayout = findViewById(R.id.alarme_layout)
        saveVideo = findViewById(R.id.button_save_video)
        saveAlarm = findViewById(R.id.button_save_alarm)

        val videoButton = findViewById<Button>(R.id.button_video)
        val alarmButton = findViewById<Button>(R.id.button_alarm)
// Faz aparecer os itens relacionados a dispositivos de vídeo para preencher
        videoButton.setOnClickListener {
            videoLayout.visibility = View.VISIBLE
            alarmLayout.visibility = View.GONE
            videoButton.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
            alarmButton.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray))
        }
// Faz aparecer os itens relacionados a dispositivos de alarme para preencher
        alarmButton.setOnClickListener {
            videoLayout.visibility = View.GONE
            alarmLayout.visibility = View.VISIBLE
            alarmButton.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
            videoButton.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray))
        }

        saveVideo.setOnClickListener(this)
        saveAlarm.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.button_save_video -> {
                // Código para salvar o dispositivo de vídeo
                val namevideo = findViewById<EditText>(R.id.edit_nomevideo).text.toString()
                val serialvideo = findViewById<EditText>(R.id.edit_nsvideo).text.toString()
                val usernamevideo = findViewById<EditText>(R.id.edit_usuariovideo).text.toString()
                val passwordvideo = findViewById<EditText>(R.id.edit_senhavideo).text.toString()

                if (namevideo.isNotEmpty() && serialvideo.isNotEmpty() && usernamevideo.isNotEmpty() && passwordvideo.isNotEmpty()) {

                    // Cria um objeto JSON com as informações do dispositivo
                    val dispositivovideo = JSONObject()
                        .put("name", namevideo)
                        .put("serial", serialvideo)
                        .put("username", usernamevideo)
                        .put("password", passwordvideo)

                    // Chama a função para criar o dispositivo na API
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val response = criarDispositivovideo(dispositivovideo)
                            withContext(Dispatchers.Main) {
                                // Redireciona para a tela principal após a criação do dispositivo
                                startActivity(
                                    Intent(
                                        this@AddDeviceActivity,
                                        MainActivity::class.java
                                    )
                                )
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                // Mostra a mensagem de erro ao usuário
                                alertvideo("Erro ao criar o dispositivo: ${e.message}")
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Há campos a serem preenchidos!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            R.id.button_save_alarm -> {

                // Código para salvar o dispositivo de alarme
                val namealarm = findViewById<EditText>(R.id.edit_nomealarme).text.toString()
                val macalarm = findViewById<EditText>(R.id.edit_MAC).text.toString()
                val passwordalarm = findViewById<EditText>(R.id.edit_senhaalarme).text.toString()

                if (namealarm.isNotEmpty() && macalarm.isNotEmpty() && passwordalarm.isNotEmpty()) {

                    // Cria um objeto JSON com as informações do dispositivo
                    val dispositivoalarm = JSONObject()
                        .put("name", namealarm)
                        .put("macAddress", macalarm)
                        .put("password", passwordalarm)

                    // Chama a função para criar o dispositivo na API
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val responsealarm = criarDispositivoalarm(dispositivoalarm)
                            withContext(Dispatchers.Main) {
                                // Redireciona para a tela principal após a criação do dispositivo
                                startActivity(
                                    Intent(
                                        this@AddDeviceActivity,
                                        MainActivity::class.java
                                    )
                                )
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                // Mostra a mensagem de erro ao usuário
                                alertalarm("Erro ao criar o dispositivo: ${e.message}")
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Há campos a serem preenchidos!", Toast.LENGTH_SHORT)
                        .show()
                }

            }
        }
    }

    // Função para criar um novo dispositivo de vídeo na API
    private fun criarDispositivovideo(dispositivo: JSONObject): String {
        val API_BASE_URL_video = "http://squadapps.ddns-intelbras.com.br:3000"
        val API_TOKEN_video =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjlhMmU0YTczLWNiODktNGUzZS1hMGE5LTYwODYxZDM3NWYwMSIsImlhdCI6MTY4MjAwODgzMSwiZXhwIjoxNjg0NjAwODMxfQ._WZkRusg8qj-kWeqVoFD3yVRdRneWmx7voHo2Jk7XU0"

        val clientvideo = OkHttpClient()

        // Define o corpo da requisição com o objeto JSON do dispositivo
        val bodyvideo = dispositivo.toString().toRequestBody("application/json".toMediaType())

        // Cria a requisição HTTP POST com o token de autenticação no header
        val requestvideo = Request.Builder()
            .url("$API_BASE_URL_video/video-devices")
            .header("Authorization", "Bearer $API_TOKEN_video")
            .post(bodyvideo)
            .build()
        Log.d("API_REQUEST", "Request: $requestvideo")

        // Envia a requisição e retorna o ID do dispositivo criado
        val responsevideo = clientvideo.newCall(requestvideo).execute()
        Log.d("API_RESPONSE", "Response: $responsevideo")
        if (!responsevideo.isSuccessful) {
            throw IOException("Erro ao criar o dispositivo: ${responsevideo.message}")
        }
        val responseBodyvideo = responsevideo.body?.string()
        val responseJsonvideo = JSONObject(responseBodyvideo)
        return responseJsonvideo.getString("id")
    }
    // Função para criar um novo dispositivo de alarme na API
    private fun criarDispositivoalarm(dispositivo: JSONObject): String {
        val API_BASE_URL_alarm = "http://squadapps.ddns-intelbras.com.br:3000"
        val API_TOKEN_alarm =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjlhMmU0YTczLWNiODktNGUzZS1hMGE5LTYwODYxZDM3NWYwMSIsImlhdCI6MTY4MjAwODgzMSwiZXhwIjoxNjg0NjAwODMxfQ._WZkRusg8qj-kWeqVoFD3yVRdRneWmx7voHo2Jk7XU0"

        val clientalarm = OkHttpClient()

        // Define o corpo da requisição com o objeto JSON do dispositivo
        val bodyalarm = dispositivo.toString().toRequestBody("application/json".toMediaType())

        // Cria a requisição HTTP POST com o token de autenticação no header
        val requestalarm = Request.Builder()
            .url("$API_BASE_URL_alarm/alarm-centrals")
            .header("Authorization", "Bearer $API_TOKEN_alarm")
            .post(bodyalarm)
            .build()
        Log.d("API_REQUEST", "Request: $requestalarm")

        // Envia a requisição e retorna o ID do dispositivo criado
        val responsealarm = clientalarm.newCall(requestalarm).execute()
        Log.d("API_RESPONSE", "Response: $responsealarm")
        if (!responsealarm.isSuccessful) {
            throw IOException("Erro ao criar o dispositivo: ${responsealarm.message}")
        }
        val responseBodyalarm = responsealarm.body?.string()
        val responseJsonalarm = JSONObject(responseBodyalarm)
        return responseJsonalarm.getString("id")

    }

        // Função para mostrar uma mensagem de alerta video ao usuário
    private fun alertvideo(message: String) {
        val buildervideo = AlertDialog.Builder(this)
        buildervideo.setMessage(message)
            .setPositiveButton("OK", null)
        val alert = buildervideo.create()
        alert.show()
    }

    // Função para mostrar uma mensagem de alerta alarme ao usuário
    private fun alertalarm(message: String) {
        val builderalarm = AlertDialog.Builder(this)
        builderalarm.setMessage(message)
            .setPositiveButton("OK", null)
        val alertalarm = builderalarm.create()
        alertalarm.show()
    }
}
