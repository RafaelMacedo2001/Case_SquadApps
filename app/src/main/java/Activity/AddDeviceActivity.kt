package Activity

import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.motivation.R
import com.example.motivation.databinding.ActivityAddDeviceBinding
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class AddDeviceActivity : AppCompatActivity(), View.OnClickListener {

    val API_BASE_URL = "http://squadapps.ddns-intelbras.com.br:3000"
    val API_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjlhMmU0YTczLWNiODktNGUzZS1hMGE5L" +
                    "TYwODYxZDM3NWYwMSIsImlhdCI6MTY4MjAwODgzMSwiZXhwIjoxNjg0NjAwODMxfQ._WZkRusg8qj-" +
                    "kWeqVoFD3yVRdRneWmx7voHo2Jk7XU0"

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

    fun showToast(message: String) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.CENTER, 0, 0)
        val textView = toast.view?.findViewById(android.R.id.message) as? TextView
        textView?.maxLines = 5 // limita o número de linhas
        textView?.gravity = Gravity.CENTER
        toast.show()
    }

    fun isValidSerial(serial: String): Boolean {
        return serial.length == 13 && serial.all { it.isLetterOrDigit() }
    }

    fun isValidPassword(password: String): Boolean {
        return password.length in 4..6 && password.all { it.isDigit() }
    }

    fun isValidMac(mac: String): Boolean {
        return mac.length == 12 && mac.all { it.isDigit() || it.isLetter() && it.uppercaseChar() <= 'F' }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button_save_video -> {
                val namevideo = binding.editNomevideo.text.toString()
                val serialvideo = binding.editNsvideo.text.toString()
                val usernamevideo = binding.editUsuariovideo.text.toString()
                val passwordvideo = binding.editSenhavideo.text.toString()

                if (namevideo.isNotEmpty() && usernamevideo.isNotEmpty() && passwordvideo.isNotEmpty()) {
                    if (isValidSerial(serialvideo)) {
                        val dispositivovideo = JSONObject()
                            .put("name", namevideo)
                            .put("serial", serialvideo)
                            .put("username", usernamevideo)
                            .put("password", passwordvideo)

                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                criarDispositivovideo(dispositivovideo)
                                withContext(Dispatchers.Main) {
                                    startActivity(
                                        Intent(
                                            this@AddDeviceActivity,
                                            MainActivity::class.java
                                        )
                                    )
                                    notifyDeviceAdded()
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    alertvideo("Erro ao criar o dispositivo: ${e.message}")
                                    Log.e(TAG, "Erro ao criar o dispositivo: ${e.message}")
                                }
                            }
                        }
                    } else {
                        showToast("O campo serial deve conter 13 caracteres com letras e números. Confira o serial do seu dispositivo na etiqueta.")
                    }
                } else {
                    Toast.makeText(this, "Há campos a serem preenchidos!", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            R.id.button_save_alarm -> {
                val namealarm = binding.editNomealarme.text.toString()
                val macalarm = binding.editMAC.text.toString()
                val passwordalarm = binding.editSenhaalarme.text.toString()

                if (namealarm.isNotEmpty() && macalarm.isNotEmpty()) {
                    if (isValidMac(macalarm)) {
                        if (isValidPassword(passwordalarm)) {
                            val dispositivoalarm = JSONObject()
                                .put("name", namealarm)
                                .put("macAddress", macalarm)
                                .put("password", passwordalarm)

                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    criarDispositivoalarm(dispositivoalarm)
                                    withContext(Dispatchers.Main) {
                                        startActivity(
                                            Intent(this@AddDeviceActivity, MainActivity::class.java)
                                        )
                                        notifyDeviceAdded()
                                    }
                                } catch (e: Exception) {
                                    Log.e(TAG, "Erro ao criar o dispositivo: ${e.message}")
                                    withContext(Dispatchers.Main) {
                                        alertalarm("Erro ao criar o dispositivo: ${e.message}")
                                    }
                                }
                            }
                        } else {
                            showToast("A senha deve conter entre 4 e 6 números.")
                        }
                    } else {
                        showToast("MAC deve conter 12 caracteres, sendo números e letras até 'F'. Confira o MAC do seu dispositivo na etiqueta.")

                    }
                } else {
                    Toast.makeText(this, "Há campos a serem preenchidos!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun notifyDeviceAdded() {
        Toast.makeText(this, "Dispositivo adicionado com sucesso!", Toast.LENGTH_LONG).show()
    }

    private fun criarDispositivovideo(dispositivo: JSONObject): String {

        val clientvideo = OkHttpClient()
        val bodyvideo = dispositivo.toString().toRequestBody("application/json".toMediaType())

        val requestvideo = Request.Builder()
            .url("$API_BASE_URL/video-devices")
            .header("Authorization", "Bearer $API_TOKEN")
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

        val clientalarm = OkHttpClient()
        val bodyalarm = dispositivo.toString().toRequestBody("application/json".toMediaType())

        val requestalarm = Request.Builder()
            .url("$API_BASE_URL/alarm-centrals")
            .header("Authorization", "Bearer $API_TOKEN")
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

