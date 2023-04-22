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

            // Cadastro de dispositivo de vídeo
            R.id.button_save_video -> {
                // Obtem as informações do dispositivo a partir dos campos de texto
                val name = findViewById<EditText>(R.id.edit_nomevideo).text.toString()
                val serial = findViewById<EditText>(R.id.edit_nsvideo).text.toString()
                val username = findViewById<EditText>(R.id.edit_usuariovideo).text.toString()
                val password = findViewById<EditText>(R.id.edit_senhavideo).text.toString()

                if (name.isNotEmpty() && serial.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()) {

                    // Cria um objeto JSON com as informações do dispositivo
                    val dispositivo = JSONObject()
                        .put("name", name)
                        .put("serial", serial)
                        .put("username", username)
                        .put("password", password)

                    // Chama a função para criar o dispositivo na API
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val response = criarDispositivo(dispositivo)
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
                                alert("Erro ao criar o dispositivo: ${e.message}")
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Há campos a serem preenchidos!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            R.id.button_save_alarm -> {
                // Implemente aqui o código para salvar o dispositivo de alarme
            }
        }
    }

    // Função para criar um novo dispositivo na API
    private fun criarDispositivo(dispositivo: JSONObject): String {
        val API_BASE_URL = "http://squadapps.ddns-intelbras.com.br:3000"
        val API_TOKEN =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjlhMmU0YTczLWNiODktNGUzZS1hMGE5LTYwODYxZDM3NWYwMSIsImlhdCI6MTY4MjAwODgzMSwiZXhwIjoxNjg0NjAwODMxfQ._WZkRusg8qj-kWeqVoFD3yVRdRneWmx7voHo2Jk7XU0"

        val client = OkHttpClient()

        // Define o corpo da requisição com o objeto JSON do dispositivo
        val body = dispositivo.toString().toRequestBody("application/json".toMediaType())

        // Cria a requisição HTTP POST com o token de autenticação no header
        val request = Request.Builder()
            .url("$API_BASE_URL/video-devices")
            .header("Authorization", "Bearer $API_TOKEN")
            .post(body)
            .build()

        // Envia a requisição e retorna o ID do dispositivo criado
        val response = client.newCall(request).execute()
        Log.d("API_RESPONSE", "Response: $response")
        if (!response.isSuccessful) {
            throw IOException("Erro ao criar o dispositivo: ${response.message}")
        }
        val responseBody = response.body?.string()
        val responseJson = JSONObject(responseBody)
        return responseJson.getString("id")
    }

    // Função para mostrar uma mensagem de alerta ao usuário
    private fun alert(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
            .setPositiveButton("OK", null)
        val alert = builder.create()
        alert.show()
    }
}
