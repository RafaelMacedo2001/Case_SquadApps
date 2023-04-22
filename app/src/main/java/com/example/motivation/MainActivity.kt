package com.example.motivation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.motivation.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.Fab.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        if (view.id == R.id.Fab){
            addNewDevice()
        }
    }

    private fun addNewDevice() {
        startActivity(Intent(this, AddDeviceActivity::class.java))
    }

}