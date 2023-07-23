package com.vision.firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vision.firebase.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//
        binding.button.setOnClickListener {
            startActivity(Intent(this,SignUp::class.java))
            finish()
        }

        binding.button2.setOnClickListener {
            startActivity(Intent(this,Login::class.java))
            finish()
        }
    }
}