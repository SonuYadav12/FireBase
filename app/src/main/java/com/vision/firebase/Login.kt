package com.vision.firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.vision.firebase.databinding.ActivityLoginBinding

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private var isPasswordVisible = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth=FirebaseAuth.getInstance()

        binding.signup.setOnClickListener {
            signInWithEmailAndPassword(binding.email.text.toString(),binding.password.text.toString())
        }


        binding.passwordToggle.setOnTouchListener { _, event ->
            handlePasswordToggle(event, binding.password)
            true
        }
    }


    private fun handlePasswordToggle(event: MotionEvent, editText: EditText) {
        val toggleIcon: Int
        val transformationMethod: android.text.method.TransformationMethod

        if (event.action == MotionEvent.ACTION_DOWN) {
            // Show password characters
            transformationMethod = HideReturnsTransformationMethod.getInstance()
            toggleIcon = R.drawable.eyeseen
        } else if (event.action == MotionEvent.ACTION_UP) {
            // Hide password characters
            transformationMethod = PasswordTransformationMethod.getInstance()
            toggleIcon = R.drawable.eyehide
        } else {
            return
        }

        editText.transformationMethod = transformationMethod

        // Set the appropriate eye icon for the corresponding password field
        if (editText == binding.password) {
            binding.passwordToggle.setImageResource(toggleIcon)
            isPasswordVisible = !isPasswordVisible
        }
    }


    private fun signInWithEmailAndPassword(email: String, password: String) {

              auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                  val user: FirebaseUser? =auth.currentUser
                  if(user?.email==email) {
                      if (user != null && user.email?.trim() == email.trim()) {
                          Toast.makeText(this, "Login Successful", Toast.LENGTH_LONG).show()
                          startActivity(Intent(this, Home::class.java))
                          finish()
                  }
              }
    }
              }
}