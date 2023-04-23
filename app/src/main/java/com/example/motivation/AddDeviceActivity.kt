package com.example.motivation

import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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

    companion object {
        private const val TAG = "AddDeviceActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        videoLayout = findViewById(R.id.video_layout)
        alarmLayout = findViewById(R.id.alarme_layout)

        binding.buttonVideo.setOnClickListener {
            videoLayout.visibility = View.VISIBLE
            alarmLayout.visibility = View.GONE
            it.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
            binding.buttonAlarm.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray))
        }

        binding.buttonAlarm.setOnClickListener {
            videoLayout.visibility = View.GONE
            alarmLayout.visibility = View.VISIBLE
            it.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
            binding.buttonVideo.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray))
        }

        binding.buttonSaveVideo.setOnClickListener(this)
        binding.buttonSaveAlarm.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button_save_video -> {
                val namevideo = binding.editNomevideo.text.toString()
                val serialvideo = binding.editNsvideo.text.toString()
                val usernamevideo = binding.editUsuariovideo.text.toString()
                val passwordvideo = binding.editSenhavideo.text.toString()

                if (namevideo.isNotEmpty() && serialvideo.isNotEmpty() && usernamevideo.isNotEmpty() && passwordvideo.isNotEmpty()) {
                    val dispositivovideo = JSONObject()
                        .put("name", namevideo)
                        .put("serial", serialvideo)
                        .put("username", usernamevideo)
                        .put("password", passwordvideo)

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            criarDispositivovideo(dispositivovideo)
                            withContext(Dispatchers.Main) {
                                startActivity(Intent(this@AddDeviceActivity, MainActivity::class.java))
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                alertvideo("Erro ao criar o dispositivo: ${e.message}")
                                Log.e(TAG, "Erro ao criar o dispositivo: ${e.message}")
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Há campos a serem preenchidos!", Toast.LENGTH_SHORT).show()
                }
            }

            R.id.button_save_alarm -> {
                val namealarm = binding.editNomealarme.text.toString()
                val macalarm = binding.editMAC.text.toString()
                val passwordalarm = binding.editSenhaalarme.text.toString()

                if (namealarm.isNotEmpty() && macalarm.isNotEmpty() && passwordalarm.isNotEmpty()) {
                    val dispositivoalarm = JSONObject()
                        .put("name", namealarm)
                        .put("macAddress", macalarm)
                        .put("password", passwordalarm)

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            criarDispositivoalarm(dispositivoalarm)
                            withContext(Dispatchers.Main) {
                                startActivity(Intent(this@AddDeviceActivity, MainActivity::class.java))
                            }
                        }catch (e: Exception) {
                            Log.e(TAG, "Erro ao criar o dispositivo: ${e.message}")
                            withContext(Dispatchers.Main) {
                                alertvideo("Erro ao criar o dispositivo: ${e.message}")
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Há campos a serem preenchidos!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun criarDispositivovideo(dispositivo: JSONObject): String {
        val API_BASE_URL_VIDEO = "http://squadapps.ddns-intelbras.com.br:3000"
        val API_TOKEN_VIDEO = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjlhMmU0YTczLWNiODktNGUzZS1hMGE5LTYwODYxZDM3NWYwMSIsImlhdCI6MTY4MjAwODgzMSwiZXhwIjoxNjg0NjAwODMxfQ._WZkRusg8qj-kWeqVoFD3yVRdRneWmx7voHo2Jk7XU0"

        val clientvideo = OkHttpClient()
        val bodyvideo = dispositivo.toString().toRequestBody("application/json".toMediaType())

        val requestvideo = Request.Builder()
            .url("$API_BASE_URL_VIDEO/video-devices")
            .header("Authorization", "Bearer $API_TOKEN_VIDEO")
            .post(bodyvideo)
            .build()

        val responsevideo = clientvideo.newCall(requestvideo).execute()
        if (!responsevideo.isSuccessful) {
            throw IOException("Erro ao criar o dispositivo: ${responsevideo.message}")
        }
        val responseBodyvideo = responsevideo.body?.string()
        val responseJsonvideo = JSONObject(responseBodyvideo)
        return responseJsonvideo.getString("id")
    }

    private fun criarDispositivoalarm(dispositivo: JSONObject): String {
        val API_BASE_URL_ALARM = "http://squadapps.ddns-intelbras.com.br:3000"
        val API_TOKEN_ALARM = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjlhMmU0YTczLWNiODktNGUzZS1hMGE5LTYwODYxZDM3NWYwMSIsImlhdCI6MTY4MjAwODgzMSwiZXhwIjoxNjg0NjAwODMxfQ._WZkRusg8qj-kWeqVoFD3yVRdRneWmx7voHo2Jk7XU0"

        val clientalarm = OkHttpClient()
        val bodyalarm = dispositivo.toString().toRequestBody("application/json".toMediaType())

        val requestalarm = Request.Builder()
            .url("$API_BASE_URL_ALARM/alarm-centrals")
            .header("Authorization", "Bearer $API_TOKEN_ALARM")
            .post(bodyalarm)
            .build()

        val responsealarm = clientalarm.newCall(requestalarm).execute()
        if (!responsealarm.isSuccessful) {
            throw IOException("Erro ao criar o dispositivo: ${responsealarm.message}")
        }
        val responseBodyalarm = responsealarm.body?.string()
        val responseJsonalarm = JSONObject(responseBodyalarm)
        return responseJsonalarm.getString("id")
    }

    private fun alertvideo(message: String) {
        val buildervideo = AlertDialog.Builder(this)
        buildervideo.setMessage(message)
            .setPositiveButton("OK", null)
        val alert = buildervideo.create()
        alert.show()
    }

    private fun alertalarm(message: String) {
        val builderalarm = AlertDialog.Builder(this)
        builderalarm.setMessage(message)
            .setPositiveButton("OK", null)
        val alertalarm = builderalarm.create()
        alertalarm.show()
    }
}

