package com.vision.firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ServerValue
import com.vision.firebase.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth= FirebaseAuth.getInstance()
//
        binding.button.setOnClickListener {
            startActivity(Intent(this,SignUp::class.java))

        }

        binding.button2.setOnClickListener {
//            val userId = auth.currentUser?.uid
//            if (userId != null) {
//               startActivity(Intent(this,Home::class.java))
//                finish()
//            }else {
                startActivity(Intent(this, Login::class.java))
                finish()
//            }
        }
    }
}